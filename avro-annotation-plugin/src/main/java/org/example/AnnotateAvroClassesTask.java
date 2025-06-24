package org.example;

import org.apache.avro.Schema;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithPublicModifier;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import com.github.javaparser.javadoc.description.JavadocDescription;
import com.github.javaparser.ast.NodeList;

public class AnnotateAvroClassesTask extends DefaultTask {
    private File inputDir;
    private File outputDir;
    private File schemaFile;

    @InputDirectory
    public File getInputDir() { return inputDir; }
    public void setInputDir(File inputDir) { this.inputDir = inputDir; }

    @OutputDirectory
    public File getOutputDir() { return outputDir; }
    public void setOutputDir(File outputDir) { this.outputDir = outputDir; }

    @InputFile
    public File getSchemaFile() { return schemaFile; }
    public void setSchemaFile(File schemaFile) { this.schemaFile = schemaFile; }

    @TaskAction
    public void annotate() throws IOException {
        if (inputDir == null || outputDir == null || schemaFile == null) {
            throw new IllegalArgumentException("inputDir, outputDir, and schemaFile must be set");
        }
        Schema schema = new Schema.Parser().parse(schemaFile);
        Files.walk(inputDir.toPath())
                .filter(path -> path.toString().endsWith(".java"))
                .forEach(path -> {
                    try {
                        processGeneratedClass(path.toFile(), schema, inputDir.toPath(), outputDir.toPath());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        getLogger().lifecycle("Annotated Avro classes in {} using schema {}", inputDir, schemaFile);
    }

    private static final String NOT_NULL_ANNOTATION = "org.jetbrains.annotations.NotNull";
    private static final String NULLABLE_ANNOTATION = "org.jetbrains.annotations.Nullable";
    private static final String DEPRECATED_ANNOTATION = "java.lang.Deprecated";
    private static final String BUILD_METHOD_NAME = "build";
    private static final String NEW_BUILD_METHOD_NAME = "newBuilder";

    private static void processGeneratedClass(File javaFile, Schema avroSchema, Path inputRoot, Path outputRoot) throws IOException {
        try (FileInputStream in = new FileInputStream(javaFile)) {
            JavaParser javaParser = new JavaParser();
            CompilationUnit cu = javaParser.parse(in).getResult().orElseThrow(() -> new RuntimeException("Failed to parse Java file"));
            cu.addImport(NOT_NULL_ANNOTATION);
            cu.addImport(NULLABLE_ANNOTATION);
            cu.addImport(DEPRECATED_ANNOTATION);
            cu.findAll(ConstructorDeclaration.class).stream()
                    .filter(NodeWithPublicModifier::isPublic)
                    .filter(constructor -> !hasAnnotation(constructor.getAnnotations(), DEPRECATED_ANNOTATION))
                    .forEach(constructor -> {
                        constructor.addAnnotation(new MarkerAnnotationExpr(DEPRECATED_ANNOTATION));
                        Javadoc newJavadoc = constructor.getJavadoc().orElseGet(() -> new Javadoc(JavadocDescription.parseText("")));
                        newJavadoc.addBlockTag(new JavadocBlockTag("deprecated", "Do not use this constructor, use .newBuilder() instead"));
                        constructor.setJavadocComment(newJavadoc);
                    });

            List<FieldDeclaration> fields = cu.findAll(FieldDeclaration.class);
            for (FieldDeclaration field : fields) {
                String fieldName = field.getVariable(0).getNameAsString();
                Schema.Field avroField = avroSchema.getField(fieldName);
                if (avroField != null) {
                    boolean isNullable = isNullable(avroField.schema());
                    addNullabilityAnnotation(field, isNullable);
                    if (!(field.getParentNode().isPresent() && field.getParentNode().get().getParentNode().isPresent() && field.getParentNode().get().getParentNode().get().getClass().getSimpleName().equals("ClassOrInterfaceDeclaration"))) {
                        String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        cu.findAll(MethodDeclaration.class).stream()
                            .filter(m -> m.getNameAsString().equals(getterName) && m.getParameters().isEmpty())
                            .forEach(m -> addNullabilityAnnotationToMethod(m, isNullable));
                        String clearerName = "clear" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        cu.findAll(MethodDeclaration.class).stream()
                                .filter(m -> m.getNameAsString().equals(clearerName) && m.getParameters().isEmpty())
                                .forEach(m -> addNullabilityAnnotationToMethod(m, false));
                        String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        cu.findAll(MethodDeclaration.class).stream()
                            .filter(m -> m.getNameAsString().equals(setterName) && m.getParameters().size() == 1)
                            .forEach(m -> {
                                Parameter param = m.getParameter(0);
                                addNullabilityAnnotationToParameter(param, isNullable);
                                addNullabilityAnnotationToMethod(m, false);
                            });
                    }
                }
            }

            cu.findAll(MethodDeclaration.class).stream()
                    .filter(m -> m.getNameAsString().equals(BUILD_METHOD_NAME) && m.getParameters().isEmpty())
                    .forEach(m -> addNullabilityAnnotationToMethod(m, false));
            cu.findAll(MethodDeclaration.class).stream()
                    .filter(m -> m.getNameAsString().equals(NEW_BUILD_METHOD_NAME))
                    .forEach(m -> addNullabilityAnnotationToMethod(m, false));
            cu.findAll(MethodDeclaration.class).stream()
                    .filter(m -> m.getNameAsString().equals(NEW_BUILD_METHOD_NAME) && !m.getParameters().isEmpty())
                    .forEach(m -> {
                        Parameter param = m.getParameter(0);
                        addNullabilityAnnotationToParameter(param, true);
                    });

            // Write to output directory, preserving relative path
            Path relativePath = inputRoot.relativize(javaFile.toPath());
            Path outputPath = outputRoot.resolve(relativePath);
            Files.createDirectories(outputPath.getParent());
            try (FileWriter writer = new FileWriter(outputPath.toFile())) {
                writer.write(cu.toString());
            }
        }
    }

    private static boolean isNullable(Schema schema) {
        if (schema.getType() == Schema.Type.UNION) {
            return schema.getTypes().stream().anyMatch(type -> type.getType() == Schema.Type.NULL);
        }
        return false;
    }

    private static void addNullabilityAnnotation(FieldDeclaration field, boolean isNullable) {
        String annotationName = isNullable ? NULLABLE_ANNOTATION : NOT_NULL_ANNOTATION;
        if (!hasAnnotation(field.getAnnotations(), annotationName)) {
            field.addAnnotation(new MarkerAnnotationExpr(annotationName));
        }
    }

    private static void addNullabilityAnnotationToMethod(MethodDeclaration method, boolean isNullable) {
        String annotationName = isNullable ? NULLABLE_ANNOTATION : NOT_NULL_ANNOTATION;
        if (!hasAnnotation(method.getAnnotations(), annotationName)) {
            method.addAnnotation(new MarkerAnnotationExpr(annotationName));
        }
    }

    private static void addNullabilityAnnotationToParameter(Parameter parameter, boolean isNullable) {
        String annotationName = isNullable ? NULLABLE_ANNOTATION : NOT_NULL_ANNOTATION;
        if (!hasAnnotation(parameter.getAnnotations(), annotationName)) {
            parameter.addAnnotation(new MarkerAnnotationExpr(annotationName));
        }
    }

    private static boolean hasAnnotation(NodeList<AnnotationExpr> annotations, String annotationName) {
        return annotations.stream().anyMatch(annotation -> annotation.getNameAsString().equals(annotationName));
    }
} 
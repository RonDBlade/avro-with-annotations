package org.example;

import com.github.javaparser.HasParentNode;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithPublicModifier;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import com.github.javaparser.javadoc.description.JavadocDescription;
import org.apache.avro.Schema;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class AvroClassProcessor {
    private static final String NOT_NULL_ANNOTATION = "org.jetbrains.annotations.NotNull";
    private static final String NULLABLE_ANNOTATION = "org.jetbrains.annotations.Nullable";
    private static final String DEPRECATED_ANNOTATION = "java.lang.Deprecated";
    private static final String BUILD_METHOD_NAME = "build";
    private static final String NEW_BUILD_METHOD_NAME = "newBuilder";


    /**
     * Processes a generated Java class file to add nullability annotations to fields, getter methods, and setter methods.
     * This method reads the Java file, parses it using JavaParser, and then adds appropriate annotations based on the Avro schema.
     * It annotates fields and their corresponding getter and setter methods with @NotNull or @Nullable based on the field's nullability in the Avro schema.
     * Additionally, it marks public constructors as deprecated and updates their Javadoc.
     *
     * @param javaFile The Java file to process.
     * @throws IOException If an I/O error occurs while reading or writing the file.
     */
    public static void processGeneratedClass(File javaFile) throws IOException {
        System.out.println("processing file: " + javaFile.getName());
        try (FileInputStream in = new FileInputStream(javaFile)) {
            JavaParser javaParser = new JavaParser();
            CompilationUnit cu = javaParser.parse(in).getResult().orElseThrow(() -> new RuntimeException("Failed to parse Java file"));

            if (cu.getTypes().get(0).isEnumDeclaration()) {
                // Ignore enums, nothing to annotate there.
                return;
            }

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

            Schema avroSchema = extractSchema(cu);

            List<FieldDeclaration> fields = cu.findAll(FieldDeclaration.class);
            for (FieldDeclaration field : fields) {
                String fieldName = field.getVariable(0).getNameAsString();
                boolean isTopLevelField = isTopLevel(field);

                if (isTopLevelField) {
                    Schema.Field avroField = avroSchema.getField(fieldName);
                    if (avroField != null) {
                        // Only working for top level fields that are written in the schema file
                        boolean isNullable = isNullableAccordingToSchema(avroField.schema());
                        addNullabilityAnnotation(field, isNullable);

                        String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        cu.findAll(MethodDeclaration.class).stream()
                                .filter(m -> m.getNameAsString().equals(getterName) && m.getParameters().isEmpty())
                                .filter(AvroClassProcessor::isTopLevel)
                                .forEach(m -> addNullabilityAnnotationToMethod(m, isNullable));

                        String clearerName = "clear" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        cu.findAll(MethodDeclaration.class).stream()
                                .filter(m -> m.getNameAsString().equals(clearerName) && m.getParameters().isEmpty())
                                .filter(AvroClassProcessor::isTopLevel)
                                .forEach(m -> addNullabilityAnnotationToMethod(m, false));

                        // Annotate Builder setter method parameter
                        String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        cu.findAll(MethodDeclaration.class).stream()
                                .filter(m -> m.getNameAsString().equals(setterName) && m.getParameters().size() == 1)
                                .filter(AvroClassProcessor::isTopLevel)
                                .forEach(m -> {
                                    Parameter param = m.getParameter(0);
                                    addNullabilityAnnotationToParameter(param, isNullable);
                                });
                    }
                } else {
                    boolean isNullable = !field.getCommonType().isPrimitiveType();
                    addNullabilityAnnotation(field, isNullable);

                    String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    cu.findAll(MethodDeclaration.class).stream()
                            .filter(m -> m.getNameAsString().equals(getterName) && m.getParameters().isEmpty())
                            .filter(m1 -> !isTopLevel(m1))
                            .forEach(m -> addNullabilityAnnotationToMethod(m, isNullable));

                    String clearerName = "clear" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    cu.findAll(MethodDeclaration.class).stream()
                            .filter(m -> m.getNameAsString().equals(clearerName) && m.getParameters().isEmpty())
                            .filter(m1 -> !isTopLevel(m1))
                            .forEach(m -> addNullabilityAnnotationToMethod(m, isNullable));

                    // Annotate Builder setter method parameter
                    String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    cu.findAll(MethodDeclaration.class).stream()
                            .filter(m -> m.getNameAsString().equals(setterName) && m.getParameters().size() == 1)
                            .filter(m1 -> !isTopLevel(m1))
                            .forEach(m -> {
                                Parameter param = m.getParameter(0);
                                addNullabilityAnnotationToParameter(param, isNullable);
                                // Annotate the builders setter method itself with @NotNull
                                addNullabilityAnnotationToMethod(m, false);
                            });
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

            try (FileWriter writer = new FileWriter(javaFile)) {
                writer.write(cu.toString());
            }
        }
    }

    private static boolean isTopLevel(HasParentNode<?> m) {
        TypeDeclaration<?> typeDeclaration = m.findAncestor(TypeDeclaration.class).get();
        return !typeDeclaration.isNestedType();
    }

    private static Schema extractSchema(CompilationUnit cu) {
        String fieldValue = extractStaticFieldValue(cu, "SCHEMA$").get();
        String schemaStructure = fieldValue.substring(fieldValue.lastIndexOf('(') + 2, fieldValue.lastIndexOf(')') - 1);
        String usableSchemaString = schemaStructure.replace("\\", "");
        return new org.apache.avro.Schema.Parser().parse(usableSchemaString);
    }

    public static Optional<String> extractStaticFieldValue(CompilationUnit cu, String fieldName) {
        return cu.findAll(FieldDeclaration.class).stream()
                .filter(field -> field.isStatic()) // Only static fields
                .flatMap(field -> field.getVariables().stream())
                .filter(var -> var.getNameAsString().equals(fieldName))
                .map(VariableDeclarator::getInitializer)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Node::toString)
                .findFirst();
    }


    private static boolean isNullableAccordingToSchema(Schema schema) {
        if (schema.getType() == Schema.Type.UNION) {
            return schema.getTypes().stream().anyMatch(type -> type.getType() == Schema.Type.NULL);
        }
        return false;
    }

    private static void addNullabilityAnnotation(FieldDeclaration field, boolean isNullable) {
        String annotationName = isNullable ? NULLABLE_ANNOTATION : NOT_NULL_ANNOTATION;
        field.getAnnotations().stream().forEach(annotation -> System.out.println(annotation.getNameAsString()));
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

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage: AvroClassProcessor <generatedJavaDir> <schemaFile>");
            System.exit(1);
        }
        String generatedClassesDir = args[0];
        Files.walk(Paths.get(generatedClassesDir))
                .filter(path -> path.toString().endsWith(".java"))
                .forEach(path -> {
                    try {
                        processGeneratedClass(path.toFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
} 
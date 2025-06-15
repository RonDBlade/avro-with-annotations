package org.example;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
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

@Deprecated
public class AvroClassProcessor {
    private static final String NOT_NULL_ANNOTATION = "org.jetbrains.annotations.NotNull";
    private static final String NULLABLE_ANNOTATION = "org.jetbrains.annotations.Nullable";
    private static final String DEPRECATED_ANNOTATION = "java.lang.Deprecated";

    /**
     * Processes a generated Java class file to add nullability annotations to fields, getter methods, and setter methods.
     * This method reads the Java file, parses it using JavaParser, and then adds appropriate annotations based on the Avro schema.
     * It annotates fields and their corresponding getter and setter methods with @NotNull or @Nullable based on the field's nullability in the Avro schema.
     * Additionally, it marks public constructors as deprecated and updates their Javadoc.
     *
     * @param javaFile The Java file to process.
     * @param avroSchema The Avro schema used to determine field nullability.
     * @throws IOException If an I/O error occurs while reading or writing the file.
     */
    public static void processGeneratedClass(File javaFile, Schema avroSchema) throws IOException {
        try (FileInputStream in = new FileInputStream(javaFile)) {
            JavaParser javaParser = new JavaParser();
            CompilationUnit cu = javaParser.parse(in).getResult().orElseThrow(() -> new RuntimeException("Failed to parse Java file"));
            cu.addImport(NOT_NULL_ANNOTATION);
            cu.addImport(NULLABLE_ANNOTATION);
            cu.addImport(DEPRECATED_ANNOTATION);
            cu.findAll(ConstructorDeclaration.class).stream()
                    .filter(NodeWithPublicModifier::isPublic)
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
                    // Annotate getter method only if the field is not part of an inner class
                    if (!(field.getParentNode().isPresent() && field.getParentNode().get().getParentNode().isPresent() && field.getParentNode().get().getParentNode().get().getClass().getSimpleName().equals("ClassOrInterfaceDeclaration"))) {
                        String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        cu.findAll(MethodDeclaration.class).stream()
                            .filter(m -> m.getNameAsString().equals(getterName) && m.getParameters().isEmpty())
                            .forEach(m -> addNullabilityAnnotationToMethod(m, isNullable));
                        // Annotate Builder setter method parameter
                        String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        cu.findAll(MethodDeclaration.class).stream()
                            .filter(m -> m.getNameAsString().equals(setterName) && m.getParameters().size() == 1)
                            .forEach(m -> {
                                Parameter param = m.getParameter(0);
                                addNullabilityAnnotationToParameter(param, isNullable);
                                // Annotate the setter method itself with @NotNull
                                addNullabilityAnnotationToMethod(m, false);
                            });
                    }
                }
            }
            try (FileWriter writer = new FileWriter(javaFile)) {
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
        field.addAnnotation(new MarkerAnnotationExpr(annotationName));
    }

    private static void addNullabilityAnnotationToMethod(MethodDeclaration method, boolean isNullable) {
        String annotationName = isNullable ? NULLABLE_ANNOTATION : NOT_NULL_ANNOTATION;
        method.addAnnotation(new MarkerAnnotationExpr(annotationName));
    }

    private static void addNullabilityAnnotationToParameter(Parameter parameter, boolean isNullable) {
        String annotationName = isNullable ? NULLABLE_ANNOTATION : NOT_NULL_ANNOTATION;
        parameter.addAnnotation(new MarkerAnnotationExpr(annotationName));
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: AvroClassProcessor <generatedJavaDir> <schemaFile>");
            System.exit(1);
        }
        String generatedClassesDir = args[0];
        String schemaPath = args[1];
        Schema schema = new Schema.Parser().parse(new File(schemaPath));
        Files.walk(Paths.get(generatedClassesDir))
                .filter(path -> path.toString().endsWith(".java"))
                .forEach(path -> {
                    try {
                        processGeneratedClass(path.toFile(), schema);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
} 
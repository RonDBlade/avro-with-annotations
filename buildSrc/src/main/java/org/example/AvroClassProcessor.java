package org.example;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import org.apache.avro.Schema;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class AvroClassProcessor {
    private static final String NOT_NULL_ANNOTATION = "org.jetbrains.annotations.NotNull";
    private static final String NULLABLE_ANNOTATION = "org.jetbrains.annotations.Nullable";

    public static void processGeneratedClass(File javaFile, Schema avroSchema) throws IOException {
        try (FileInputStream in = new FileInputStream(javaFile)) {
            JavaParser javaParser = new JavaParser();
            CompilationUnit cu = javaParser.parse(in).getResult().orElseThrow(() -> new RuntimeException("Failed to parse Java file"));
            cu.addImport(NOT_NULL_ANNOTATION);
            cu.addImport(NULLABLE_ANNOTATION);
            List<FieldDeclaration> fields = cu.findAll(FieldDeclaration.class);
//            System.out.println(fields);
            for (FieldDeclaration field : fields) {
                System.out.println("variables: " + field.getVariables());
                String fieldName = field.getVariable(0).getNameAsString();
                System.out.println(fieldName);
                Schema.Field avroField = avroSchema.getField(fieldName);
                if (avroField != null) {
                    boolean isNullable = isNullable(avroField.schema());
                    addNullabilityAnnotation(field, isNullable);
                    // Annotate getter method
                    List<String> methodsAddedAnnot = new java.util.ArrayList<>();
                    String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    cu.findAll(MethodDeclaration.class).stream()
                        .filter(m -> m.getNameAsString().equals(getterName) && m.getParameters().isEmpty())
                            .peek(m -> methodsAddedAnnot.add(m.getNameAsString()))
                        .forEach(m -> addNullabilityAnnotationToMethod(m, isNullable));

                    System.out.println(methodsAddedAnnot);
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
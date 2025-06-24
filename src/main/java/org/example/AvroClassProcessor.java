package org.example;

import com.github.javaparser.HasParentNode;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithPublicModifier;
import com.github.javaparser.ast.type.Type;
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
import java.util.Set;

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
     * @param javaFile   The Java file to process.
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
                if (isTopLevel(field)) {
                    Schema.Field avroField = avroSchema.getField(fieldName);
                    if (avroField != null) {
                        boolean isNullable = isNullable(avroField.schema());
                        addNullabilityAnnotation(field, isNullable);
                        // Annotate getter method only if the field is not part of an inner class
                        String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        cu.findAll(MethodDeclaration.class).stream()
                                .filter(method -> method.getNameAsString().equals(getterName) && method.getParameters().isEmpty())
                                .forEach(getter -> addNullabilityAnnotationToMethod(getter, isNullable));

                        String clearerName = "clear" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        cu.findAll(MethodDeclaration.class).stream()
                                .filter(method -> method.getNameAsString().equals(clearerName) && method.getParameters().isEmpty())
                                .forEach(clearer -> addNullabilityAnnotationToMethod(clearer, false));

                        // Annotate Builder setter method parameter
                        String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        cu.findAll(MethodDeclaration.class).stream()
                                .filter(method -> method.getNameAsString().equals(setterName) && method.getParameters().size() == 1)
                                .forEach(setter -> {
                                    Parameter param = setter.getParameter(0);
                                    addNullabilityAnnotationToParameter(param, isNullable);
                                    // Annotate the setter method itself with @NotNull
                                    addNullabilityAnnotationToMethod(setter, false);
                                });

                        if (isTemplatedType(avroField)) {
                            // Templates
                            NodeList<Type> fieldTypeTemplates = field.getCommonType().asClassOrInterfaceType().getTypeArguments().get();
                            NodeList<Type> getterReturnTypeTemplates = cu.findAll(MethodDeclaration.class).stream()
                                    .filter(AvroClassProcessor::isTopLevel)
                                    .filter(method -> method.getNameAsString().equals(getterName) && method.getParameters().isEmpty())
                                    .map(getter -> getter.getType().asClassOrInterfaceType().getTypeArguments().get())
                                    .findFirst().get();
                            NodeList<Type> setterParameterTypeTemplates = cu.findAll(MethodDeclaration.class).stream()
                                    .filter(AvroClassProcessor::isTopLevel)
                                    .filter(method -> method.getNameAsString().equals(setterName) && method.getParameters().size() == 1)
                                    .map(setter -> setter.getParameter(0))
                                    .map(setterArgument -> setterArgument.getType().asClassOrInterfaceType().getTypeArguments().get())
                                    .findFirst().get();

                            addAnnotationsToTemplatesInRecursion(avroField.schema(), fieldTypeTemplates);
                            addAnnotationsToTemplatesInRecursion(avroField.schema(), getterReturnTypeTemplates);
                            addAnnotationsToTemplatesInRecursion(avroField.schema(), setterParameterTypeTemplates);
                        }
                    }
                }
            }

            cu.findAll(MethodDeclaration.class).stream()
                    .filter(method -> method.getNameAsString().equals(BUILD_METHOD_NAME) && method.getParameters().isEmpty())
                    .forEach(buildMethod -> addNullabilityAnnotationToMethod(buildMethod, false));
            cu.findAll(MethodDeclaration.class).stream()
                    .filter(method -> method.getNameAsString().equals(NEW_BUILD_METHOD_NAME))
                    .forEach(newBuilderMethod -> addNullabilityAnnotationToMethod(newBuilderMethod, false));
            cu.findAll(MethodDeclaration.class).stream()
                    .filter(method -> method.getNameAsString().equals(NEW_BUILD_METHOD_NAME) && !method.getParameters().isEmpty())
                    .forEach(newBuilderCopyMethod -> {
                        Parameter param = newBuilderCopyMethod.getParameter(0);
                        addNullabilityAnnotationToParameter(param, true);
                    });

            try (FileWriter writer = new FileWriter(javaFile)) {
                writer.write(cu.toString());
            }
        }
    }

    private static void addAnnotationsToTemplatesInRecursion(Schema avroSchema, NodeList<Type> typesOfTemplates) {
        switch (avroSchema.getType()) {
            case ARRAY -> {
                boolean isNullableItem = isNullable(avroSchema.getElementType());
                addNullabilityToTemplateType(typesOfTemplates.get(0), isNullableItem);

                typesOfTemplates.get(0).asClassOrInterfaceType().getTypeArguments()
                        .ifPresent(itemType -> addAnnotationsToTemplatesInRecursion(avroSchema.getElementType(), itemType));
            }
            case MAP -> {
                boolean isNullableValue = isNullable(avroSchema.getValueType());
                addNullabilityToTemplateType(typesOfTemplates.get(0), false);
                addNullabilityToTemplateType(typesOfTemplates.get(1), isNullableValue);

                typesOfTemplates.get(1).asClassOrInterfaceType().getTypeArguments()
                        .ifPresent(valueType -> addAnnotationsToTemplatesInRecursion(avroSchema.getValueType(), valueType));
            }
        }
    }

    private static boolean isTopLevel(HasParentNode<?> node) {
        TypeDeclaration<?> typeDeclaration = node.findAncestor(TypeDeclaration.class).get();
        return !typeDeclaration.isNestedType();
    }

    private static boolean isTemplatedType(Schema.Field avroField) {
        Set<Schema.Type> templatedAvroTypes = Set.of(Schema.Type.ARRAY, Schema.Type.MAP);
        return templatedAvroTypes.contains(avroField.schema().getType());
    }

    private static boolean isNullable(Schema schema) {
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

    private static void addNullabilityToTemplateType(Type type, boolean isNullable) {
        String annotationName = isNullable ? NULLABLE_ANNOTATION : NOT_NULL_ANNOTATION;
        if (!hasAnnotation(type.getAnnotations(), annotationName)) {
            type.setAnnotations(NodeList.nodeList(new MarkerAnnotationExpr(annotationName)));
        }
    }

    private static boolean hasAnnotation(NodeList<AnnotationExpr> annotations, String annotationName) {
        return annotations.stream().anyMatch(annotation -> annotation.getNameAsString().equals(annotationName));
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
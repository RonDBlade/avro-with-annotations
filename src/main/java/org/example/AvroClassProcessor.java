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
                Schema.Field avroField = avroSchema.getField(fieldName);

                if (isTopLevel(field)) {
                    if (avroField != null) {
                        // Working on the actual schema class fields
                        boolean isNullable = isNullable(avroField.schema());
                        addNullabilityAnnotation(field, isNullable);

                        String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        cu.findAll(MethodDeclaration.class).stream()
                                .filter(method -> method.getNameAsString().equals(getterName) && method.getParameters().isEmpty())
                                .filter(AvroClassProcessor::isTopLevel)
                                .forEach(getter -> addNullabilityAnnotationToMethod(getter, isNullable));

                        // Annotate Builder setter method parameter
                        String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        cu.findAll(MethodDeclaration.class).stream()
                                .filter(method -> method.getNameAsString().equals(setterName) && method.getParameters().size() == 1)
                                .filter(AvroClassProcessor::isTopLevel)
                                .forEach(setter -> {
                                    Parameter param = setter.getParameter(0);
                                    addNullabilityAnnotationToParameter(param, isNullable);
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
                } else {
                    // Working on the builder fields
                    if (avroField != null) {
                        // Fields in the builder from the schema
                        boolean isNullable = !field.getCommonType().isPrimitiveType();
                        addNullabilityAnnotation(field, isNullable);

                        String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        cu.findAll(MethodDeclaration.class).stream()
                                .filter(method -> method.getNameAsString().equals(getterName) && method.getParameters().isEmpty())
                                .filter(getter -> !isTopLevel(getter))
                                .forEach(builderGetter -> addNullabilityAnnotationToMethod(builderGetter, isNullable));

                        String clearerName = "clear" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        cu.findAll(MethodDeclaration.class).stream()
                                .filter(method -> method.getNameAsString().equals(clearerName) && method.getParameters().isEmpty())
                                .filter(clearer -> !isTopLevel(clearer))
                                .forEach(builderClearer -> addNullabilityAnnotationToMethod(builderClearer, false));

                        // Annotate Builder setter method parameter
                        String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        cu.findAll(MethodDeclaration.class).stream()
                                .filter(method -> method.getNameAsString().equals(setterName) && method.getParameters().size() == 1)
                                .filter(setter -> !isTopLevel(setter))
                                .forEach(builderSetter -> {
                                    Parameter param = builderSetter.getParameter(0);
                                    addNullabilityAnnotationToParameter(param, isNullable(avroField.schema()));
                                    // Annotate the builders setter method itself with @NotNull
                                    addNullabilityAnnotationToMethod(builderSetter, false);
                                });

                        if (isTemplatedType(avroField)) {
                            // Templates
                            NodeList<Type> fieldTypeTemplates = field.getCommonType().asClassOrInterfaceType().getTypeArguments().get();
                            NodeList<Type> getterReturnTypeTemplates = cu.findAll(MethodDeclaration.class).stream()
                                    .filter(method -> !isTopLevel(method))
                                    .filter(builderMethod -> builderMethod.getNameAsString().equals(getterName) && builderMethod.getParameters().isEmpty())
                                    .map(builderGetter -> builderGetter.getType().asClassOrInterfaceType().getTypeArguments().get())
                                    .findFirst().get();
                            NodeList<Type> setterParameterTypeTemplates = cu.findAll(MethodDeclaration.class).stream()
                                    .filter(method -> !isTopLevel(method))
                                    .filter(builderMethod -> builderMethod.getNameAsString().equals(setterName) && builderMethod.getParameters().size() == 1)
                                    .map(builderSetter -> builderSetter.getParameter(0))
                                    .map(setterArgument -> setterArgument.getType().asClassOrInterfaceType().getTypeArguments().get())
                                    .findFirst().get();

                            addAnnotationsToTemplatesInRecursion(avroField.schema(), fieldTypeTemplates);
                            addAnnotationsToTemplatesInRecursion(avroField.schema(), getterReturnTypeTemplates);
                            addAnnotationsToTemplatesInRecursion(avroField.schema(), setterParameterTypeTemplates);
                        }
                    } else {
                        // The additional builder fields. Meaning, those who end with the word "builder"
                        boolean isNullable = true;
                        addNullabilityAnnotation(field, isNullable);

                        String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        cu.findAll(MethodDeclaration.class).stream()
                                .filter(method -> method.getNameAsString().equals(getterName) && method.getParameters().isEmpty())
                                .filter(getter -> !isTopLevel(getter))
                                .forEach(builderGetter -> addNullabilityAnnotationToMethod(builderGetter, isNullable));

                        String clearerName = "clear" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        cu.findAll(MethodDeclaration.class).stream()
                                .filter(method -> method.getNameAsString().equals(clearerName) && method.getParameters().isEmpty())
                                .filter(clearer -> !isTopLevel(clearer))
                                .forEach(builderClearer -> addNullabilityAnnotationToMethod(builderClearer, false));

                        // Annotate Builder setter method parameter
                        String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        cu.findAll(MethodDeclaration.class).stream()
                                .filter(method -> method.getNameAsString().equals(setterName) && method.getParameters().size() == 1)
                                .filter(setter -> !isTopLevel(setter))
                                .forEach(builderSetter -> {
                                    Parameter param = builderSetter.getParameter(0);
                                    addNullabilityAnnotationToParameter(param, isNullable);
                                    // Annotate the builders setter method itself with @NotNull
                                    addNullabilityAnnotationToMethod(builderSetter, false);
                                });
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
        Schema trueSchema = getTrueFieldSchema(avroSchema);
        switch (trueSchema.getType()) {
            case ARRAY -> {
                boolean isNullableItem = isNullable(trueSchema.getElementType());
                addNullabilityToTemplateType(typesOfTemplates.get(0), isNullableItem);

                typesOfTemplates.get(0).asClassOrInterfaceType().getTypeArguments()
                        .ifPresent(itemType -> addAnnotationsToTemplatesInRecursion(trueSchema.getElementType(), itemType));
            }
            case MAP -> {
                boolean isNullableValue = isNullable(trueSchema.getValueType());
                addNullabilityToTemplateType(typesOfTemplates.get(0), false);
                addNullabilityToTemplateType(typesOfTemplates.get(1), isNullableValue);

                typesOfTemplates.get(1).asClassOrInterfaceType().getTypeArguments()
                        .ifPresent(valueType -> addAnnotationsToTemplatesInRecursion(trueSchema.getValueType(), valueType));
            }
        }
    }

    private static Schema getTrueFieldSchema(Schema avroSchema) {
        if (avroSchema.getType() != Schema.Type.UNION) {
            return avroSchema;
        }

        if (avroSchema.getTypes().size() >= 3) {
            return avroSchema;
        }

        if (avroSchema.getTypes().stream().noneMatch(singleSchema -> singleSchema.getType() == Schema.Type.NULL)) {
            return avroSchema;
        }

        return avroSchema.getTypes().stream()
                .filter(singleSchema -> singleSchema.getType() != Schema.Type.NULL)
                .findFirst()
                .get();
    }

    private static boolean isTopLevel(HasParentNode<?> node) {
        TypeDeclaration<?> typeDeclaration = node.findAncestor(TypeDeclaration.class).get();
        return !typeDeclaration.isNestedType();
    }

    private static boolean isTemplatedType(Schema.Field avroField) {
        Set<Schema.Type> templatedAvroTypes = Set.of(Schema.Type.ARRAY, Schema.Type.MAP);

        Schema trueFieldSchema = getTrueFieldSchema(avroField.schema());
        return templatedAvroTypes.contains(trueFieldSchema.getType());
    }

    private static boolean isNullable(Schema schema) {
        if (schema.getType() == Schema.Type.UNION) {
            return schema.getTypes().stream().anyMatch(type -> type.getType() == Schema.Type.NULL);
        }
        return false;
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
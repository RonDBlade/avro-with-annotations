import net.bytebuddy.description.annotation.AnnotationList
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.ClassFileLocator
import net.bytebuddy.matcher.ElementMatchers
import net.bytebuddy.pool.TypePool
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class ListsObjectTest {

    companion object {
        private const val SCHEMA_CLASS = "com.example.testsuite.ListsObject"
        private const val SCHEMA_BUILDER_CLASS = "com.example.testsuite.ListsObject\$Builder"
        private val NON_NULL_ANNOTATION_TYPE = TypeDescription.ForLoadedType(NotNull::class.java)
        private val NULLABLE_ANNOTATION_TYPE = TypeDescription.ForLoadedType(Nullable::class.java)

        // Byte Buddy TypeDescription of our target class, loaded once for all tests
        private lateinit var schemaClass: TypeDescription
        private lateinit var schemaBuilderClass: TypeDescription

        @JvmStatic
        @BeforeAll
        fun setUp() {
            // When running tests via Gradle/Maven, the compiled classes are usually
            // already on the system classpath. We can leverage this directly.
            val locator = ClassFileLocator.ForClassLoader.ofSystemLoader()

            // Create a TypePool to resolve type descriptions from the locator
            val typePool = TypePool.Default.of(locator)

            // Describe and resolve the target class
            schemaClass = typePool.describe(SCHEMA_CLASS).resolve()
            schemaBuilderClass = typePool.describe(SCHEMA_BUILDER_CLASS).resolve()
        }

        @JvmStatic
        fun classesToTest() = setOf(schemaClass, schemaBuilderClass)

        @JvmStatic
        fun nonPrimitiveFieldsOfSchema() = setOf(
            "listWithRegularValues",
            "listWithNullableValues",
            "listWithRegularListWithRegularValues",
            "listWithRegularListWithNullableValues",
            "listWithNullableListWithRegularValues",
            "listWithNullableListWithNullableValues"
        )

        @JvmStatic
        fun nullableNonPrimitiveFieldsOfSchema() = setOf(
            "nullableListWithRegularValues",
            "nullableListWithNullableValues",
            "nullableListWithRegularListWithRegularValues",
            "nullableListWithRegularListWithNullableValues",
            "nullableListWithNullableListWithRegularValues",
            "nullableListWithNullableListWithNullableValues"
        )

        @JvmStatic
        fun regularFirstLevelTemplate() = setOf(
            "listWithRegularValues",
            "nullableListWithRegularValues",
            "listWithRegularListWithRegularValues",
            "nullableListWithRegularListWithRegularValues",
            "listWithRegularListWithNullableValues",
            "nullableListWithRegularListWithNullableValues"
        )

        @JvmStatic
        fun classWithRegularFirstLevelTemplate(): List<Arguments> {
            return classesToTest().flatMap { clazz: TypeDescription ->
                regularFirstLevelTemplate().flatMap { fieldName: String ->
                    listOf(Arguments.arguments(clazz, fieldName))
                }
            }
        }

        @JvmStatic
        fun nullableFirstLevelTemplate() = setOf(
            "listWithNullableValues",
            "nullableListWithNullableValues",
            "listWithNullableListWithRegularValues",
            "listWithNullableListWithNullableValues",
            "nullableListWithNullableListWithRegularValues",
            "nullableListWithNullableListWithNullableValues"
        )

        @JvmStatic
        fun classWithNullableFirstLevelTemplate(): List<Arguments> {
            return classesToTest().flatMap { clazz: TypeDescription ->
                nullableFirstLevelTemplate().flatMap { fieldName: String ->
                    listOf(Arguments.arguments(clazz, fieldName))
                }
            }
        }

        @JvmStatic
        fun regularSecondLevelTemplate() = setOf(
            "listWithRegularListWithRegularValues",
            "nullableListWithRegularListWithRegularValues",
            "listWithNullableListWithRegularValues",
            "nullableListWithNullableListWithRegularValues"
        )

        @JvmStatic
        fun classWithRegularSecondLevelTemplate(): List<Arguments> {
            return classesToTest().flatMap { clazz: TypeDescription ->
                regularSecondLevelTemplate().flatMap { fieldName: String ->
                    listOf(Arguments.arguments(clazz, fieldName))
                }
            }
        }

        @JvmStatic
        fun nullableSecondLevelTemplate() = setOf(
            "listWithRegularListWithNullableValues",
            "nullableListWithRegularListWithNullableValues",
            "listWithNullableListWithNullableValues",
            "nullableListWithNullableListWithNullableValues"
        )

        @JvmStatic
        fun classWithNullableSecondLevelTemplate(): List<Arguments> {
            return classesToTest().flatMap { clazz: TypeDescription ->
                nullableSecondLevelTemplate().flatMap { fieldName: String ->
                    listOf(Arguments.arguments(clazz, fieldName))
                }
            }
        }
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nonPrimitiveFieldsOfSchema")
    fun `test non primitive field is marked as not nullable in the schema`(fieldName: String) {
        val fieldDescription = schemaClass.declaredFields
            .filter(ElementMatchers.named(fieldName))
            .getOnly()

        val annotations = fieldDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nonPrimitiveFieldsOfSchema")
    fun `test non primitive field is still marked as nullable in the builder`(fieldName: String) {
        val fieldDescription = schemaBuilderClass.declaredFields
            .filter(ElementMatchers.named(fieldName))
            .getOnly()

        val annotations = fieldDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nonPrimitiveFieldsOfSchema")
    fun `test non primitive field getter method is marked as not nullable in the schema`(fieldName: String) {
        val methodName = "get${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = schemaClass.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .getOnly()

        val annotations = methodDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nonPrimitiveFieldsOfSchema")
    fun `test non primitive field getter method is still marked as nullable in the builder`(fieldName: String) {
        val methodName = "get${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = schemaBuilderClass.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .getOnly()

        val annotations = methodDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nonPrimitiveFieldsOfSchema")
    fun `test non primitive field setter method parameter is marked as not nullable in the schema`(fieldName: String) {
        val methodName = "set${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = schemaClass.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .getOnly()
        val parameter = methodDescription.parameters[0]

        val annotations = parameter.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nonPrimitiveFieldsOfSchema")
    fun `test non primitive field setter method parameter is marked as not nullable in the builder`(fieldName: String) {
        val methodName = "set${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = schemaBuilderClass.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .getOnly()
        val parameter = methodDescription.parameters[0]

        val annotations = parameter.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nonPrimitiveFieldsOfSchema")
    fun `test non primitive field setter method is marked as not nullable in the builder`(fieldName: String) {
        val methodName = "set${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = schemaBuilderClass.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .getOnly()

        val annotations = methodDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nonPrimitiveFieldsOfSchema")
    fun `test non primitive field clear method is marked as not nullable in the builder`(fieldName: String) {
        val methodName = "clear${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = schemaBuilderClass.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .getOnly()

        val annotations = methodDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nullableNonPrimitiveFieldsOfSchema")
    fun `test nullable non primitive field is marked as nullable in the schema`(fieldName: String) {
        val fieldDescription = schemaClass.declaredFields
            .filter(ElementMatchers.named(fieldName))
            .getOnly()

        val annotations = fieldDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nullableNonPrimitiveFieldsOfSchema")
    fun `test nullable non primitive field is marked as nullable in the builder`(fieldName: String) {
        val fieldDescription = schemaBuilderClass.declaredFields
            .filter(ElementMatchers.named(fieldName))
            .getOnly()

        val annotations = fieldDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nullableNonPrimitiveFieldsOfSchema")
    fun `test nullable non primitive field getter method is marked as nullable in the schema`(fieldName: String) {
        val methodName = "get${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = schemaClass.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .getOnly()

        val annotations = methodDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nullableNonPrimitiveFieldsOfSchema")
    fun `test nullable non primitive field getter method is marked as nullable in the builder`(fieldName: String) {
        val methodName = "get${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = schemaBuilderClass.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .getOnly()

        val annotations = methodDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nullableNonPrimitiveFieldsOfSchema")
    fun `test nullable non primitive field setter method parameter is marked as nullable in the schema`(fieldName: String) {
        val methodName = "set${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = schemaClass.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .getOnly()
        val parameter = methodDescription.parameters[0]

        val annotations = parameter.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nullableNonPrimitiveFieldsOfSchema")
    fun `test nullable non primitive field setter method parameter is marked as nullable in the builder`(fieldName: String) {
        val methodName = "set${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = schemaBuilderClass.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .getOnly()
        val parameter = methodDescription.parameters[0]

        val annotations = parameter.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nullableNonPrimitiveFieldsOfSchema")
    fun `test nullable non primitive field setter method is marked as not nullable in the builder`(fieldName: String) {
        val methodName = "set${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = schemaBuilderClass.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .getOnly()

        val annotations = methodDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nullableNonPrimitiveFieldsOfSchema")
    fun `test nullable non primitive field clear method is marked as not nullable in the builder`(fieldName: String) {
        val methodName = "clear${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = schemaBuilderClass.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .getOnly()

        val annotations = methodDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest(name = "[{displayName}] - for class {0}, for field: {1}")
    @MethodSource("classWithRegularFirstLevelTemplate")
    fun `test that field whose first level template type is not nullable, the template is marked as not nullable`(
        clazz: TypeDescription, fieldName: String
    ) {
        val fieldTemplateTypeDescription = clazz.declaredFields
            .filter(ElementMatchers.named(fieldName))
            .getOnly()
            .type
            .typeArguments[0]

        val annotations = fieldTemplateTypeDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for class {0}, for field: {1}")
    @MethodSource("classWithRegularFirstLevelTemplate")
    fun `test that getter of field whose first level template type is not nullable, the template is marked as not nullable`(
        clazz: TypeDescription, fieldName: String
    ) {
        val methodName = "get${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodReturnTypeTemplateTypeDescription = clazz.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .getOnly()
            .returnType
            .typeArguments[0]

        val annotations = methodReturnTypeTemplateTypeDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for class {0}, for field: {1}")
    @MethodSource("classWithRegularFirstLevelTemplate")
    fun `test that parameter of the setter of field whose first level template type is not nullable, the template is marked as not nullable`(
        clazz: TypeDescription, fieldName: String
    ) {
        val methodName = "set${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = clazz.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .getOnly()
        val parameter = methodDescription.parameters[0]
        val parameterTemplateTypeDescription = parameter.type
            .typeArguments[0]

        val annotations = parameterTemplateTypeDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest(name = "[{displayName}] - for class {0}, for field: {1}")
    @MethodSource("classWithNullableFirstLevelTemplate")
    fun `test that field whose first level template type is nullable, the template is marked as nullable`(
        clazz: TypeDescription, fieldName: String
    ) {
        val fieldTemplateTypeDescription = clazz.declaredFields
            .filter(ElementMatchers.named(fieldName))
            .getOnly()
            .type
            .typeArguments[0]

        val annotations = fieldTemplateTypeDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for class {0}, for field: {1}")
    @MethodSource("classWithNullableFirstLevelTemplate")
    fun `test that getter of field whose first level template type is nullable, the template is marked as nullable`(
        clazz: TypeDescription, fieldName: String
    ) {
        val methodName = "get${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodReturnTypeTemplateTypeDescription = clazz.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .getOnly()
            .returnType
            .typeArguments[0]

        val annotations = methodReturnTypeTemplateTypeDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for class {0}, for field: {1}")
    @MethodSource("classWithNullableFirstLevelTemplate")
    fun `test that parameter of the setter of field whose first level template type is nullable, the template is marked as nullable`(
        clazz: TypeDescription, fieldName: String
    ) {
        val methodName = "set${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = clazz.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .getOnly()
        val parameter = methodDescription.parameters[0]
        val parameterTemplateTypeDescription = parameter.type
            .typeArguments[0]

        val annotations = parameterTemplateTypeDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest(name = "[{displayName}] - for class {0}, for field: {1}")
    @MethodSource("classWithRegularSecondLevelTemplate")
    fun `test that field whose second level template type is not nullable, the template is marked as not nullable`(
        clazz: TypeDescription, fieldName: String
    ) {
        val fieldSecondLevelTemplateTypeDescription = clazz.declaredFields
            .filter(ElementMatchers.named(fieldName))
            .getOnly()
            .type
            .typeArguments[0]
            .typeArguments[0]

        val annotations = fieldSecondLevelTemplateTypeDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for class {0}, for field: {1}")
    @MethodSource("classWithRegularSecondLevelTemplate")
    fun `test that getter of field whose second level template type is not nullable, the template is marked as not nullable`(
        clazz: TypeDescription, fieldName: String
    ) {
        val methodName = "get${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodReturnTypeSecondLevelTemplateTypeDescription = clazz.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .getOnly()
            .returnType
            .typeArguments[0]
            .typeArguments[0]

        val annotations = methodReturnTypeSecondLevelTemplateTypeDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for class {0}, for field: {1}")
    @MethodSource("classWithRegularSecondLevelTemplate")
    fun `test that parameter of the setter of field whose second level template type is not nullable, the template is marked as not nullable`(
        clazz: TypeDescription, fieldName: String
    ) {
        val methodName = "set${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = clazz.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .getOnly()
        val parameter = methodDescription.parameters[0]
        val parameterSecondLevelTemplateTypeDescription = parameter.type
            .typeArguments[0]
            .typeArguments[0]

        val annotations = parameterSecondLevelTemplateTypeDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest(name = "[{displayName}] - for class {0}, for field: {1}")
    @MethodSource("classWithNullableSecondLevelTemplate")
    fun `test that field whose second level template type is nullable, the template is marked as nullable`(
        clazz: TypeDescription, fieldName: String
    ) {
        val fieldSecondLevelTemplateTypeDescription = clazz.declaredFields
            .filter(ElementMatchers.named(fieldName))
            .getOnly()
            .type
            .typeArguments[0]
            .typeArguments[0]

        val annotations = fieldSecondLevelTemplateTypeDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for class {0}, for field: {1}")
    @MethodSource("classWithNullableSecondLevelTemplate")
    fun `test that getter of field whose second level template type is nullable, the template is marked as nullable`(
        clazz: TypeDescription, fieldName: String
    ) {
        val methodName = "get${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodReturnTypeSecondLevelTemplateTypeDescription = clazz.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .getOnly()
            .returnType
            .typeArguments[0]
            .typeArguments[0]

        val annotations = methodReturnTypeSecondLevelTemplateTypeDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for class {0}, for field: {1}")
    @MethodSource("classWithNullableSecondLevelTemplate")
    fun `test that parameter of the setter of field whose second level template type is nullable, the template is marked as nullable`(
        clazz: TypeDescription, fieldName: String
    ) {
        val methodName = "set${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = clazz.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .getOnly()
        val parameter = methodDescription.parameters[0]
        val parameterSecondLevelTemplateTypeDescription = parameter.type
            .typeArguments[0]
            .typeArguments[0]

        val annotations = parameterSecondLevelTemplateTypeDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    fun assertAnnotations(
        source: AnnotationList,
        existingAnnotation: TypeDescription,
        nonExistingAnnotation: TypeDescription
    ) {
        assertAll(
            {
                assertTrue(source.isAnnotationPresent(existingAnnotation)) { "${existingAnnotation.name} should be present" }
            },
            {
                assertFalse(source.isAnnotationPresent(nonExistingAnnotation)) { "${nonExistingAnnotation.name} should not be present" }
            })
    }
}
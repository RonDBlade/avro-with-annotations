import com.example.testsuite.MapsObject
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.matcher.ElementMatchers
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import TestUtils.assertNotNullable
import TestUtils.assertNullable
import TestUtils.extractMatchingMethod
import TestUtils.NON_NULL_ANNOTATION_TYPE
import TestUtils.NULLABLE_ANNOTATION_TYPE
import TestUtils.assertAnnotations
import org.junit.jupiter.api.assertAll

class MapsObjectTest {

    companion object {
        private const val SCHEMA_CLASS = "com.example.testsuite.MapsObject"

        // Byte Buddy TypeDescription of our target class, loaded once for all tests
        private lateinit var schemaClass: TypeDescription
        private lateinit var schemaBuilderClass: TypeDescription

        @JvmStatic
        @BeforeAll
        fun setUp() {
            val (schema, schemaBuilder) = TestUtils.createTypeDescriptions(SCHEMA_CLASS)
            schemaClass = schema
            schemaBuilderClass = schemaBuilder
        }

        @JvmStatic
        fun classesToTest() = setOf(schemaClass, schemaBuilderClass)

        @JvmStatic
        fun mapFieldsOfSchema() = setOf(
            "mapWithRegularValues",
            "mapWithNullableValues",
            "mapWithRegularMapWithRegularValues",
            "mapWithRegularMapWithNullableValues",
            "mapWithNullableMapWithRegularValues",
            "mapWithNullableMapWithNullableValues"
        )

        @JvmStatic
        fun nullableMapFieldsOfSchema() = setOf(
            "nullableMapWithRegularValues",
            "nullableMapWithNullableValues",
            "nullableMapWithRegularMapWithRegularValues",
            "nullableMapWithRegularMapWithNullableValues",
            "nullableMapWithNullableMapWithRegularValues",
            "nullableMapWithNullableMapWithNullableValues"
        )

        @JvmStatic
        fun regularFirstLevelTemplate() = setOf(
            "mapWithRegularValues",
            "nullableMapWithRegularValues",
            "mapWithRegularMapWithRegularValues",
            "nullableMapWithRegularMapWithRegularValues",
            "mapWithRegularMapWithNullableValues",
            "nullableMapWithRegularMapWithNullableValues"
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
            "mapWithNullableValues",
            "nullableMapWithNullableValues",
            "mapWithNullableMapWithRegularValues",
            "mapWithNullableMapWithNullableValues",
            "nullableMapWithNullableMapWithRegularValues",
            "nullableMapWithNullableMapWithNullableValues"
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
            "mapWithRegularMapWithRegularValues",
            "nullableMapWithRegularMapWithRegularValues",
            "mapWithNullableMapWithRegularValues",
            "nullableMapWithNullableMapWithRegularValues"
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
            "mapWithRegularMapWithNullableValues",
            "nullableMapWithRegularMapWithNullableValues",
            "mapWithNullableMapWithNullableValues",
            "nullableMapWithNullableMapWithNullableValues"
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

    @TestFactory
    fun `test newBuilder methods are marked not nullable`(testInfo: TestInfo): List<DynamicTest> {
        val newBuilderMethodsDescription = schemaClass.declaredMethods
            .filter(ElementMatchers.named("newBuilder"))

        return newBuilderMethodsDescription.map {
            val annotations = it.declaredAnnotations

            DynamicTest.dynamicTest(
                "[${testInfo.displayName}] - method with the parameter ${it.parameters}"
            ) {
                annotations.assertNotNullable()
            }
        }.toList()
    }

    @TestFactory
    fun `test newBuilder methods parameter is marked nullable`(testInfo: TestInfo): List<DynamicTest> {
        val copyBuilderMethodsDescription = schemaClass.declaredMethods
            .filter(ElementMatchers.named("newBuilder"))
            .filter(ElementMatchers.takesArguments(1))

        return copyBuilderMethodsDescription.map {
            val parameter = it.parameters[0]
            val annotations = parameter.declaredAnnotations

            DynamicTest.dynamicTest(
                "[${testInfo.displayName}] - method with the parameter ${it.parameters}"
            ) {
                annotations.assertNullable()
            }
        }.toList()
    }

    @Test
    fun `test that the build method of the builder is marked as not nullable`() {
        val buildMethodDescription = schemaBuilderClass.declaredMethods
            .filter(ElementMatchers.named("build"))
            /*
            * Note: Adding this filter because for unknown reason it had found 2 build methods, so I added additional
            *  filter so that only the actually used one will be tested.
            */
            .filter(ElementMatchers.returns(TypeDescription.ForLoadedType(MapsObject::class.java)))
            .only

        val annotations = buildMethodDescription.declaredAnnotations

        annotations.assertNotNullable()
    }

    /////////////////////////////////////////////////////////////////////////////////////////////


    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("mapFieldsOfSchema")
    fun `test map field is marked as not nullable in the schema`(fieldName: String) {
        val fieldDescription = schemaClass.declaredFields
            .filter(ElementMatchers.named(fieldName))
            .only

        val annotations = fieldDescription.declaredAnnotations

        annotations.assertNotNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("mapFieldsOfSchema")
    fun `test map field is still marked as nullable in the builder`(fieldName: String) {
        val fieldDescription = schemaBuilderClass.declaredFields
            .filter(ElementMatchers.named(fieldName))
            .only

        val annotations = fieldDescription.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("mapFieldsOfSchema")
    fun `test map field getter method is marked as not nullable in the schema`(fieldName: String) {
        val methodDescription = schemaClass.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.GETTER
        )

        val annotations = methodDescription.declaredAnnotations

        annotations.assertNotNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("mapFieldsOfSchema")
    fun `test map field getter method is still marked as nullable in the builder`(fieldName: String) {
        val methodDescription = schemaBuilderClass.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.GETTER
        )

        val annotations = methodDescription.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("mapFieldsOfSchema")
    fun `test map field setter method parameter is marked as not nullable in the schema`(fieldName: String) {
        val methodDescription = schemaClass.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.SETTER
        )
        val parameter = methodDescription.parameters[0]

        val annotations = parameter.declaredAnnotations

        annotations.assertNotNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("mapFieldsOfSchema")
    fun `test map field setter method parameter is marked as not nullable in the builder`(fieldName: String) {
        val methodDescription = schemaBuilderClass.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.SETTER
        )
        val parameter = methodDescription.parameters[0]

        val annotations = parameter.declaredAnnotations

        annotations.assertNotNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("mapFieldsOfSchema")
    fun `test map field setter method is marked as not nullable in the builder`(fieldName: String) {
        val methodDescription = schemaBuilderClass.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.SETTER
        )

        val annotations = methodDescription.declaredAnnotations

        annotations.assertNotNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("mapFieldsOfSchema")
    fun `test map field clear method is marked as not nullable in the builder`(fieldName: String) {
        val methodDescription = schemaBuilderClass.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.CLEARER
        )

        val annotations = methodDescription.declaredAnnotations

        annotations.assertNotNullable()
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nullableMapFieldsOfSchema")
    fun `test nullable map field is marked as nullable in the schema`(fieldName: String) {
        val fieldDescription = schemaClass.declaredFields
            .filter(ElementMatchers.named(fieldName))
            .only

        val annotations = fieldDescription.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nullableMapFieldsOfSchema")
    fun `test nullable map field is marked as nullable in the builder`(fieldName: String) {
        val fieldDescription = schemaBuilderClass.declaredFields
            .filter(ElementMatchers.named(fieldName))
            .only

        val annotations = fieldDescription.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nullableMapFieldsOfSchema")
    fun `test nullable map field getter method is marked as nullable in the schema`(fieldName: String) {
        val methodDescription = schemaClass.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.GETTER
        )

        val annotations = methodDescription.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nullableMapFieldsOfSchema")
    fun `test nullable map field getter method is marked as nullable in the builder`(fieldName: String) {
        val methodDescription = schemaBuilderClass.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.GETTER
        )

        val annotations = methodDescription.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nullableMapFieldsOfSchema")
    fun `test nullable map field setter method parameter is marked as nullable in the schema`(fieldName: String) {
        val methodDescription = schemaClass.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.SETTER
        )
        val parameter = methodDescription.parameters[0]

        val annotations = parameter.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nullableMapFieldsOfSchema")
    fun `test nullable map field setter method parameter is marked as nullable in the builder`(fieldName: String) {
        val methodDescription = schemaBuilderClass.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.SETTER
        )
        val parameter = methodDescription.parameters[0]

        val annotations = parameter.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nullableMapFieldsOfSchema")
    fun `test nullable map field setter method is marked as not nullable in the builder`(fieldName: String) {
        val methodDescription = schemaBuilderClass.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.SETTER
        )

        val annotations = methodDescription.declaredAnnotations

        annotations.assertNotNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nullableMapFieldsOfSchema")
    fun `test nullable map field clear method is marked as not nullable in the builder`(fieldName: String) {
        val methodDescription = schemaBuilderClass.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.CLEARER
        )

        val annotations = methodDescription.declaredAnnotations

        annotations.assertNotNullable()
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest(name = "[{displayName}] - for class {0}, for field: {1}")
    @MethodSource("classWithRegularFirstLevelTemplate")
    fun `test that field whose first level value template type is not nullable, the template value is marked as not nullable while the keys are marked as not nullable`(
        clazz: TypeDescription, fieldName: String
    ) {
        val fieldTypeDescription = clazz.declaredFields
            .filter(ElementMatchers.named(fieldName))
            .only
            .type

        assertTemplateAnnotations(
            fieldTypeDescription,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for class {0}, for field: {1}")
    @MethodSource("classWithRegularFirstLevelTemplate")
    fun `test that getter of field whose first level value template type is not nullable, the template is marked as not nullable while the keys are marked as not nullable`(
        clazz: TypeDescription, fieldName: String
    ) {
        val methodReturnTypeTypeDescription = clazz.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.GETTER
        ).returnType

        assertTemplateAnnotations(
            methodReturnTypeTypeDescription,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for class {0}, for field: {1}")
    @MethodSource("classWithRegularFirstLevelTemplate")
    fun `test that parameter of the setter of field whose first level value template type is not nullable, the template is marked as not nullable while the keys are marked as not nullable`(
        clazz: TypeDescription, fieldName: String
    ) {
        val methodDescription = clazz.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.SETTER
        )
        val parameter = methodDescription.parameters[0]
        val parameterTypeDescription = parameter.type

        assertTemplateAnnotations(
            parameterTypeDescription,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    fun assertTemplateAnnotations(
        source: TypeDescription.Generic,
        existingAnnotation: TypeDescription,
        nonExistingAnnotation: TypeDescription
    ) {
        assertAll(
            {
                assertForKeyTemplate(source)
            },
            {
                assertForValueTemplate(
                    source,
                    existingAnnotation = existingAnnotation,
                    nonExistingAnnotation = nonExistingAnnotation
                )
            }
        )
    }

    fun assertForKeyTemplate(
        source: TypeDescription.Generic
    ) {
        val keyTemplateAnnotations = source.typeArguments[0].declaredAnnotations

        assertAll(
            "Key annotations assertions",
            {
                keyTemplateAnnotations.assertNotNullable()
            })

    }

    fun assertForValueTemplate(
        source: TypeDescription.Generic,
        existingAnnotation: TypeDescription,
        nonExistingAnnotation: TypeDescription
    ) {
        val valueTemplateAnnotations = source.typeArguments[1].declaredAnnotations

        assertAll(
            "Value annotations assertions",
            {
                assertAnnotations(
                    valueTemplateAnnotations,
                    existingAnnotation = existingAnnotation,
                    nonExistingAnnotation = nonExistingAnnotation
                )
            })
    }
}
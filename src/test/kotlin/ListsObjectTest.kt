import TestUtils.assertNotNullable
import TestUtils.assertNullable
import TestUtils.extractMatchingMethod
import TestUtils.filterFieldsByName
import TestUtils.filterMethodsByName
import com.example.testsuite.ListsObject
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

class ListsObjectTest {

    companion object {
        private const val SCHEMA_CLASS = "com.example.testsuite.ListsObject"

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
        fun listFieldsOfSchema() = setOf(
            "listWithRegularValues",
            "listWithNullableValues",
            "listWithRegularListWithRegularValues",
            "listWithRegularListWithNullableValues",
            "listWithNullableListWithRegularValues",
            "listWithNullableListWithNullableValues"
        )

        @JvmStatic
        fun nullableListFieldsOfSchema() = setOf(
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

    @TestFactory
    fun `test newBuilder methods are marked not nullable`(testInfo: TestInfo): List<DynamicTest> {
        val newBuilderMethodsDescription = schemaClass.filterMethodsByName("newBuilder")

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
        val copyBuilderMethodsDescription = schemaClass.filterMethodsByName("newBuilder")
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
        val buildMethodDescription = schemaBuilderClass.filterMethodsByName("build")
            /*
            * Note: Adding this filter because for unknown reason it had found 2 build methods, so I added additional
            *  filter so that only the actually used one will be tested.
            */
            .filter(ElementMatchers.returns(TypeDescription.ForLoadedType(ListsObject::class.java)))
            .only

        val annotations = buildMethodDescription.declaredAnnotations

        annotations.assertNotNullable()
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("listFieldsOfSchema")
    fun `test list field is marked as not nullable in the schema`(fieldName: String) {
        val fieldDescription = schemaClass.filterFieldsByName(fieldName)
            .only

        val annotations = fieldDescription.declaredAnnotations

        annotations.assertNotNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("listFieldsOfSchema")
    fun `test list field is still marked as nullable in the builder`(fieldName: String) {
        val fieldDescription = schemaBuilderClass.filterFieldsByName(fieldName)
            .only

        val annotations = fieldDescription.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("listFieldsOfSchema")
    fun `test list field getter method is marked as not nullable in the schema`(fieldName: String) {
        val methodDescription = schemaClass.extractMatchingMethod(fieldName, TestUtils.SchemaMethodType.GETTER)

        val annotations = methodDescription.declaredAnnotations

        annotations.assertNotNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("listFieldsOfSchema")
    fun `test list field getter method is still marked as nullable in the builder`(fieldName: String) {
        val methodDescription = schemaBuilderClass.extractMatchingMethod(fieldName, TestUtils.SchemaMethodType.GETTER)

        val annotations = methodDescription.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("listFieldsOfSchema")
    fun `test list field setter method parameter is marked as not nullable in the schema`(fieldName: String) {
        val methodDescription = schemaClass.extractMatchingMethod(fieldName, TestUtils.SchemaMethodType.SETTER)
        val parameter = methodDescription.parameters[0]

        val annotations = parameter.declaredAnnotations

        annotations.assertNotNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("listFieldsOfSchema")
    fun `test list field setter method parameter is marked as not nullable in the builder`(fieldName: String) {
        val methodDescription = schemaBuilderClass.extractMatchingMethod(fieldName, TestUtils.SchemaMethodType.SETTER)
        val parameter = methodDescription.parameters[0]

        val annotations = parameter.declaredAnnotations

        annotations.assertNotNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("listFieldsOfSchema")
    fun `test list field setter method is marked as not nullable in the builder`(fieldName: String) {
        val methodDescription = schemaBuilderClass.extractMatchingMethod(fieldName, TestUtils.SchemaMethodType.SETTER)

        val annotations = methodDescription.declaredAnnotations

        annotations.assertNotNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("listFieldsOfSchema")
    fun `test list field clear method is marked as not nullable in the builder`(fieldName: String) {
        val methodDescription = schemaBuilderClass.extractMatchingMethod(fieldName, TestUtils.SchemaMethodType.CLEARER)

        val annotations = methodDescription.declaredAnnotations

        annotations.assertNotNullable()
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nullableListFieldsOfSchema")
    fun `test nullable list field is marked as nullable in the schema`(fieldName: String) {
        val fieldDescription = schemaClass.filterFieldsByName(fieldName)
            .only

        val annotations = fieldDescription.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nullableListFieldsOfSchema")
    fun `test nullable list field is marked as nullable in the builder`(fieldName: String) {
        val fieldDescription = schemaBuilderClass.filterFieldsByName(fieldName)
            .only

        val annotations = fieldDescription.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nullableListFieldsOfSchema")
    fun `test nullable list field getter method is marked as nullable in the schema`(fieldName: String) {
        val methodDescription = schemaClass.extractMatchingMethod(fieldName, TestUtils.SchemaMethodType.GETTER)

        val annotations = methodDescription.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nullableListFieldsOfSchema")
    fun `test nullable list field getter method is marked as nullable in the builder`(fieldName: String) {
        val methodDescription = schemaBuilderClass.extractMatchingMethod(fieldName, TestUtils.SchemaMethodType.GETTER)

        val annotations = methodDescription.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nullableListFieldsOfSchema")
    fun `test nullable list field setter method parameter is marked as nullable in the schema`(fieldName: String) {
        val methodDescription = schemaClass.extractMatchingMethod(fieldName, TestUtils.SchemaMethodType.SETTER)
        val parameter = methodDescription.parameters[0]

        val annotations = parameter.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nullableListFieldsOfSchema")
    fun `test nullable list field setter method parameter is marked as nullable in the builder`(fieldName: String) {
        val methodDescription = schemaBuilderClass.extractMatchingMethod(fieldName, TestUtils.SchemaMethodType.SETTER)
        val parameter = methodDescription.parameters[0]

        val annotations = parameter.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nullableListFieldsOfSchema")
    fun `test nullable list field setter method is marked as not nullable in the builder`(fieldName: String) {
        val methodDescription = schemaBuilderClass.extractMatchingMethod(fieldName, TestUtils.SchemaMethodType.SETTER)

        val annotations = methodDescription.declaredAnnotations

        annotations.assertNotNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @MethodSource("nullableListFieldsOfSchema")
    fun `test nullable list field clear method is marked as not nullable in the builder`(fieldName: String) {
        val methodDescription = schemaBuilderClass.extractMatchingMethod(fieldName, TestUtils.SchemaMethodType.CLEARER)

        val annotations = methodDescription.declaredAnnotations

        annotations.assertNotNullable()
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest(name = "[{displayName}] - for class {0}, for field: {1}")
    @MethodSource("classWithRegularFirstLevelTemplate")
    fun `test that field whose first level template type is not nullable, the template is marked as not nullable`(
        clazz: TypeDescription, fieldName: String
    ) {
        val fieldTemplateTypeDescription = clazz.filterFieldsByName(fieldName)
            .only
            .type
            .typeArguments[0]

        val annotations = fieldTemplateTypeDescription.declaredAnnotations

        annotations.assertNotNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for class {0}, for field: {1}")
    @MethodSource("classWithRegularFirstLevelTemplate")
    fun `test that getter of field whose first level template type is not nullable, the template is marked as not nullable`(
        clazz: TypeDescription, fieldName: String
    ) {
        val methodReturnTypeTemplateTypeDescription = clazz.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.GETTER)
            .returnType
            .typeArguments[0]

        val annotations = methodReturnTypeTemplateTypeDescription.declaredAnnotations

        annotations.assertNotNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for class {0}, for field: {1}")
    @MethodSource("classWithRegularFirstLevelTemplate")
    fun `test that parameter of the setter of field whose first level template type is not nullable, the template is marked as not nullable`(
        clazz: TypeDescription, fieldName: String
    ) {
        val methodDescription = clazz.extractMatchingMethod(fieldName, TestUtils.SchemaMethodType.SETTER)
        val parameter = methodDescription.parameters[0]
        val parameterTemplateTypeDescription = parameter.type
            .typeArguments[0]

        val annotations = parameterTemplateTypeDescription.declaredAnnotations

        annotations.assertNotNullable()
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest(name = "[{displayName}] - for class {0}, for field: {1}")
    @MethodSource("classWithNullableFirstLevelTemplate")
    fun `test that field whose first level template type is nullable, the template is marked as nullable`(
        clazz: TypeDescription, fieldName: String
    ) {
        val fieldTemplateTypeDescription = clazz.filterFieldsByName(fieldName)
            .only
            .type
            .typeArguments[0]

        val annotations = fieldTemplateTypeDescription.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for class {0}, for field: {1}")
    @MethodSource("classWithNullableFirstLevelTemplate")
    fun `test that getter of field whose first level template type is nullable, the template is marked as nullable`(
        clazz: TypeDescription, fieldName: String
    ) {
        val methodReturnTypeTemplateTypeDescription = clazz.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.GETTER)
            .returnType
            .typeArguments[0]

        val annotations = methodReturnTypeTemplateTypeDescription.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for class {0}, for field: {1}")
    @MethodSource("classWithNullableFirstLevelTemplate")
    fun `test that parameter of the setter of field whose first level template type is nullable, the template is marked as nullable`(
        clazz: TypeDescription, fieldName: String
    ) {
        val methodDescription = clazz.extractMatchingMethod(fieldName, TestUtils.SchemaMethodType.SETTER)
        val parameter = methodDescription.parameters[0]
        val parameterTemplateTypeDescription = parameter.type
            .typeArguments[0]

        val annotations = parameterTemplateTypeDescription.declaredAnnotations

        annotations.assertNullable()
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest(name = "[{displayName}] - for class {0}, for field: {1}")
    @MethodSource("classWithRegularSecondLevelTemplate")
    fun `test that field whose second level template type is not nullable, the template is marked as not nullable`(
        clazz: TypeDescription, fieldName: String
    ) {
        val fieldSecondLevelTemplateTypeDescription = clazz.filterFieldsByName(fieldName)
            .only
            .type
            .typeArguments[0]
            .typeArguments[0]

        val annotations = fieldSecondLevelTemplateTypeDescription.declaredAnnotations

        annotations.assertNotNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for class {0}, for field: {1}")
    @MethodSource("classWithRegularSecondLevelTemplate")
    fun `test that getter of field whose second level template type is not nullable, the template is marked as not nullable`(
        clazz: TypeDescription, fieldName: String
    ) {
        val methodReturnTypeSecondLevelTemplateTypeDescription = clazz.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.GETTER)
            .returnType
            .typeArguments[0]
            .typeArguments[0]

        val annotations = methodReturnTypeSecondLevelTemplateTypeDescription.declaredAnnotations

        annotations.assertNotNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for class {0}, for field: {1}")
    @MethodSource("classWithRegularSecondLevelTemplate")
    fun `test that parameter of the setter of field whose second level template type is not nullable, the template is marked as not nullable`(
        clazz: TypeDescription, fieldName: String
    ) {
        val methodDescription = clazz.extractMatchingMethod(fieldName, TestUtils.SchemaMethodType.SETTER)
        val parameter = methodDescription.parameters[0]
        val parameterSecondLevelTemplateTypeDescription = parameter.type
            .typeArguments[0]
            .typeArguments[0]

        val annotations = parameterSecondLevelTemplateTypeDescription.declaredAnnotations

        annotations.assertNotNullable()
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest(name = "[{displayName}] - for class {0}, for field: {1}")
    @MethodSource("classWithNullableSecondLevelTemplate")
    fun `test that field whose second level template type is nullable, the template is marked as nullable`(
        clazz: TypeDescription, fieldName: String
    ) {
        val fieldSecondLevelTemplateTypeDescription = clazz.filterFieldsByName(fieldName)
            .only
            .type
            .typeArguments[0]
            .typeArguments[0]

        val annotations = fieldSecondLevelTemplateTypeDescription.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for class {0}, for field: {1}")
    @MethodSource("classWithNullableSecondLevelTemplate")
    fun `test that getter of field whose second level template type is nullable, the template is marked as nullable`(
        clazz: TypeDescription, fieldName: String
    ) {
        val methodReturnTypeSecondLevelTemplateTypeDescription = clazz.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.GETTER)
            .returnType
            .typeArguments[0]
            .typeArguments[0]

        val annotations = methodReturnTypeSecondLevelTemplateTypeDescription.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for class {0}, for field: {1}")
    @MethodSource("classWithNullableSecondLevelTemplate")
    fun `test that parameter of the setter of field whose second level template type is nullable, the template is marked as nullable`(
        clazz: TypeDescription, fieldName: String
    ) {
        val methodDescription = clazz.extractMatchingMethod(fieldName, TestUtils.SchemaMethodType.SETTER)
        val parameter = methodDescription.parameters[0]
        val parameterSecondLevelTemplateTypeDescription = parameter.type
            .typeArguments[0]
            .typeArguments[0]

        val annotations = parameterSecondLevelTemplateTypeDescription.declaredAnnotations

        annotations.assertNullable()
    }
}
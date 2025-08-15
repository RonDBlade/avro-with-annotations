import com.example.testsuite.DomainClassWrapper
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.matcher.ElementMatchers
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import TestUtils.assertNotNullable
import TestUtils.assertNullable
import TestUtils.extractMatchingMethod
import TestUtils.filterMethodsByName
import TestUtils.filterFieldsByName

class DomainClassWrapperTest {

    companion object {
        private const val SCHEMA_CLASS = "com.example.testsuite.DomainClassWrapper"

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
        val buildMethodDescription = schemaBuilderClass.filterMethodsByName("build")/*
            * Note: Adding this filter because for unknown reason it had found 2 build methods, so I added additional
            *  filter so that only the actually used one will be tested.
            */.filter(ElementMatchers.returns(TypeDescription.ForLoadedType(DomainClassWrapper::class.java))).only

        val annotations = buildMethodDescription.declaredAnnotations

        annotations.assertNotNullable()
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["independentEnum", "domainClass"])
    fun `test schema generated field is marked as not nullable in the schema`(fieldName: String) {
        val fieldDescription = schemaClass.filterFieldsByName(fieldName).only

        val annotations = fieldDescription.declaredAnnotations

        annotations.assertNotNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["independentEnum", "domainClass"])
    fun `test schema generated field is still marked as nullable in the builder`(fieldName: String) {
        val fieldDescription = schemaBuilderClass.filterFieldsByName(fieldName).only

        val annotations = fieldDescription.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["independentEnum", "domainClass"])
    fun `test schema generated field getter method is marked as not nullable in the schema`(fieldName: String) {
        val methodDescription = schemaClass.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.GETTER
        )

        val annotations = methodDescription.declaredAnnotations

        annotations.assertNotNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["independentEnum", "domainClass"])
    fun `test schema generated field getter method is still marked as nullable in the builder`(fieldName: String) {
        val methodDescription = schemaBuilderClass.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.GETTER
        )

        val annotations = methodDescription.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["independentEnum", "domainClass"])
    fun `test schema generated field setter method parameter is marked as not nullable in the schema`(fieldName: String) {
        val methodDescription = schemaClass.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.SETTER
        )
        val parameter = methodDescription.parameters[0]

        val annotations = parameter.declaredAnnotations

        annotations.assertNotNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["independentEnum", "domainClass"])
    fun `test schema generated field setter method parameter is marked as not nullable in the builder`(fieldName: String) {
        val methodDescription = schemaBuilderClass.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.SETTER
        )
        val parameter = methodDescription.parameters[0]

        val annotations = parameter.declaredAnnotations

        annotations.assertNotNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["independentEnum", "domainClass"])
    fun `test schema generated field setter method is marked as not nullable in the builder`(fieldName: String) {
        val methodDescription = schemaBuilderClass.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.SETTER
        )
        val annotations = methodDescription.declaredAnnotations

        annotations.assertNotNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["independentEnum", "domainClass"])
    fun `test schema generated field clear method is marked as not nullable in the builder`(fieldName: String) {
        val methodDescription = schemaBuilderClass.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.CLEARER
        )
        val annotations = methodDescription.declaredAnnotations

        annotations.assertNotNullable()
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["nullableIndependentEnum", "nullableDomainClass"])
    fun `test nullable schema generated field is marked as nullable in the schema`(fieldName: String) {
        val fieldDescription = schemaClass.filterFieldsByName(fieldName).only

        val annotations = fieldDescription.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["nullableIndependentEnum", "nullableDomainClass"])
    fun `test nullable schema generated field is marked as nullable in the builder`(fieldName: String) {
        val fieldDescription = schemaBuilderClass.filterFieldsByName(fieldName).only

        val annotations = fieldDescription.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["nullableIndependentEnum", "nullableDomainClass"])
    fun `test nullable schema generated field getter method is marked as nullable in the schema`(fieldName: String) {
        val methodDescription = schemaClass.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.GETTER
        )

        val annotations = methodDescription.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["nullableIndependentEnum", "nullableDomainClass"])
    fun `test nullable schema generated field getter method is marked as nullable in the builder`(fieldName: String) {
        val methodDescription = schemaBuilderClass.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.GETTER
        )

        val annotations = methodDescription.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["nullableIndependentEnum", "nullableDomainClass"])
    fun `test nullable schema generated field setter method parameter is marked as nullable in the schema`(fieldName: String) {
        val methodDescription = schemaClass.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.SETTER
        )

        val parameter = methodDescription.parameters[0]

        val annotations = parameter.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["nullableIndependentEnum", "nullableDomainClass"])
    fun `test nullable schema generated field setter method parameter is marked as nullable in the builder`(fieldName: String) {
        val methodDescription = schemaBuilderClass.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.SETTER
        )

        val parameter = methodDescription.parameters[0]

        val annotations = parameter.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["nullableIndependentEnum", "nullableDomainClass"])
    fun `test nullable schema generated field setter method is marked as not nullable in the builder`(fieldName: String) {
        val methodDescription = schemaBuilderClass.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.SETTER
        )

        val annotations = methodDescription.declaredAnnotations

        annotations.assertNotNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["nullableIndependentEnum", "nullableDomainClass"])
    fun `test nullable schema generated field clear method is marked as not nullable in the builder`(fieldName: String) {
        val methodDescription = schemaBuilderClass.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.CLEARER
        )
        val annotations = methodDescription.declaredAnnotations

        annotations.assertNotNullable()
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["domainClass", "nullableDomainClass"])
    fun `test record builder field is marked as nullable in the builder`(fieldName: String) {
        val fieldBuilderFieldName = "${fieldName}Builder"
        val fieldDescription =
            schemaBuilderClass.filterFieldsByName(fieldBuilderFieldName).only

        val annotations = fieldDescription.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["domainClass", "nullableDomainClass"])
    fun `test record builder field getter method is marked as nullable in the builder`(fieldName: String) {
        val methodDescription = schemaBuilderClass.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.BUILDER_GETTER
        )

        val annotations = methodDescription.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["domainClass", "nullableDomainClass"])
    fun `test record builder field setter method parameter is marked as nullable in the builder`(fieldName: String) {
        val methodDescription = schemaBuilderClass.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.BUILDER_SETTER
        )
        val parameter = methodDescription.parameters[0]

        val annotations = parameter.declaredAnnotations

        annotations.assertNullable()
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["domainClass", "nullableDomainClass"])
    fun `test record builder field setter method is marked as not nullable in the builder`(fieldName: String) {
        val methodDescription = schemaBuilderClass.extractMatchingMethod(
            fieldName, TestUtils.SchemaMethodType.BUILDER_SETTER
        )

        val annotations = methodDescription.declaredAnnotations

        annotations.assertNotNullable()
    }
}
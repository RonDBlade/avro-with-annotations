import com.example.testsuite.DomainClassWrapper
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
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class DomainClassWrapperTest {

    companion object {
        private const val SCHEMA_CLASS = "com.example.testsuite.DomainClassWrapper"
        private const val SCHEMA_BUILDER_CLASS = "com.example.testsuite.DomainClassWrapper\$Builder"
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
                assertAnnotations(
                    annotations,
                    existingAnnotation = NON_NULL_ANNOTATION_TYPE,
                    nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
                )
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
                assertAnnotations(
                    annotations,
                    existingAnnotation = NULLABLE_ANNOTATION_TYPE,
                    nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
                )
            }
        }.toList()
    }

    @Test
    fun `test that the build method of the builder is marked as not nullable`() {
        val buildMethodDescription = schemaBuilderClass.declaredMethods
            .filter(ElementMatchers.named("build"))
            .filter(ElementMatchers.returns(TypeDescription.ForLoadedType(DomainClassWrapper::class.java)))
            .only

        val annotations = buildMethodDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["independentEnum", "domainClass"])
    fun `test schema generated field is marked as not nullable in the schema`(fieldName: String) {
        val fieldDescription = schemaClass.declaredFields
            .filter(ElementMatchers.named(fieldName))
            .only

        val annotations = fieldDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["independentEnum", "domainClass"])
    fun `test schema generated field is still marked as nullable in the builder`(fieldName: String) {
        val fieldDescription = schemaBuilderClass.declaredFields
            .filter(ElementMatchers.named(fieldName))
            .only

        val annotations = fieldDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["independentEnum", "domainClass"])
    fun `test schema generated field getter method is marked as not nullable in the schema`(fieldName: String) {
        val methodName = "get${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = schemaClass.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .only

        val annotations = methodDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["independentEnum", "domainClass"])
    fun `test schema generated field getter method is still marked as nullable in the builder`(fieldName: String) {
        val methodName = "get${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = schemaBuilderClass.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .only

        val annotations = methodDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["independentEnum", "domainClass"])
    fun `test schema generated field setter method parameter is marked as not nullable in the schema`(fieldName: String) {
        val methodName = "set${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = schemaClass.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .only
        val parameter = methodDescription.parameters[0]

        val annotations = parameter.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["independentEnum", "domainClass"])
    fun `test schema generated field setter method parameter is marked as not nullable in the builder`(fieldName: String) {
        val methodName = "set${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = schemaBuilderClass.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .only
        val parameter = methodDescription.parameters[0]

        val annotations = parameter.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["independentEnum", "domainClass"])
    fun `test schema generated field setter method is marked as not nullable in the builder`(fieldName: String) {
        val methodName = "set${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = schemaBuilderClass.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .only

        val annotations = methodDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["independentEnum", "domainClass"])
    fun `test schema generated field clear method is marked as not nullable in the builder`(fieldName: String) {
        val methodName = "clear${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = schemaBuilderClass.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .only

        val annotations = methodDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["nullableIndependentEnum", "nullableDomainClass"])
    fun `test nullable schema generated field is marked as nullable in the schema`(fieldName: String) {
        val fieldDescription = schemaClass.declaredFields
            .filter(ElementMatchers.named(fieldName))
            .only

        val annotations = fieldDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["nullableIndependentEnum", "nullableDomainClass"])
    fun `test nullable schema generated field is marked as nullable in the builder`(fieldName: String) {
        val fieldDescription = schemaBuilderClass.declaredFields
            .filter(ElementMatchers.named(fieldName))
            .only

        val annotations = fieldDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["nullableIndependentEnum", "nullableDomainClass"])
    fun `test nullable schema generated field getter method is marked as nullable in the schema`(fieldName: String) {
        val methodName = "get${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = schemaClass.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .only

        val annotations = methodDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["nullableIndependentEnum", "nullableDomainClass"])
    fun `test nullable schema generated field getter method is marked as nullable in the builder`(fieldName: String) {
        val methodName = "get${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = schemaBuilderClass.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .only

        val annotations = methodDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["nullableIndependentEnum", "nullableDomainClass"])
    fun `test nullable schema generated field setter method parameter is marked as nullable in the schema`(fieldName: String) {
        val methodName = "set${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = schemaClass.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .only
        val parameter = methodDescription.parameters[0]

        val annotations = parameter.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["nullableIndependentEnum", "nullableDomainClass"])
    fun `test nullable schema generated field setter method parameter is marked as nullable in the builder`(fieldName: String) {
        val methodName = "set${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = schemaBuilderClass.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .only
        val parameter = methodDescription.parameters[0]

        val annotations = parameter.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["nullableIndependentEnum", "nullableDomainClass"])
    fun `test nullable schema generated field setter method is marked as not nullable in the builder`(fieldName: String) {
        val methodName = "set${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = schemaBuilderClass.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .only

        val annotations = methodDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["nullableIndependentEnum", "nullableDomainClass"])
    fun `test nullable schema generated field clear method is marked as not nullable in the builder`(fieldName: String) {
        val methodName = "clear${fieldName[0].uppercase() + fieldName.substring(1)}"
        val methodDescription = schemaBuilderClass.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .only

        val annotations = methodDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["domainClass", "nullableDomainClass"])
    fun `test record builder field is marked as nullable in the builder`(fieldName: String) {
        val fieldBuilderFieldName = "${fieldName}Builder"
        val fieldDescription = schemaBuilderClass.declaredFields
            .filter(ElementMatchers.named(fieldBuilderFieldName))
            .only

        val annotations = fieldDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["domainClass", "nullableDomainClass"])
    fun `test record builder field getter method is marked as nullable in the builder`(fieldName: String) {
        val fieldBuilderFieldName = "${fieldName}Builder"
        val methodName = "get${fieldBuilderFieldName[0].uppercase() + fieldBuilderFieldName.substring(1)}"
        val methodDescription = schemaBuilderClass.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .only

        val annotations = methodDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["domainClass", "nullableDomainClass"])
    fun `test record builder field setter method parameter is marked as nullable in the builder`(fieldName: String) {
        val fieldBuilderFieldName = "${fieldName}Builder"
        val methodName = "set${fieldBuilderFieldName[0].uppercase() + fieldBuilderFieldName.substring(1)}"
        val methodDescription = schemaBuilderClass.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .only
        val parameter = methodDescription.parameters[0]

        val annotations = parameter.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["domainClass", "nullableDomainClass"])
    fun `test record builder field setter method is marked as not nullable in the builder`(fieldName: String) {
        val fieldBuilderFieldName = "${fieldName}Builder"
        val methodName = "set${fieldBuilderFieldName[0].uppercase() + fieldBuilderFieldName.substring(1)}"
        val methodDescription = schemaBuilderClass.declaredMethods
            .filter(ElementMatchers.named(methodName))
            .only

        val annotations = methodDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

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
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

        @JvmStatic
        fun recordFields() = setOf("domainClass", "nullableDomainClass")
    }

    @ParameterizedTest(name = "[{displayName}] - for field: {0}")
    @ValueSource(strings = ["independentEnum"])
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
    @ValueSource(strings = ["independentEnum"])
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
    @ValueSource(strings = ["independentEnum"])
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
    @ValueSource(strings = ["independentEnum"])
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
    @ValueSource(strings = ["independentEnum"])
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
    @ValueSource(strings = ["independentEnum"])
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
    @ValueSource(strings = ["independentEnum"])
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
    @ValueSource(strings = ["independentEnum"])
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
    @ValueSource(strings = ["nullableIndependentEnum"])
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
    @ValueSource(strings = ["nullableIndependentEnum"])
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
    @ValueSource(strings = ["nullableIndependentEnum"])
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
    @ValueSource(strings = ["nullableIndependentEnum"])
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
    @ValueSource(strings = ["nullableIndependentEnum"])
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
    @ValueSource(strings = ["nullableIndependentEnum"])
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
    @ValueSource(strings = ["nullableIndependentEnum"])
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
    @ValueSource(strings = ["nullableIndependentEnum"])
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
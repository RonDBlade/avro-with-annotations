import net.bytebuddy.description.annotation.AnnotationList
import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.ClassFileLocator
import net.bytebuddy.matcher.ElementMatchers
import net.bytebuddy.pool.TypePool
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class BasicObjectTest {

    companion object {
        private const val SCHEMA_CLASS = "com.example.testsuite.BasicObject"
        private const val SCHEMA_BUILDER_CLASS = "com.example.testsuite.BasicObject\$Builder"
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
            val locator: ClassFileLocator = ClassFileLocator.ForClassLoader.ofSystemLoader()

            // Create a TypePool to resolve type descriptions from the locator
            val typePool = TypePool.Default.of(locator)

            // Describe and resolve the target class
            schemaClass = typePool.describe(SCHEMA_CLASS).resolve()
            schemaBuilderClass = typePool.describe(SCHEMA_BUILDER_CLASS).resolve()
        }

        @JvmStatic
        fun classesOfTheSchema(): List<TypeDescription> {
            return listOf(schemaClass, schemaBuilderClass)
        }
    }

    @ParameterizedTest(name = "[{displayName}] - using class: {0}")
    @MethodSource("classesOfTheSchema")
    fun `test method getPrimitive is marked as not nullable`(classDescription: TypeDescription) {
        val methodDescription: MethodDescription = classDescription.declaredMethods
            .filter(ElementMatchers.named("getPrimitive"))
            .getOnly()

        val annotations = methodDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - using class: {0}")
    @MethodSource("classesOfTheSchema")
    fun `test field primitive is marked as not nullable`() {
        val fieldDescription = schemaClass.declaredFields
            .filter(ElementMatchers.named("primitive"))
            .getOnly()

        val annotations = fieldDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - using class: {0}")
    @MethodSource("classesOfTheSchema")
    fun `test method parameter of setPrimitive is marked as not nullable`() {
        val methodDescription: MethodDescription = schemaClass.declaredMethods
            .filter(ElementMatchers.named("setPrimitive"))
            .getOnly()
        val parameter = methodDescription.parameters[0]

        val annotations = parameter.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE,
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - using class: {0}")
    @MethodSource("classesOfTheSchema")
    fun `test method getNullablePrimitive is marked as  Nullable`(classDescription: TypeDescription) {
        val methodDescription: MethodDescription = classDescription.declaredMethods
            .filter(ElementMatchers.named("getNullablePrimitive"))
            .getOnly()

        val annotations = methodDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - using class: {0}")
    @MethodSource("classesOfTheSchema")
    fun `test field nullablePrimitive is marked as nullable`() {
        val fieldDescription = schemaClass.declaredFields
            .filter(ElementMatchers.named("nullablePrimitive"))
            .getOnly()

        val annotations = fieldDescription.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    @ParameterizedTest(name = "[{displayName}] - using class: {0}")
    @MethodSource("classesOfTheSchema")
    fun `test method parameter of setNullablePrimitive is marked as nullable`() {
        val methodDescription: MethodDescription = schemaClass.declaredMethods
            .filter(ElementMatchers.named("setNullablePrimitive"))
            .getOnly()
        val parameter = methodDescription.parameters[0]

        val annotations = parameter.declaredAnnotations

        assertAnnotations(
            annotations,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE,
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

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
import org.jetbrains.annotations.NotNull
import org.junit.jupiter.api.BeforeAll

import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.ClassFileLocator
import net.bytebuddy.matcher.ElementMatchers
import net.bytebuddy.pool.TypePool
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import net.bytebuddy.description.annotation.AnnotationList
import org.jetbrains.annotations.Nullable

class NewTest {

    companion object {
        private val CLASSES_DIR = "build/generated-test-avro-java" // For Kotlin, usually 'kotlin/main' or 'java/main'
        private val TARGET_CLASS_FQN = "com.example.testsuite.BasicObject"
        private val NON_NULL_ANNOTATION_TYPE = TypeDescription.ForLoadedType(NotNull::class.java)
        private val NULLABLE_ANNOTATION_TYPE = TypeDescription.ForLoadedType(Nullable::class.java)


        private lateinit var targetClassDescription: TypeDescription

        @JvmStatic
        @BeforeAll
        fun setUp() {
            val locator: ClassFileLocator = ClassFileLocator.ForClassLoader.ofSystemLoader()

            // Create a TypePool to resolve type descriptions from the locator
            val typePool = TypePool.Default.of(locator)

            // Describe and resolve the target class
            targetClassDescription = typePool.describe(TARGET_CLASS_FQN).resolve()
            assertNotNull(targetClassDescription) { "Could not find TypeDescription for $TARGET_CLASS_FQN" }
        }
    }

    @Test
    fun `test method getPrimitive has NonNull annotation`() {
        val methodDescription: MethodDescription = targetClassDescription.declaredMethods
            .filter(ElementMatchers.named("getPrimitive"))
            .getOnly()

        val annotations = methodDescription.declaredAnnotations

        assertAnnotations(annotations, existingAnnotation = NON_NULL_ANNOTATION_TYPE, nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE)
    }

    @Test
    fun `test field primitive has NonNull annotation`() {
        val fieldDescription = targetClassDescription.declaredFields
            .filter(ElementMatchers.named("primitive"))
            .getOnly()

        val annotations = fieldDescription.declaredAnnotations

        assertAnnotations(annotations, existingAnnotation = NON_NULL_ANNOTATION_TYPE, nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE)
    }

    @Test
    fun `test method parameter of setPrimitive has NonNull annotation`() {
        val methodDescription: MethodDescription = targetClassDescription.declaredMethods
            .filter(ElementMatchers.named("setPrimitive"))
            .getOnly()
        val parameter = methodDescription.parameters[0]

        val annotations = parameter.declaredAnnotations

        assertAnnotations(annotations, existingAnnotation = NON_NULL_ANNOTATION_TYPE, nonExistingAnnotation =  NULLABLE_ANNOTATION_TYPE)
    }

    fun assertAnnotations(source: AnnotationList, existingAnnotation : TypeDescription, nonExistingAnnotation: TypeDescription) {
        assertAll(
            {
                assertTrue(source.isAnnotationPresent(existingAnnotation)) {"${existingAnnotation.name} should be present"}
            },
            {
                assertFalse(source.isAnnotationPresent(nonExistingAnnotation)) {"${nonExistingAnnotation.name} should not be present"}
            }

        );

    }
}
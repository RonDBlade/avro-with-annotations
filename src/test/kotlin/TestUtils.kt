import net.bytebuddy.description.annotation.AnnotationList
import net.bytebuddy.description.field.FieldDescription
import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.ClassFileLocator
import net.bytebuddy.matcher.ElementMatchers
import net.bytebuddy.pool.TypePool
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

/**
 * Common utilities for testing annotation presence on Avro-generated classes.
 * This object contains shared functionality used across multiple test classes to avoid code duplication.
 */
object TestUtils {

    // Common annotation type descriptions
    val NON_NULL_ANNOTATION_TYPE: TypeDescription = TypeDescription.ForLoadedType(NotNull::class.java)
    val NULLABLE_ANNOTATION_TYPE: TypeDescription = TypeDescription.ForLoadedType(Nullable::class.java)

    /**
     * Enum representing different types of schema methods for field access.
     */
    enum class SchemaMethodType {
        GETTER,
        SETTER,
        CLEARER,
        BUILDER_GETTER,
        BUILDER_SETTER
    }

    /**
     * Creates TypeDescription instances for a schema class and its builder class.
     * 
     * @param schemaClassName The fully qualified name of the schema class
     * @return Pair of (schemaClass, schemaBuilderClass) TypeDescriptions
     */
    fun createTypeDescriptions(schemaClassName: String): Pair<TypeDescription, TypeDescription> {
        val schemaBuilderClassName = "$schemaClassName\$Builder"
        
        // When running tests via Gradle/Maven, the compiled classes are usually
        // already on the system classpath. We can leverage this directly.
        val locator = ClassFileLocator.ForClassLoader.ofSystemLoader()
        
        // Create a TypePool to resolve type descriptions from the locator
        val typePool = TypePool.Default.of(locator)

        // Describe and resolve the target classes
        val schemaClass = typePool.describe(schemaClassName).resolve()
        val schemaBuilderClass = typePool.describe(schemaBuilderClassName).resolve()
        
        return Pair(schemaClass, schemaBuilderClass)
    }

    /**
     * Extension function to extract a field by name from a TypeDescription.
     */
    fun TypeDescription.extractField(fieldName: String): FieldDescription.InDefinedShape {
        return filterFieldsByName(fieldName).only
    }

    /**
     * Extension function to filter declared fields by name from a TypeDescription.
     * 
     * @param fieldName The name of the field to filter by
     * @return FilterableList of fields matching the given name
     */
    fun TypeDescription.filterFieldsByName(fieldName: String) = 
        declaredFields.filter(ElementMatchers.named(fieldName))

    /**
     * Extension function to filter declared methods by name from a TypeDescription.
     * 
     * @param methodName The name of the method to filter by
     * @return FilterableList of methods matching the given name
     */
    fun TypeDescription.filterMethodsByName(methodName: String) = 
        declaredMethods.filter(ElementMatchers.named(methodName))

    /**
     * Extension function to extract a method by field name and method type from a TypeDescription.
     */
    fun TypeDescription.extractMatchingMethod(fieldName: String, schemaMethodType: SchemaMethodType): MethodDescription.InDefinedShape {
        val (prefix, targetFieldName) = when (schemaMethodType) {
            SchemaMethodType.GETTER -> "get" to fieldName
            SchemaMethodType.SETTER -> "set" to fieldName
            SchemaMethodType.CLEARER -> "clear" to fieldName
            SchemaMethodType.BUILDER_GETTER -> "get" to "${fieldName}Builder"
            SchemaMethodType.BUILDER_SETTER -> "set" to "${fieldName}Builder"
        }



        val methodName = "${prefix}${targetFieldName.replaceFirstChar { it.uppercaseChar() }}"

        return filterMethodsByName(methodName).only
    }

    /**
     * Extension function to assert that an AnnotationList contains nullable annotations.
     */
    fun AnnotationList.assertNullable() {
        assertAnnotations(
            this,
            existingAnnotation = NULLABLE_ANNOTATION_TYPE, 
            nonExistingAnnotation = NON_NULL_ANNOTATION_TYPE
        )
    }

    /**
     * Extension function to assert that an AnnotationList contains not-null annotations.
     */
    fun AnnotationList.assertNotNullable() {
        assertAnnotations(
            this,
            existingAnnotation = NON_NULL_ANNOTATION_TYPE, 
            nonExistingAnnotation = NULLABLE_ANNOTATION_TYPE
        )
    }

    /**
     * Core function to assert annotation presence and absence.
     * 
     * @param source The AnnotationList to check
     * @param existingAnnotation The annotation type that should be present
     * @param nonExistingAnnotation The annotation type that should not be present
     */
    fun assertAnnotations(
        source: AnnotationList,
        existingAnnotation: TypeDescription,
        nonExistingAnnotation: TypeDescription
    ) {
        assertAll(
            {
                assertTrue(source.isAnnotationPresent(existingAnnotation)) { 
                    "${existingAnnotation.name} should be present" 
                }
            },
            {
                assertFalse(source.isAnnotationPresent(nonExistingAnnotation)) { 
                    "${nonExistingAnnotation.name} should not be present" 
                }
            }
        )
    }
}

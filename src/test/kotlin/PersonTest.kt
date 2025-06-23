import com.example.Person
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class PersonTest {
    @Test
    fun testPersonNullability() {
        // Create a person with required fields
        val person = Person.newBuilder().setId("123")
            .setAge(2)
            .setListWithValues(listOf(1))
            .setListWithNullableValues(listOf(2, null))
            .setMapWithValues(mapOf("a" to 1))
            .setMapWithNullableValues(mapOf("a" to 1, "b" to null))
            .build()
        // Required field should be non-null
        assertEquals("123", person.id)

        val lnn: List<Int> = person.listWithValues
        val ln: List<Int?> = person.listWithNullableValues
        val mnn: Map<String, Int> = person.mapWithValues
        val mn: Map<String, Int?> = person.mapWithNullableValues

        // Optional field should be nullable
        person.age = null
        assertNull(person.age)

        // Setting optional field should work
        person.age = 30
        assertEquals(30, person.age)
    }
}
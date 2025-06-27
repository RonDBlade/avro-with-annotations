import com.example.Person
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class PersonTest {
    @Test
    fun testPersonNullability() {
        // Create a person with required fields
        val person = Person.newBuilder().setId("123")
            .setAge(2)
            .setIncome(2)
            .setListWithValues(listOf(1))
            .setListWithNullableValues(listOf(2, null))
            .setListWithListWithValues(listOf(listOf(10)))
            .setMapWithValues(mapOf("a" to 1))
            .setMapWithNullableValues(mapOf("a" to 1, "b" to null))
            .setMapWithMapWithValues(mapOf("a" to mapOf("aa" to 10)))
            .build()
        // Required field should be non-null
        person.income = 5
        assertEquals("123", person.id)

        val lnn: List<Int> = person.listWithValues
        val ln: List<Int?> = person.listWithNullableValues
        val llnn: List<List<Int>> = person.listWithListWithValues
        val mnn: Map<String, Int> = person.mapWithValues
        val mn: Map<String, Int?> = person.mapWithNullableValues
        val mmnn: Map<String, Map<String, Int>> = person.mapWithMapWithValues

        // Optional field should be nullable
        person.age = null
        assertNull(person.age)

        // Setting optional field should work
        person.age = 30
        assertEquals(30, person.age)
    }
}
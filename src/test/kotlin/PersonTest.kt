import com.example.Person
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class PersonTest {
    @Test
    fun testPersonNullability() {
        // Create a person with required fields
        val person = Person.newBuilder().setId("123")
            .setIncome(2)
            .build()
        // Required field should be non-null
        person.income = 5
        assertEquals("123", person.id)

        // Optional field should be nullable
        person.age = null
        assertNull(person.age)

        // Setting optional field should work
        person.age = 30
        assertEquals(30, person.age)
    }
}
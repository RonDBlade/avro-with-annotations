import com.example.Person
import com.example.PersonAndStatus
import com.example.Status
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PersonAndStatusTest {

    @Test
    fun testPersonNullability() {
        val personAndStatus = PersonAndStatus.newBuilder()
            .setPerson(
                Person.newBuilder().setId("!")
                    .setIncome(5)
                    .setListWithValues(listOf(1))
                    .setListWithNullableValues(listOf(2, null))
                    .setListWithListWithValues(listOf(listOf(10)))
                    .setMapWithValues(mapOf("a" to 1))
                    .setMapWithNullableValues(mapOf("a" to 1, "b" to null))
                    .setMapWithMapWithValues(mapOf("a" to mapOf("aa" to 10)))
                    .build()
            )
            .setStatus(Status.ACTIVE)
            .build()

        val personUpdate = Person.newBuilder()
            .setId("hai")
            .setAge(5)
            .setIncome(10)
            .setListWithValues(listOf(1))
            .setListWithNullableValues(listOf(2, null))
            .setListWithListWithValues(listOf(listOf(10)))
            .setMapWithValues(mapOf("a" to 1))
            .setMapWithNullableValues(mapOf("a" to 1, "b" to null))
            .setMapWithMapWithValues(mapOf("a" to mapOf("aa" to 10)))
            .build()
        personAndStatus.person = Person.newBuilder(personUpdate).build()
        personAndStatus.nullableStatus = Status.INACTIVE

        assertEquals(personUpdate, personAndStatus.person)
        assertEquals(Status.ACTIVE, personAndStatus.status)
        assertNull(personAndStatus.nullablePerson)
        assertEquals(Status.INACTIVE, personAndStatus.nullableStatus)
    }
}
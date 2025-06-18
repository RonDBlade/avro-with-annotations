import com.example.Person
import com.example.PersonAndStatus
import com.example.Status
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class PersonAndStatusTest {

    @Test
    fun testPersonNullability() {
        // Create a person with required fields
        Status.INACTIVE;
        val v = PersonAndStatus.newBuilder()
            .setPerson(Person.newBuilder().setId("!").setAge(1).build())
            .setStatus(Status.ACTIVE)
            .build();
    }
}
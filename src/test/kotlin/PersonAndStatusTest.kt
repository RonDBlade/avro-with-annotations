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
            .setStatus(Status.ACTIVE);

        val k = v.person;
        val d = v.personBuilder
        val r = v.nullablePerson
        val e = v.nullablePersonBuilder
        println(k)
        println(d.toString())

        // Conclusion:
        // Within the builder:
        // - All fields of objects (I.E, not primitives) themselves are nullable (if they were not set, then they are null)
        // - All fields of primitives nullability is the same as the way its defined in the schema file.
        // - The getters of the field of the builder should all be nullable
        // - The input of the setters of the field of the builder should all be nullable
        // - The output of the setters and clear-ers should be non-nullable.
    }
}
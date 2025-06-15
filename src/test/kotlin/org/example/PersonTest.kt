package org.example

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class PersonTest {
    @Test
    fun testPersonNullability() {
        // Create a person with required fields
        val person = Person()
        person.id = 1L
        person.name = "John Doe"
        
        // These should compile without null checks
        assertEquals(1L, person.id)
        assertEquals("John Doe", person.name)
        
        // Optional fields should be nullable
        person.email = null
        person.age = null
        
        // These should compile with null checks
        assertNull(person.email)
        assertNull(person.age)
        
        // Setting optional fields should work
        person.email = "john@example.com"
        person.age = 30
        
        assertEquals("john@example.com", person.email)
        assertEquals(30, person.age)
    }
} 
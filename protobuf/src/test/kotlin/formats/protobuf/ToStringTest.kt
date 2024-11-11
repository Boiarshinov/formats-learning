package formats.protobuf

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ToStringTest {

    @Test
    fun convertToString() {
        val message = simpleMessage {
            name = "some name"
            version = 5
        }

        assertEquals("""
            name: "some name"
            version: 5
            
        """.trimIndent(), message.toString())
    }

    @Test
    fun convertEmptyToString() {
        val message = simpleMessage { }

        assertEquals("", message.toString())
    }

    @Test
    fun convertDefaultsToString() {
        val message = simpleMessage {
            name = ""
            version = 0
        }

        assertEquals("", message.toString())
    }

    @Test
    fun convertOptionalToString() {
        val message = optionalFieldsMessage {
            intField = 0
            strField = ""
        }

        assertEquals("""
            int_field: 0
            str_field: ""
            
        """.trimIndent(), message.toString())
    }
}
package formats.protobuf

import formats.protobuf.Messages.SimpleMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class SimpleMessageKtTest {

    @Test
    fun backAndForth() {
        val simpleMessage = simpleMessage {
            name = "name"
            version = 15
        }

        val proto = simpleMessage.toByteArray()

        val parsed = SimpleMessage.parseFrom(proto)

        assertEquals(simpleMessage, parsed)
    }

    @Test
    fun copy() {
        val message = simpleMessage {
            name = "name"
            version = 15
        }

        val updatedMessage = message.copy {
            name = "new name"
        }

        assertNotEquals(message, updatedMessage)

        assertEquals("name", message.name)
        assertEquals(15, message.version)

        assertEquals("new name", updatedMessage.name)
        assertEquals(15, updatedMessage.version)
    }
}
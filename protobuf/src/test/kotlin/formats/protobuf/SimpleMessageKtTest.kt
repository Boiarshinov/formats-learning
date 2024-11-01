package formats.protobuf

import formats.protobuf.Messages.SimpleMessage
import org.junit.jupiter.api.Assertions.assertEquals
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
}
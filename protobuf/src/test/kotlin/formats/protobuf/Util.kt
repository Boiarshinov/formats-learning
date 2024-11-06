package formats.protobuf

import com.google.protobuf.Message
import org.apache.commons.codec.binary.Hex
import org.junit.jupiter.api.Assertions.assertEquals

fun assertHexEncoding(expected: String, message: Message) {
    val hexString = Hex.encodeHexString(message.toByteArray())
    assertEquals(expected, hexString)
}
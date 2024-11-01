package formats.protobuf;

import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleMessageTest {

    @Test
    void toByteArray() {
        Messages.SimpleMessage message = Messages.SimpleMessage.newBuilder()
            .setName("Name")
            .setVersion(15)
            .build();

        byte[] proto = message.toByteArray();

        assertEquals(
            "0a" // 00001 010 -> tag = 1, type = string
            + "04" // length = 4
            + "4e616d65" // value
            + "10" // 00011 000 -> tag = 2, type = varint
            + "0f", // 0000 1111 -> value
            Hex.encodeHexString(proto));
    }

    @Test
    void withoutValue() {
        Messages.SimpleMessage message = Messages.SimpleMessage.newBuilder()
            .setVersion(15)
            .build();

        byte[] proto = message.toByteArray();

        assertEquals(
            "10" // 00011 000 -> tag = 2, type = varint
            + "0f", // 0000 1111 -> value
            Hex.encodeHexString(proto));
    }

    @Test
    void withoutAllValues() {
        Messages.SimpleMessage message = Messages.SimpleMessage.newBuilder().build();

        byte[] proto = message.toByteArray();

        assertEquals("", Hex.encodeHexString(proto));
    }
}

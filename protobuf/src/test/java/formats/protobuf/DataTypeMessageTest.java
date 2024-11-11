package formats.protobuf;

import com.google.protobuf.Message;
import formats.protobuf.Messages.DataTypesMessage;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataTypeMessageTest {

    @SuppressWarnings("SimplifiableAssertion")
    @Test
    void defaultValues() {
        DataTypesMessage message = DataTypesMessage.getDefaultInstance();

        assertEquals(0, message.getInteger32());
        assertEquals(0, message.getUnsignedInt32());
        assertEquals(0, message.getSignedInt32());
        assertEquals(0, message.getFixedInt32());
        assertEquals(0, message.getSfixedInt32());

        assertEquals(0, message.getInteger64());
        assertEquals(0, message.getUnsignedInt64());
        assertEquals(0, message.getSignedInt64());
        assertEquals(0, message.getFixedInt64());
        assertEquals(0, message.getSfixedInt64());

        assertEquals(0, message.getFloatField());
        assertEquals(0, message.getDoubleField());

        assertEquals(false, message.getFlag());
        assertEquals(0, message.getRawBytesField().size());
        assertEquals("", message.getStrField());

        assertEquals(Messages.Status.STATUS_UNKNOWN, message.getEnumField());
        assertEquals(false, message.hasNested());

        assertEquals(0, message.getIntCollectionCount());
        assertEquals(0, message.getIntCollectionList().size());
        assertEquals(0, message.getStrCollectionCount());
        assertEquals(0, message.getStrCollectionList().size());

        assertEquals(false, message.hasStringVariant());
        assertEquals(false, message.hasIntegerVariant());
        assertEquals(false, message.hasBoolVariant());

        assertEquals(0, message.getMapFieldCount());
        assertEquals(0, message.getMapFieldMap().size());
    }

    @Test
    void emptyBytesForEmptyMessage() {
        DataTypesMessage message = DataTypesMessage.getDefaultInstance();

        assertHexEncoding("", message);
    }

    @Test
    void nestedMessageHasExistenceCheckMethod() {
        DataTypesMessage emptyMessage = DataTypesMessage.getDefaultInstance();
        assertFalse(emptyMessage.hasNested());

        DataTypesMessage message = DataTypesMessage.newBuilder()
            .setNested(Messages.NestedMessage.newBuilder().setName("name").build())
            .build();
        assertTrue(message.hasNested());
    }

    private void assertHexEncoding(String expected, Message message) {
        String hexString = Hex.encodeHexString(message.toByteArray());
        assertEquals(expected, hexString);
    }
}

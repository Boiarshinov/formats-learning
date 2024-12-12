package formats.messagepack;

import org.junit.jupiter.api.Test;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.ImmutableValue;
import org.msgpack.value.impl.ImmutableBinaryValueImpl;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessagePackBaseTest {

    @Test
    public void primitiveTypesTest() throws IOException {
        boolean expectedBoolean = true;
        byte expectedByte = 1;
        short expectedShort = 2;
        int expectedInt = 3;
        long expectedLong = 4;
        float expectedFloat = 5.0f;
        double expectedDouble = 6.0;

        try (MessageBufferPacker packer = MessagePack.newDefaultBufferPacker()) {
            packer.packBoolean(expectedBoolean)
                    .packByte(expectedByte)
                    .packShort(expectedShort)
                    .packInt(expectedInt)
                    .packLong(expectedLong)
                    .packFloat(expectedFloat)
                    .packDouble(expectedDouble);

            try (MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(packer.toByteArray())) {
                assertEquals(expectedBoolean, unpacker.unpackBoolean());
                assertEquals(expectedByte, unpacker.unpackByte());
                assertEquals(expectedShort, unpacker.unpackShort());
                assertEquals(expectedInt, unpacker.unpackInt());
                assertEquals(expectedLong, unpacker.unpackLong());
                assertEquals(expectedFloat, unpacker.unpackFloat());
                assertEquals(expectedDouble, unpacker.unpackDouble());
            }
        }
    }

    @Test
    public void referenceTypesTest() throws IOException {
        BigInteger expectedBigInteger = BigInteger.valueOf(10);
        String expectedString = "string";
        Instant expectedInstant = Instant.now();

        try (MessageBufferPacker packer = MessagePack.newDefaultBufferPacker()) {
            packer.packBigInteger(expectedBigInteger)
                    .packString(expectedString)
                    .packTimestamp(expectedInstant);

            try (MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(packer.toByteArray())) {
                assertEquals(expectedBigInteger, unpacker.unpackBigInteger());
                assertEquals(expectedString, unpacker.unpackString());
                assertEquals(expectedInstant, unpacker.unpackTimestamp());
            }
        }
    }

    @Test
    public void arrayTest() throws IOException {
        String[] expectedArray = {"xxx-xxxx", "yyy-yyyy"};
        try (MessageBufferPacker packer = MessagePack.newDefaultBufferPacker()) {
            packer.packArrayHeader(expectedArray.length)
                    .packString(expectedArray[0])
                    .packString(expectedArray[1]);

            try (MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(packer.toByteArray())) {
                int arraySize = unpacker.unpackArrayHeader();
                String[] actualArray = new String[arraySize];
                for (int i = 0; i < arraySize; i++) {
                    actualArray[i] = unpacker.unpackString();
                }
                assertArrayEquals(expectedArray, actualArray);
            }
        }
    }

    @Test
    public void mapTest() throws IOException {
        Map<String, Integer> expectedMap = Map.of("key1", 1, "key2", 2);
        try (MessageBufferPacker packer = MessagePack.newDefaultBufferPacker()) {
            packer.packMapHeader(expectedMap.size());
            for (Map.Entry<String, Integer> entry : expectedMap.entrySet()) {
                packer.packString(entry.getKey()).packInt(entry.getValue());
            }

            try (MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(packer.toByteArray())) {
                assertTrue(unpacker.hasNext());
                Map<String, Integer> actualMap = new HashMap<>();
                int pairs = unpacker.unpackMapHeader();
                for (int i = 0; i < pairs; i++) {
                    actualMap.put(unpacker.unpackString(), unpacker.unpackInt());
                }
                assertEquals(expectedMap, actualMap);
                assertFalse(unpacker.hasNext());
            }
        }
    }

    @Test
    public void nullTest() throws IOException {
        try (MessageBufferPacker packer = MessagePack.newDefaultBufferPacker()) {
            packer.packNil();

            // Десериализация
            try (MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(packer.toByteArray())) {
                assertTrue(unpacker.tryUnpackNil());
            }
        }
    }

    @Test
    public void binaryValueTest() throws IOException {
        try (MessageBufferPacker packer = MessagePack.newDefaultBufferPacker()) {
            byte[] expectedBytes = Files.readAllBytes(Path.of("src", "test", "resources", "img.png"));
            packer.packValue(new ImmutableBinaryValueImpl(expectedBytes));

            try (MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(packer.toByteArray())) {
                ImmutableValue immutableValue = unpacker.unpackValue();
                byte[] actualBytes = immutableValue.asBinaryValue().asByteArray();
                assertArrayEquals(expectedBytes, actualBytes);
            }
        }
    }
}

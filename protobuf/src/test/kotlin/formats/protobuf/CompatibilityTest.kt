package formats.protobuf

import formats.protobuf.Compatibility.NewMessage
import formats.protobuf.Compatibility.OldMessage
import org.apache.commons.codec.binary.Hex
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class CompatibilityTest {

    @DisplayName("Старый код может работать с новой версией сообщения")
    @Test
    fun forwardCompatibility() {
        val newMessage = newMessage {
            numericField = 1
            repeatedField.addAll(listOf("1", "2", "3"))
            status = Compatibility.NewMessage.Status.PAUSED
        }

        val oldMessage = OldMessage.parseFrom(newMessage.toByteArray())

        assertEquals(false, oldMessage.terminated)
        assertEquals(1, oldMessage.numericField)
        assertEquals("3", oldMessage.singularField) //last wins
        // 2003 -> 00100 000 0000 0011 -> tag = 4, type = VARINT, value = 3
        assertEquals("2003", Hex.encodeHexString(oldMessage.unknownFields.toByteArray()))
        assertEquals(1, oldMessage.unknownFields.asMap().size)
    }

    @DisplayName("Новый код может работать со старой версией сообщения")
    @Test
    fun backwardCompatibility() {
        val oldMessage = oldMessage {
            terminated = true
            numericField = 1
            singularField = "1"
        }

        val newMessage = NewMessage.parseFrom(oldMessage.toByteArray())

        assertEquals(Compatibility.NewMessage.Status.UNKNOWN, newMessage.status)
        assertEquals(1, newMessage.numericField)
        assertEquals(1, newMessage.repeatedFieldList.size)
        assertEquals("1", newMessage.repeatedFieldList[0])
        // 0801 -> 00001 000 0000 0001 -> tag = 1, type = VARINT, value = 1
        assertEquals("0801", Hex.encodeHexString(newMessage.unknownFields.toByteArray()))
        assertEquals(1, newMessage.unknownFields.asMap().size)
    }

    @DisplayName("Неизвестные поля не стираются при декодинге/энкодинге старой версии")
    @Test
    fun unknownFieldsNotCleared() {
        val newMessage = newMessage {
            status = Compatibility.NewMessage.Status.PAUSED
        }
        val oldMessage = OldMessage.parseFrom(newMessage.toByteArray())
        assertEquals(1, oldMessage.unknownFields.asMap().size)
        val updatedOldMessage = oldMessage.copy { terminated = true }

        val parsedNewMessage = NewMessage.parseFrom(updatedOldMessage.toByteArray())
        assertEquals(Compatibility.NewMessage.Status.PAUSED, parsedNewMessage.status)
        assertEquals(1, parsedNewMessage.unknownFields.asMap().size)
    }

    @DisplayName("Старшие биты числового значения отбрасываются при изменении int64 -> int32")
    @Test
    fun trimHighestBitsOfInt() {
        val newMessage = newMessage {
            numericField = Long.MAX_VALUE
        }

        val oldMessage = OldMessage.parseFrom(newMessage.toByteArray())

        assertEquals(-1, oldMessage.numericField)
        // Бинарное представление изменилось.
        // Забавно, что старое сообщение занимает больше места
        assertHexEncoding("10ffffffffffffffff7f", newMessage)
        assertHexEncoding("10ffffffffffffffffff01", oldMessage)

        val parsedNewMessage = NewMessage.parseFrom(oldMessage.toByteArray())
        // Старый код может испортить те данные, которые он даже не трогал
        assertNotEquals(newMessage.numericField, parsedNewMessage.numericField)
        assertEquals(-1, parsedNewMessage.numericField)
    }

}

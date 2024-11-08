import com.github.avrokotlin.avro4k.Avro
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SimpleMessageTest {
    @Test
    fun backAndForth() {
        val data = SimpleMessage("avro", "1")
        val bytes = Avro.encodeToByteArray(data)
        val obj = Avro.decodeFromByteArray<SimpleMessage>(bytes)

        assertEquals(data, obj)
    }

    @Test
    fun backAndForthWithNullField() {
        // Serializing objects
        val data = SimpleMessage(null, "1")
        val bytes = Avro.encodeToByteArray(data)
        val obj = Avro.decodeFromByteArray<SimpleMessage>(bytes)

        assertEquals(data, obj)
    }

    @Test
    fun backAndForthWithNullFields() {
        // Serializing objects
        val data = SimpleMessage(null, null)
        val bytes = Avro.encodeToByteArray(data)
        val obj = Avro.decodeFromByteArray<SimpleMessage>(bytes)

        assertEquals(data, obj)
    }
}
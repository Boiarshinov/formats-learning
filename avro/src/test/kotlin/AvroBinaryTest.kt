import com.github.avrokotlin.avro4k.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToByteArray
import org.apache.avro.SchemaNormalization
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.time.Instant
import kotlin.io.path.Path

// https://avro.apache.org/docs/1.12.0/specification/#single-object-encoding
@OptIn(ExperimentalSerializationApi::class)
class AvroBinaryTest {
    val orderEvent =
        OrderEvent(
            OrderId("123"),
            Instant.now().truncatedTo(java.time.temporal.ChronoUnit.MILLIS),
            42.0
        )
    private val schema = Avro.schema(OrderEvent.serializer())
    private val schemas = mapOf(SchemaNormalization.parsingFingerprint64(schema) to schema)
    private val avroSingleObject = AvroSingleObject(schemas::get)

    @Test
    fun singleObjectBytesTest() {
        val bytes = avroSingleObject.encodeToByteArray(orderEvent)

        assertEquals(bytes[0], 0xC3.toByte())
        assertEquals(bytes[1], 1)
        assertArrayEquals(bytes.sliceArray(2..9), SchemaNormalization.parsingFingerprint("CRC-64-AVRO", schema))
        assertArrayEquals(bytes.sliceArray(10 until bytes.size), Avro.encodeToByteArray(orderEvent))
    }

    @Test
    fun singleObjectTest() {
        val bytes =
            byteArrayOf(0xC3.toByte(), 1) +
                    SchemaNormalization.parsingFingerprint("CRC-64-AVRO", schema) +
                    Avro.encodeToByteArray(orderEvent)

        val decoded = avroSingleObject.decodeFromByteArray<OrderEvent>(bytes)
        assertEquals(orderEvent, decoded)
    }

    @Test
    fun containerTest() {
        // Serializing objects
        val valuesToEncode = setOf(
            SimpleMessage("test1", "1"),
            SimpleMessage("test2", "2"),
            SimpleMessage("test3", "3"),
        )
        Files.newOutputStream(Path("src/test/resources/container.bin")).use { fileStream ->
            AvroObjectContainer.openWriter<SimpleMessage>(fileStream).use { writer ->
                valuesToEncode.forEach { writer.writeValue(it) }
            }
        }

        // Deserializing objects
        val resultValues = Files.newInputStream(Path("src/test/resources/container.bin")).use { fileStream ->
            AvroObjectContainer.decodeFromStream<SimpleMessage>(fileStream).toSet()
        }

        assertEquals(valuesToEncode, resultValues)
    }

    @Serializable
    data class OrderEvent(
        val orderId: OrderId,
        @Contextual val date: Instant,
        val amount: Double
    )

    @Serializable
    @JvmInline
    value class OrderId(val value: String)
}
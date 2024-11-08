import com.github.avrokotlin.avro4k.Avro
import com.github.avrokotlin.avro4k.schema
import org.apache.avro.Schema
import org.apache.avro.SchemaCompatibility
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class CompatibilityTest {

    @DisplayName("Новый код может работать со старой версией сообщения")
    @Test
    fun backwardCompatibility() {
        val schema = Schema.Parser().parse(javaClass.getResourceAsStream("DataMessage.json"))
        // Добавлены поля bigDecimalAsString, bigDecimalAsBytes, enumField
        val dataMessage = Avro.schema<DataMessage>()
        val compatibility = SchemaCompatibility.checkReaderWriterCompatibility(schema, dataMessage)
        assertEquals(SchemaCompatibility.SchemaCompatibilityType.COMPATIBLE, compatibility.result.compatibility)
    }

    @DisplayName("Старый код может работать с новой версией сообщения")
    @Test
    fun forwardCompatibility() {
        // В схеме добавлено поле newVersionField
        val schema = Schema.Parser().parse(javaClass.getResourceAsStream("DataMessage_v2.json"))
        val dataMessage = Avro.schema<DataMessage>()
        val compatibility = SchemaCompatibility.checkReaderWriterCompatibility(schema, dataMessage)
        assertEquals(SchemaCompatibility.SchemaCompatibilityType.COMPATIBLE, compatibility.result.compatibility)
    }
}
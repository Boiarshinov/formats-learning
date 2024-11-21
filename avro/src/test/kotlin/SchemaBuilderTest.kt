import com.github.avrokotlin.avro4k.Avro
import com.github.avrokotlin.avro4k.schema
import org.apache.avro.SchemaBuilder
import org.apache.avro.SchemaCompatibility
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class SchemaBuilderTest {
    @Test
    fun builderTest() {
        val buildSchema = SchemaBuilder.builder()
            .record("DataMessage")
            .fields()
            .optionalString("text")
            .requiredDouble("doubleField")
            .requiredBoolean("flag")
            .requiredFloat("floatField")
            .requiredLong("longField")
            .requiredInt("intField")
            .requiredInt("shortField")
            .requiredInt("byteField")
//            .requiredBytes("byteField")
            .name("enumField")
                .type(Avro.schema<MyEnum>())
                .withDefault("DIAMONDS")
            .name("mapField")
                .type().map().values().stringType().noDefault()
            .name("bigDecimalAsString").type().stringType().noDefault()
            .name("bigDecimalAsBytes").type()
                .bytesBuilder()
                    .prop("precision", 3)
                    .prop("scale", 2)
                    .prop("logicalType", "decimal")
                .endBytes()
                .noDefault()
            .endRecord()

        val original = Avro.schema<DataMessage>()
        val compatibility = SchemaCompatibility.checkReaderWriterCompatibility(buildSchema, original)

        assertEquals(compatibility.result.compatibility, SchemaCompatibility.SchemaCompatibilityType.COMPATIBLE)
    }
}
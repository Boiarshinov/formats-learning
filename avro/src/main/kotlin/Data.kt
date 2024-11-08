import com.github.avrokotlin.avro4k.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class SimpleMessage(val name: String?, val version: String?)

@Serializable
data class DataMessage(
    val text: String?,
    val doubleField: Double,
    val flag: Boolean,
    val floatField: Float,
    val longField: Long,
    val intField: Int,
    val shortField: Short,
    val byteField: Byte,
    val enumField: MyEnum,
    val mapField: Map<Int, String>,
    @Contextual
    @AvroStringable
    val bigDecimalAsString: BigDecimal,
    @Contextual
    @AvroDecimal(scale = 2, precision = 3)
    val bigDecimalAsBytes: BigDecimal,
)

@Serializable
@AvroAlias("MyEnum")
enum class MyEnum {
    SPADES,
    @AvroEnumDefault
    DIAMONDS,
    CLUBS,
}

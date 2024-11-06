package formats.protobuf

import com.google.protobuf.ByteString
import org.junit.jupiter.api.Test

/**
 * Data types:
 * 000 - VARINT -        int32, int64, uint32, uint64, sint32, sint64, bool, enum
 * 001 - I64    -        fixed64, sfixed64, double
 * 010 - LEN    -        string, bytes, embedded messages, packed repeated fields
 * 011 - SGROUP -        group start (deprecated)
 * 100 - EGROUP -        group end (deprecated)
 * 101 - I32    -        fixed32, sfixed32, float
 */
class DataTypesBinaryFormatTest {


    @Test
    fun int32BinaryFormat() {
        // 08 -> 00001 000 -> tag = 1, type = VARINT
        // 01 -> 0000 0001 -> value
        assertHexEncoding("08" + "01", dataTypesMessage { integer32 = 1 })
        // 7f -> 0111 1111
        assertHexEncoding("08" + "7f", dataTypesMessage { integer32 = 127 })
        // 8001 -> 1000 0000 0000 0001 -> Если старший бит в байте выставлен в 1, значит будет еще один байт
        assertHexEncoding("08" + "8001", dataTypesMessage { integer32 = 128 })
        // 8101 -> 1000 0001 0000 0001
        assertHexEncoding("08" + "8101", dataTypesMessage { integer32 = 129 })
        // 8002 -> 1000 0000 0000 0010 -> Второй байт определяет множитель для 128
        assertHexEncoding("08" + "8002", dataTypesMessage { integer32 = 256 })
        assertHexEncoding("08" + "b90a", dataTypesMessage { integer32 = 1337 }) // пример из книги с кабанчиком
        assertHexEncoding("08" + "ffffffffffffffffff01", dataTypesMessage { integer32 = -1 })
        assertHexEncoding("08" + "ffffffff07", dataTypesMessage { integer32 = Int.MAX_VALUE })
        assertHexEncoding("08" + "80808080f8ffffffff01", dataTypesMessage { integer32 = Int.MIN_VALUE })
        // в худшем случае тратится 10 байт на кодирование 32-битного значения
    }

    @Test
    fun uint32BinaryFormat() {
        // 10 -> 00010 000 -> tag = 2, type = VARINT
        assertHexEncoding("10" + "01", dataTypesMessage { unsignedInt32 = 1 })
        assertHexEncoding("10" + "ffffffff0f", dataTypesMessage { unsignedInt32 = -1 })
        assertHexEncoding("10" + "ffffffff07", dataTypesMessage { unsignedInt32 = Int.MAX_VALUE })
        assertHexEncoding("10" + "8080808008", dataTypesMessage { unsignedInt32 = Int.MIN_VALUE })
        // 5 байт на большие значения. 5 байт даже на маленькие отрицательные значения
    }

    @Test
    fun sint32BinaryFormat() {
        // 18 -> 00011 000 -> tag = 3, type = VARINT
        assertHexEncoding("18" + "02", dataTypesMessage { signedInt32 = 1 })
        assertHexEncoding("18" + "01", dataTypesMessage { signedInt32 = -1 })
        assertHexEncoding("18" + "feffffff0f", dataTypesMessage { signedInt32 = Int.MAX_VALUE })
        assertHexEncoding("18" + "ffffffff0f", dataTypesMessage { signedInt32 = Int.MIN_VALUE })
        // На маленькие отрицательные значения уходит не больше, чем на маленькие положительные.
        // Достигается благодаря тому, что знаковый бит - самый правый.
        // Поэтому и значение 1 превратилось в 2 -> 0000001_0
        // Как кодируются большие значения я не понял. Что-то хитрое
    }

    @Test
    fun fixed32BinaryFormat() {
        // 25 -> 00100 101 -> tag = 4, type = I32
        assertHexEncoding("25" + "01000000", dataTypesMessage { fixedInt32 = 1 })
        assertHexEncoding("25" + "ffffffff", dataTypesMessage { fixedInt32 = -1 })
        assertHexEncoding("25" + "ffffff7f", dataTypesMessage { fixedInt32 = Int.MAX_VALUE })
        assertHexEncoding("25" + "00000080", dataTypesMessage { fixedInt32 = Int.MIN_VALUE })
    }

    @Test
    fun sfixed32BinaryFormat() {
        // 2d -> 00101 101 -> tag = 5, type = I32
        assertHexEncoding("2d" + "01000000", dataTypesMessage { sfixedInt32 = 1 })
        assertHexEncoding("2d" + "ffffffff", dataTypesMessage { sfixedInt32 = -1 })
        assertHexEncoding("2d" + "ffffff7f", dataTypesMessage { sfixedInt32 = Int.MAX_VALUE })
        assertHexEncoding("2d" + "00000080", dataTypesMessage { sfixedInt32 = Int.MIN_VALUE })
        //Никакого отличия от fixed32
    }

    @Test
    fun floatBinaryFormat() {
        // 5d -> 01011 101 -> tag = 11, type = I32
        assertHexEncoding("5d" + "0000803f", dataTypesMessage { floatField = 1.0f })
        assertHexEncoding("5d" + "000080bf", dataTypesMessage { floatField = -1.0f })
    }

    @Test
    fun doubleBinaryFormat() {
        // 61 -> 01100 001 -> tag = 12, type = I64
        assertHexEncoding("61" + "000000000000f03f", dataTypesMessage { doubleField = 1.0 })
        assertHexEncoding("61" + "000000000000f0bf", dataTypesMessage { doubleField = -1.0 })
    }

    @Test
    fun boolBinaryFormat() {
        // 68 -> 01101 000 -> tag = 13, type = VARINT
        assertHexEncoding("68" + "01", dataTypesMessage { flag = true })
        assertHexEncoding("", dataTypesMessage { flag = false })
        // Получается, что для хранения boolean значения лучше называть их так, чтобы они чаще принимали значение false.
        // Для optional параметров разницы не будет.
    }

    @Test
    fun bytesBinaryFormat() {
        // 72 -> 01110 010 -> tag = 14, type = LEN
        assertHexEncoding("72" + "01" + "01", dataTypesMessage { rawBytesField = ByteString.fromHex("01") })
        assertHexEncoding("72" + "04" + "ffffffff", dataTypesMessage { rawBytesField = ByteString.fromHex("ffffffff") })
    }

    @Test
    fun stringBinaryFormat() {
        // 7a -> 01111 010 -> tag = 15, type = LEN
        assertHexEncoding("7a" + "01" + "31", dataTypesMessage { strField = "1" })
        assertHexEncoding("7a" + "01" + "61", dataTypesMessage { strField = "a" })
        assertHexEncoding("7a" + "01" + "7a", dataTypesMessage { strField = "z" })
        assertHexEncoding("7a" + "02" + "d18f", dataTypesMessage { strField = "я" })
        assertHexEncoding("7a" + "04" + "f09f9880", dataTypesMessage { strField = "\uD83D\uDE00" })
        // Почему энкодинг не совпадает с кодами символов в UTF-8?
        // У 'я' должно быть \u044f
    }

    @Test
    fun enumBinaryFormat() {
        // 8001 -> 10000 000 0000 0001 -> tag = 16, type = VARINT
        // Мы достигли значения 16. Оно уже не влезает в 4 бита.
        // Поэтому в старший бит первого байта выставляется 1, а к значению добавляется второй байт.
        // Значение тэга определяется так: 2-5 биты первого байта - это младшие биты значения.
        // 2-8 биты второго байта - это старшие биты значения.
        // Если в 1 бите второго байта выставлена 1, то добавляется третий байт и т.д.
        assertHexEncoding("8001" + "01", dataTypesMessage { enumField = Messages.Status.SUCCESS })
        assertHexEncoding("8001" + "02", dataTypesMessage { enumField = Messages.Status.FAIL })
    }

    @Test
    fun nestedMessageBinaryFormat() {
        // 8a01 -> 10001 010 0000 0001 -> tag = 17, type = LEN
        assertHexEncoding("8a01" + "03" + "0a0161", dataTypesMessage { nested = nestedMessage { name = "a" } })
    }

    @Test
    fun repeatedInt32BinaryFormat() {
        // 9201 -> 10010 010 0000 0001 -> tag = 18, type = LEN
        assertHexEncoding("", dataTypesMessage { intCollection.addAll(emptySet()) })
        assertHexEncoding("9201" + "02" + "0102", dataTypesMessage { intCollection.addAll(setOf(1, 2)) })
        // Не повторяется заголовок 92
    }

    @Test
    fun repeatedStrBinaryFormat() {
        // 9a01 -> 10011 010 0000 0001 -> tag = 19, type = LEN
        assertHexEncoding("", dataTypesMessage { strCollection.addAll(emptySet()) })
        assertHexEncoding("9a01" + "0131" + "9a01" + "0161", dataTypesMessage { strCollection.addAll(setOf("1", "a")) })
        // Заголовок 9a повторяется для каждого элемента
    }

    @Test
    fun oneOfBinaryFormat() {
        assertHexEncoding("a201" + "0131", dataTypesMessage { stringVariant = "1" })
        assertHexEncoding("a801" + "01", dataTypesMessage { integerVariant = 1 })
        assertHexEncoding("b001" + "01", dataTypesMessage { boolVariant = true })
        assertHexEncoding("ba01" + "030a0131", dataTypesMessage { nestedVariant = nestedMessage { name = "1" } })
    }

    @Test
    fun mapBinaryFormat() {
        // c201 -> 11000 010 0000 0001 -> tag = 24, type = LEN
        // 08 -> количество байт, отведенное на запись
        // 0a -> объявление ключа -> 00001 010 -> tag = 1, type = LEN
        // 036b6579 -> length = 3, value = key
        // 12 -> объявление значения -> 00010 010 -> tag = 2, type = LEN
        // 0161 -> length = 1, value = a
        assertHexEncoding("c201" + "08" + "0a" + "036b6579" + "12" + "0161", dataTypesMessage { mapField.put("key", "a") })

        // 036b6579 -> length = 3, value = key
        // 00 -> length = 0
        assertHexEncoding("c201" + "07" + "0a" + "036b6579" + "12" + "00", dataTypesMessage { mapField.put("key", "") })

        // 0131 -> length = 1, value = a
        assertHexEncoding("c201" + "06" + "0a" + "0131" + "12" + "0131", dataTypesMessage { mapField.put("1", "1") })

        assertHexEncoding(
            "c201"
                    + "09" + "0a" + "046b657931" + "12" + "0161" // 046b657931 -> length = 4, value = key1
                    + "c201" // Каждая пара значений начинается снова с объявления ссылки на мапу
                    + "09" + "0a" + "046b657932" + "12" + "0161", // 046b657932 -> length = 4, value = key2
            dataTypesMessage { mapField.putAll(mapOf(
                "key1" to "a",
                "key2" to "a"
            )) }
        )
    }
}
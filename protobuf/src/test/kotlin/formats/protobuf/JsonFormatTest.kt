package formats.protobuf

import com.google.protobuf.util.JsonFormat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class JsonFormatTest {

    @Test
    fun toJson() {
        val message = simpleMessage {
            name = "some name"
            version = 5
        }

        val printer = JsonFormat.printer()

        //language=JSON
        assertEquals("""
            {
              "name": "some name",
              "version": 5
            }
        """.trimIndent(), printer.print(message))
    }

    @Test
    fun toJsonMore() {
        val message = dataTypesMessage {
            integer32 = 32
            integer64 = 64
            doubleField = 0.15
            flag = true
            strField = "lol"
            enumField = Messages.Status.STATUS_SUCCESS
            nested = nestedMessage { name = "nested" }
            intCollection.addAll(listOf(1, 3, 3, 7))
            strCollection.addAll(listOf("a", "b"))
            integerVariant = 42
            mapField.putAll(
                mapOf(
                    "key1" to "value1",
                    "key2" to "value2"
                )
            )
        }

        val printer = JsonFormat.printer()

        //language=JSON
        assertEquals("""
            {
              "integer32": 32,
              "integer64": "64",
              "doubleField": 0.15,
              "flag": true,
              "strField": "lol",
              "enumField": "STATUS_SUCCESS",
              "nested": {
                "name": "nested"
              },
              "intCollection": [1, 3, 3, 7],
              "strCollection": ["a", "b"],
              "integerVariant": 42,
              "mapField": {
                "key1": "value1",
                "key2": "value2"
              }
            }
        """.trimIndent(), printer.print(message))
    }

    @Test
    fun printDefaultValues() {
        val message = simpleMessage { }

        val printer = JsonFormat.printer()
            .includingDefaultValueFields()

        //language=JSON
        assertEquals("""
            {
              "name": "",
              "version": 0
            }
        """.trimIndent(), printer.print(message))
    }
}
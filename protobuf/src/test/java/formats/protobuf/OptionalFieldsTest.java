package formats.protobuf;

import formats.protobuf.Messages.NotOptionalFieldsMessage;
import formats.protobuf.Messages.OptionalFieldsMessage;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OptionalFieldsTest {

    @DisplayName("hasFieldName() возвращает false, когда поле не было заполнено")
    @Test
    void emptyMessage() {
        OptionalFieldsMessage emptyMessage = OptionalFieldsMessage.newBuilder().build();

        assertFalse(emptyMessage.hasIntField());
        assertFalse(emptyMessage.hasStrField());
        assertEquals(0, emptyMessage.getIntField());
        assertEquals("", emptyMessage.getStrField());
    }

    @DisplayName("hasFieldName() возвращает true, когда поле было заполнено")
    @Test
    void filledMessage() {
        OptionalFieldsMessage filledMessage = OptionalFieldsMessage.newBuilder()
            .setIntField(1)
            .setStrField("value")
            .build();

        assertTrue(filledMessage.hasIntField());
        assertTrue(filledMessage.hasStrField());
        assertEquals(1, filledMessage.getIntField());
        assertEquals("value", filledMessage.getStrField());
    }

    @DisplayName("hasFieldName() возвращает true, когда поле было заполнено дефолтными значениями")
    @Test
    void defaultValuesMessage() {
        OptionalFieldsMessage defaultValuesMessage = OptionalFieldsMessage.newBuilder()
            .setIntField(0)
            .setStrField("")
            .build();

        assertTrue(defaultValuesMessage.hasIntField());
        assertTrue(defaultValuesMessage.hasStrField());
    }

    @DisplayName("Информация об отсутствии/наличии поля не затирается при сериализации и десериализации")
    @Test
    void optionalFields() throws Exception {
        OptionalFieldsMessage emptyMessage = OptionalFieldsMessage.newBuilder().build();

        OptionalFieldsMessage parsedEmptyMessage = OptionalFieldsMessage.parseFrom(emptyMessage.toByteArray());

        assertFalse(parsedEmptyMessage.hasIntField());
        assertFalse(parsedEmptyMessage.hasStrField());
        assertEquals(0, parsedEmptyMessage.getIntField());
        assertEquals("", parsedEmptyMessage.getStrField());


        OptionalFieldsMessage defaultValuesMessage = OptionalFieldsMessage.newBuilder()
            .setIntField(0)
            .setStrField("")
            .build();

        OptionalFieldsMessage parsedDefaultValuesMessage = OptionalFieldsMessage.parseFrom(defaultValuesMessage.toByteArray());

        assertTrue(parsedDefaultValuesMessage.hasIntField());
        assertTrue(parsedDefaultValuesMessage.hasStrField());
        assertEquals(0, parsedDefaultValuesMessage.getIntField());
        assertEquals("", parsedDefaultValuesMessage.getStrField());
    }

    @DisplayName("Сравнение бинарного представления опциональных и неопциональных полей")
    @Test
    void binaryComparison() {
        OptionalFieldsMessage optFilledMessage = OptionalFieldsMessage.newBuilder()
            .setIntField(1)
            .setStrField("value")
            .build();
        NotOptionalFieldsMessage regularFilledMessage = NotOptionalFieldsMessage.newBuilder()
            .setIntField(1)
            .setStrField("value")
            .build();

        assertEquals(
            Hex.encodeHexString(regularFilledMessage.toByteArray()),
            Hex.encodeHexString(optFilledMessage.toByteArray())
        );
        assertEquals("0801120576616c7565", Hex.encodeHexString(optFilledMessage.toByteArray()));
        assertEquals("0801120576616c7565", Hex.encodeHexString(regularFilledMessage.toByteArray()));
    }

    @DisplayName("Сравнение бинарного представления опциональных и неопциональных полей, когда они не заполнены")
    @Test
    void binaryComparisonEmptyValues() {
        assertEquals("", Hex.encodeHexString(OptionalFieldsMessage.getDefaultInstance().toByteArray()));
        assertEquals("", Hex.encodeHexString(NotOptionalFieldsMessage.getDefaultInstance().toByteArray()));
    }

    @DisplayName("Сравнение бинарного представления опциональных и неопциональных полей, когда они заполнены дефолтными значениями")
    @Test
    void binaryComparisonDefaultValues() {
        OptionalFieldsMessage optFilledMessage = OptionalFieldsMessage.newBuilder()
            .setIntField(0)
            .setStrField("")
            .build();
        NotOptionalFieldsMessage regularFilledMessage = NotOptionalFieldsMessage.newBuilder()
            .setIntField(0)
            .setStrField("")
            .build();

        assertNotEquals(
            Hex.encodeHexString(regularFilledMessage.toByteArray()),
            Hex.encodeHexString(optFilledMessage.toByteArray())
        );
        assertEquals(
            "08" // 00001 000 -> tag = 1, type = varint
            + "00" // 00000000 -> value = 0
            + "12" // 00010 010 -> tag = 2, type = string
            + "00", // 00000000 -> length = 0
            Hex.encodeHexString(optFilledMessage.toByteArray()));
        assertEquals("", Hex.encodeHexString(regularFilledMessage.toByteArray()));
    }

    @DisplayName("Опциональные поля бинарно совместимы с неопциональными")
    @Test
    void binaryCompatibility() throws Exception {
        OptionalFieldsMessage optFilledMessage = OptionalFieldsMessage.newBuilder()
            .setIntField(1)
            .setStrField("value")
            .build();

        NotOptionalFieldsMessage parsedOptMessage = NotOptionalFieldsMessage.parseFrom(optFilledMessage.toByteArray());
        assertEquals(1, parsedOptMessage.getIntField());
        assertEquals("value", parsedOptMessage.getStrField());


        NotOptionalFieldsMessage regularFilledMessage = NotOptionalFieldsMessage.newBuilder()
            .setIntField(1)
            .setStrField("value")
            .build();

        OptionalFieldsMessage parsedNotOptMessage = OptionalFieldsMessage.parseFrom(regularFilledMessage.toByteArray());
        assertEquals(1, parsedNotOptMessage.getIntField());
        assertEquals("value", parsedNotOptMessage.getStrField());
    }

    /**
     * Моделируем ситуацию, когда у нас была старая версия protoc, в которой отсутствовали опциональные поля.
     * Затем мы перешли на новую версию и добавили везде ключевое слово optional.
     * Надо убедиться, что поддерживается прямая совместимость: старый код корректно обрабатывает данные,
     * описанные с помощью нового формата.
     */
    @DisplayName("Опциональные поля прямо совместимы с неопциональными при дефолтных значениях")
    @Test
    void forwardCompatibility() throws Exception {
        OptionalFieldsMessage optFilledMessage = OptionalFieldsMessage.newBuilder()
            .setIntField(0)
            .setStrField("")
            .build();

        NotOptionalFieldsMessage parsedOptMessage = NotOptionalFieldsMessage.parseFrom(optFilledMessage.toByteArray());
        assertEquals(0, parsedOptMessage.getIntField());
        assertEquals("", parsedOptMessage.getStrField());
    }

    /**
     * Моделируем ситуацию, когда у нас была старая версия protoc, в которой отсутствовали опциональные поля.
     * Затем мы перешли на новую версию и добавили везде ключевое слово optional.
     * Надо убедиться, что поддерживается обратная совместимость: новый код корректно обрабатывает данные,
     * описанные с помощью старого формата.
     */
    @DisplayName("Опциональные поля обратно совместимы с неопциональными при дефолтных значениях")
    @Test
    void backwardCompatibility() throws Exception {
        NotOptionalFieldsMessage regularFilledMessage = NotOptionalFieldsMessage.newBuilder()
            .setIntField(0)
            .setStrField("")
            .build();

        OptionalFieldsMessage parsedNotOptMessage = OptionalFieldsMessage.parseFrom(regularFilledMessage.toByteArray());
        assertFalse(parsedNotOptMessage.hasIntField());
        assertFalse(parsedNotOptMessage.hasStrField());
        assertEquals(0, parsedNotOptMessage.getIntField());
        assertEquals("", parsedNotOptMessage.getStrField());
    }
}

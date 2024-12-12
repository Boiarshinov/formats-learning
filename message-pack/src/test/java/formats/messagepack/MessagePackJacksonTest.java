package formats.messagepack;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.msgpack.jackson.dataformat.MessagePackMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessagePackJacksonTest {

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final ObjectMapper msgPackMapper = new MessagePackMapper();

    @Test
    public void testSerializationSize() throws Exception {
        // Создаем объект для теста
        UserDto user = new UserDto("John Doe", 30, "john.doe@example.com");

        // JSON сериализация
        byte[] jsonData = jsonMapper.writeValueAsBytes(user);
        System.out.println("JSON serialized size: " + jsonData.length + " bytes");

        // MessagePack сериализация
        byte[] msgPackData = msgPackMapper.writeValueAsBytes(user);
        System.out.println("MessagePack serialized size: " + msgPackData.length + " bytes");

        // Проверяем, что MessagePack компактнее JSON
        assertTrue(msgPackData.length < jsonData.length, "MessagePack должен быть компактнее JSON");
    }

    @Test
    public void testBackwardCompatibilitySerializationSize() throws Exception {
        // Создаем объект для теста
        UpdatedUserDto user = new UpdatedUserDto("John Doe", 30, "john.doe@example.com", "param");

        // MessagePack сериализация
        byte[] msgPackData = msgPackMapper.writeValueAsBytes(user);
        System.out.println("MessagePack serialized size: " + msgPackData.length + " bytes");

        UserDto deserializedUser = msgPackMapper.readValue(msgPackData, UserDto.class);
        assertNotNull(deserializedUser);
        assertEquals(user.name(), deserializedUser.name());
        assertEquals(user.age(), deserializedUser.age());
        assertEquals(user.email(), deserializedUser.email());
    }

    @Test
    public void testCompatibilitySerializationSize() throws Exception {
        // Создаем объект для теста
        UserDto user = new UserDto("John Doe", 30, "john.doe@example.com");

        // MessagePack сериализация
        byte[] msgPackData = msgPackMapper.writeValueAsBytes(user);
        System.out.println("MessagePack serialized size: " + msgPackData.length + " bytes");

        UpdatedUserDto deserializedUser = msgPackMapper.readValue(msgPackData, UpdatedUserDto.class);
        assertNotNull(deserializedUser);
        assertEquals(user.name(), deserializedUser.name());
        assertEquals(user.age(), deserializedUser.age());
        assertEquals(user.email(), deserializedUser.email());
        assertNull(deserializedUser.newParam());
    }

    @Test
    public void testDeserialization() throws Exception {
        // Исходный объект
        UserDto originalUser = new UserDto("John Doe", 30, "john.doe@example.com");

        // MessagePack сериализация
        byte[] msgPackData = msgPackMapper.writeValueAsBytes(originalUser);

        // Десериализация
        UserDto deserializedUser = msgPackMapper.readValue(msgPackData, UserDto.class);

        // Проверяем корректность десериализации
        assertEquals(originalUser.name(), deserializedUser.name());
        assertEquals(originalUser.age(), deserializedUser.age());
        assertEquals(originalUser.email(), deserializedUser.email());
    }

    @Test
    public void testNestedObjectSerialization() throws Exception {
        Person person = new Person("John Doe", 30, new Address("New York", "5th Avenue"));

        byte[] jsonData = jsonMapper.writeValueAsBytes(person);
        byte[] msgPackData = msgPackMapper.writeValueAsBytes(person);

        System.out.println("JSON (nested object) size: " + jsonData.length + " bytes");
        System.out.println("MessagePack (nested object) size: " + msgPackData.length + " bytes");
        assertTrue(msgPackData.length < jsonData.length, "MessagePack должен быть компактнее JSON");

        Person deserializedPerson = msgPackMapper.readValue(msgPackData, Person.class);
        assertNotNull(deserializedPerson);
        assertEquals(person.name(), deserializedPerson.name());
        assertEquals(person.address().city(), deserializedPerson.address().city());
    }

    @Test
    public void testCollectionSerialization() throws Exception {
        List<String> hobbies = Arrays.asList("Programming", "Cycling", "Gaming");

        byte[] jsonData = jsonMapper.writeValueAsBytes(hobbies);
        byte[] msgPackData = msgPackMapper.writeValueAsBytes(hobbies);

        System.out.println("JSON (list) size: " + jsonData.length + " bytes");
        System.out.println("MessagePack (list) size: " + msgPackData.length + " bytes");
        assertTrue(msgPackData.length < jsonData.length, "MessagePack должен быть компактнее JSON");

        // Десериализация и проверка
        List<String> deserialized = msgPackMapper.readValue(msgPackData, List.class);
        assertEquals(hobbies, deserialized);
    }

    @Test
    public void testMapSerialization() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");

        byte[] jsonData = jsonMapper.writeValueAsBytes(map);
        byte[] msgPackData = msgPackMapper.writeValueAsBytes(map);

        System.out.println("JSON (map) size: " + jsonData.length + " bytes");
        System.out.println("MessagePack (map) size: " + msgPackData.length + " bytes");
        assertTrue(msgPackData.length < jsonData.length, "MessagePack должен быть компактнее JSON");

        Map<String, String> deserialized = msgPackMapper.readValue(msgPackData, Map.class);
        assertEquals(map, deserialized);
    }

    @Test
    public void testNullHandling() throws Exception {
        Person person = new Person(null, 30, null);

        byte[] jsonData = jsonMapper.writeValueAsBytes(person);
        byte[] msgPackData = msgPackMapper.writeValueAsBytes(person);

        System.out.println("JSON (null values) size: " + jsonData.length + " bytes");
        System.out.println("MessagePack (null values) size: " + msgPackData.length + " bytes");
        assertTrue(msgPackData.length < jsonData.length, "MessagePack должен быть компактнее JSON");

        Person deserializedPerson = msgPackMapper.readValue(msgPackData, Person.class);
        assertNull(deserializedPerson.name());
        assertNull(deserializedPerson.address());
    }

    @Test
    public void testPerformance() throws Exception {
        List<Person> people = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            people.add(new Person("Person " + i, i, new Address("City " + i, "Street " + i)));
        }

        // Измеряем время сериализации
        long startJson = System.nanoTime();
        byte[] jsonData = jsonMapper.writeValueAsBytes(people);
        long endJson = System.nanoTime();
        long jsonSerializationTime = endJson - startJson;

        long startMsgPack = System.nanoTime();
        byte[] msgPackData = msgPackMapper.writeValueAsBytes(people);
        long endMsgPack = System.nanoTime();
        long msgPackSerializationTime = endMsgPack - startMsgPack;

        System.out.println("JSON size: " + jsonData.length + " bytes");
        System.out.println("MessagePack (null values) size: " + msgPackData.length + " bytes");
        assertTrue(msgPackData.length < jsonData.length, "MessagePack должен быть компактнее JSON");
        System.out.println("JSON serialization time: " + (jsonSerializationTime) / 1_000_000 + " ms");
        System.out.println("MessagePack serialization time: " + (msgPackSerializationTime) / 1_000_000 + " ms");
        assertTrue(msgPackSerializationTime < jsonSerializationTime, "MessagePack должен быть быстрее JSON");

        // Измеряем время десериализации
        long startJsonDes = System.nanoTime();
        jsonMapper.readValue(jsonData, List.class);
        long endJsonDes = System.nanoTime();
        long jsonDeserializationTime = endJsonDes - startJsonDes;

        long startMsgPackDes = System.nanoTime();
        msgPackMapper.readValue(msgPackData, List.class);
        long endMsgPackDes = System.nanoTime();
        long msgPackDeserializationTime = endMsgPackDes - startMsgPackDes;

        System.out.println("JSON deserialization time: " + (jsonDeserializationTime) / 1_000_000 + " ms");
        System.out.println("MessagePack deserialization time: " + (msgPackDeserializationTime) / 1_000_000 + " ms");
        assertTrue(msgPackDeserializationTime < jsonDeserializationTime, "MessagePack должен быть быстрее JSON");
    }
}

package dk.kb.tvsubtitleocr.common.messagebroker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

/**
 * Created by jacob on 2018-03-24 (YYYY-MM-DD).
 */
public class SerializeBase64 {
    private final static Logger log = LoggerFactory.getLogger(SerializeBase64.class);

    /**
     * Serialize object to Base64 encoded String.
     * @param obj
     * @return
     */
    public static String serializeToString(Serializable obj) {
        // serialize the object
        byte[] bytes = serialize(obj);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static <T> byte[] serialize(T obj) {
        // serialize the object
        ObjectMapper mapper = new ObjectMapper();

        try {
            String json = mapper.writeValueAsString(obj);
            byte[] base64Encoded = java.util.Base64.getEncoder().encode(json.getBytes());
            return base64Encoded;
        } catch (JsonProcessingException e) {
            log.error("Error serializing content into object resultType {}", obj.getClass().getName(), e);
            return null;
        }

//        XmlMapper xmlMapper = new XmlMapper();
//
//        try {
//            String result = xmlMapper.writeValueAsString(obj);
//            return result.getBytes();
//        } catch (JsonProcessingException e) {
//            log.error("Error serializing content into object resultType {}", obj.getClass().getName(), e);
//            return null;
//        }


//        byte[] data = SerializationUtils.serialize(obj);
//        byte[] base64Encoded = java.util.Base64.getEncoder().encode(data);
//        return data;

//        try {
//            ByteArrayOutputStream bo = new ByteArrayOutputStream();
//            ObjectOutputStream so = new ObjectOutputStream(bo);
//            so.writeObject(obj);
//            so.flush();
//            byte[] base64Encoded = java.util.Base64.getEncoder().encode(bo.toByteArray());
//            return base64Encoded;
//        } catch (Exception e) {
//            log.error("Error serializing content into object resultType {}", obj.getClass().getName(), e);
//            return null;
//        }
    }

    /**
     * Deserialize base64 encoded String to an object.
     * @param content
     * @param resultType
     * @return The object type if possible, or null if not.
     */
    public static <T> T deserializeToString(String content, Class<T> resultType) {
        // deserialize the object
        byte[] bytes = content.getBytes();
        return deserialize(bytes, resultType);
    }

    public static <T> T deserialize(byte[] content, Class<T> resultType) {
        // deserialize the object
        ObjectMapper mapper = new ObjectMapper();
        byte[] base64Decoded = java.util.Base64.getDecoder().decode(content);
        String json = new String(base64Decoded);
        try {
            T object = mapper.readValue(json, resultType);
            return object;
        } catch (IOException e) {
            log.error("Error deseralizing content into object resultType {}", resultType.getName(), e);
            return null;
        }

//        mapper.writeValueAsString()

//        XmlMapper xmlMapper = new XmlMapper();
//        try {
//            String stringContent = new String(content);
//            T resultObject = xmlMapper.readValue(stringContent, resultType);
//            return resultObject;
//        } catch (IOException e) {
//            log.error("Error deseralizing content into object resultType {}", resultType.getName(), e);
//            return null;
//        }

//        try {
//        byte bytes[] = java.util.Base64.getDecoder().decode(content);
//            ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(content);
//            ObjectInputStream objectInputStream = new ObjectInputStream(arrayInputStream);
//            T deserializedObject = SerializationUtils.deserialize(content);
//            return deserializedObject;
//        } catch (/*SerializationException | IllegalArgumentException | */Exception e) {
//            log.error("Deserialization error", e);
//            return null;
//        }
//        return resultType.cast(objectInputStream.readObject());
//        } catch (ClassNotFoundException e) {
////             This would be really weird if it were to happen.
//            log.error("Error deseralizing content into object resultType {}. Class not found", resultType.getName(), e);
//            return null;
//        }
    }
}

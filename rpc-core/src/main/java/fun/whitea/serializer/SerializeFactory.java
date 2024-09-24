package fun.whitea.serializer;

import java.util.HashMap;
import java.util.Map;

public class SerializeFactory {

    private static final Map<String, Serializer> KEY_SERIALIZER_MAP = new HashMap<>();

    static {
        KEY_SERIALIZER_MAP.put(SerializerKeys.JDK, new JdkSerializer());
        KEY_SERIALIZER_MAP.put(SerializerKeys.KRYO, new KryoSerializer());
        KEY_SERIALIZER_MAP.put(SerializerKeys.JSON, new JsonSerializer());
        KEY_SERIALIZER_MAP.put(SerializerKeys.HESSIAN, new HessianSerializer());
    }

    private static final Serializer DEFAULT_SERIALIZER = KEY_SERIALIZER_MAP.get(SerializerKeys.JSON);

    public static Serializer getInstance(String key) {
        return KEY_SERIALIZER_MAP.getOrDefault(key, DEFAULT_SERIALIZER);
    }

}

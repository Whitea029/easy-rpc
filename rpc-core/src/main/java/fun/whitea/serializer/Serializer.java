package fun.whitea.serializer;

import java.io.IOException;

public interface Serializer {

    <T> byte[] serialize(T obj) throws IOException;

    <T> Object deserialize(byte[] data, Class<T> type) throws IOException;

}

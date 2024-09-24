package fun.whitea.easyrpc.serializer;


import com.fasterxml.jackson.databind.ObjectMapper;
import fun.whitea.easyrpc.model.RpcRequest;
import fun.whitea.easyrpc.model.RpcResponse;

import java.io.IOException;

public class JsonSerializer implements Serializer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> type) throws IOException {
        T t = OBJECT_MAPPER.readValue(data, type);
        if (t instanceof RpcResponse) {
            return toResponse((RpcResponse) t, type);
        } else if (t instanceof RpcRequest) {
            return toRequest((RpcRequest) t, type);
        }
        return t;
    }

    private <T> T toRequest(RpcRequest request, Class<T> type) throws IOException {
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] args = request.getArgs();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> clazz = parameterTypes[i];
            if (!clazz.isAssignableFrom(args[i].getClass())) {
                byte[] argBytes = OBJECT_MAPPER.writeValueAsBytes(args[i]);
                args[i] = OBJECT_MAPPER.readValue(argBytes, clazz);
            }
        }
        return type.cast(request);
    }

    private <T> T toResponse(RpcResponse response, Class<T> type) throws IOException {
        byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(response);
        response.setData(bytes);
        return type.cast(response);
    }
}

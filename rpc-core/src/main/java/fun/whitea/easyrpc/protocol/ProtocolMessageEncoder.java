package fun.whitea.easyrpc.protocol;

import fun.whitea.easyrpc.serializer.SerializeFactory;
import fun.whitea.easyrpc.serializer.Serializer;
import io.vertx.core.buffer.Buffer;
import lombok.SneakyThrows;

public class ProtocolMessageEncoder {

    @SneakyThrows
    public static Buffer encode(ProtocolMessage<?> msg) {
        ProtocolMessage.Header header = msg.getHeader();
        Buffer buffer = Buffer.buffer();
        if (msg == null || header == null) {
            return buffer;
        }
        buffer
                .appendByte(header.getMagic())
                .appendByte(header.getVersion())
                .appendByte(header.getType())
                .appendByte(header.getSerializer())
                .appendByte(header.getStatus())
                .appendLong(header.getRequestId());
        ProtocolMessageSerializerEnum protocolMessageSerializerEnum = ProtocolMessageSerializerEnum.fromKey(header.getSerializer());
        if (protocolMessageSerializerEnum == null) {
            throw new RuntimeException("Unknown protocol message serializer " + header.getSerializer());
        }
        Serializer serializer = SerializeFactory.getInstance(protocolMessageSerializerEnum.getVal());
        byte[] bytes = serializer.serialize(msg.getBody());
        buffer.appendInt(bytes.length).appendBytes(bytes);
        return buffer;
    }

}

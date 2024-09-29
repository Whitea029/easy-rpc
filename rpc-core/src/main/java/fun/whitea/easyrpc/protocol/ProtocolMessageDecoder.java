package fun.whitea.easyrpc.protocol;


import fun.whitea.easyrpc.constant.ProtocolConstant;
import fun.whitea.easyrpc.model.RpcRequest;
import fun.whitea.easyrpc.model.RpcResponse;
import fun.whitea.easyrpc.serializer.SerializeFactory;
import fun.whitea.easyrpc.serializer.Serializer;
import io.vertx.core.buffer.Buffer;
import lombok.SneakyThrows;

public class ProtocolMessageDecoder {

    @SneakyThrows
    public static ProtocolMessage<?> decode(Buffer buffer) {
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        byte magic = buffer.getByte(0);
        if (magic != ProtocolConstant.PROTOCOL_MAGIC) {
            throw new RuntimeException("magic not match");
        }
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        header.setBodyLength(buffer.getInt(13));
        byte[] bodyBytes = buffer.getBytes(17, 17 + header.getBodyLength());
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.fromKey(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("unknown protocol message serializer");
        }
        Serializer serializer = SerializeFactory.getInstance(serializerEnum.getVal());
        ProtocolMessageTypeEnum protocolMessageTypeEnum = ProtocolMessageTypeEnum.fromKey(header.getType());
        if (protocolMessageTypeEnum == null) {
            throw new RuntimeException("unknown protocol message type");
        }
        switch (protocolMessageTypeEnum) {
            case REQUEST:
                RpcRequest req = serializer.deserialize(bodyBytes, RpcRequest.class);
                return new ProtocolMessage<>(header, req);
            case RESPONSE:
                RpcResponse resp = serializer.deserialize(bodyBytes, RpcResponse.class);
                return new ProtocolMessage<>(header, resp);
            case HEART_BEAT:
            case OTHERS:
            default:
                throw new RuntimeException("unknown protocol message type");
        }
    }
}

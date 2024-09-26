package fun.whitea.easyrpc.protocol;


import fun.whitea.easyrpc.constant.ProtocolConstant;
import fun.whitea.easyrpc.model.RpcRequest;
import fun.whitea.easyrpc.model.RpcResponse;
import fun.whitea.easyrpc.serializer.SerializeFactory;
import fun.whitea.easyrpc.serializer.Serializer;
import lombok.SneakyThrows;

import io.vertx.core.buffer.Buffer;

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
        ProtocolMessageSerializerEnum protocolMessageSerializerEnum = ProtocolMessageSerializerEnum.fromKey(header.getSerializer());
        if (protocolMessageSerializerEnum == null) {
            throw new RuntimeException("unknown protocol message serializer");
        }
        Serializer serializer = SerializeFactory.getInstance(protocolMessageSerializerEnum.getVal());
        ProtocolMessageTypeEnum protocolMessageTypeEnum = ProtocolMessageTypeEnum.of(header.getType());
        if (protocolMessageTypeEnum == null) {
            throw new RuntimeException("unknown protocol message type");
        }
        switch (protocolMessageTypeEnum) {
            case REQUEST -> {
                RpcRequest deserialize = serializer.deserialize(bodyBytes, RpcRequest.class);
                return new ProtocolMessage<>(header, deserialize);
            }
            case RESPONSE -> {
                RpcResponse deserialize = serializer.deserialize(bodyBytes, RpcResponse.class);
                return new ProtocolMessage<>(header, deserialize);
            }
            default -> throw new RuntimeException("unknown protocol message type");
        }


    }

}

package fun.whitea.easyrpc.server.tcp;

import fun.whitea.easyrpc.model.RpcRequest;
import fun.whitea.easyrpc.model.RpcResponse;
import fun.whitea.easyrpc.protocol.ProtocolMessage;
import fun.whitea.easyrpc.protocol.ProtocolMessageDecoder;
import fun.whitea.easyrpc.protocol.ProtocolMessageEncoder;
import fun.whitea.easyrpc.protocol.ProtocolMessageTypeEnum;
import fun.whitea.easyrpc.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.lang.reflect.Method;

public class TcpServerHandler implements Handler<NetSocket> {

    @Override
    public void handle(NetSocket socket) {
        socket.handler(buffer -> {
            ProtocolMessage<RpcRequest> repMessage;
            try {
                repMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (Exception e) {
                throw new RuntimeException("protocol message decode error", e);
            }
            RpcRequest rpcReq = repMessage.getBody();
            RpcResponse rpcResp = new RpcResponse();
            try {
                Class<?> implClass = LocalRegistry.get(rpcReq.getServiceName());
                Method method = implClass.getMethod(rpcReq.getMethodName(), rpcReq.getParameterTypes());
                Object res = method.invoke(implClass.newInstance(), rpcReq.getArgs());
                rpcResp.setData(res);
                rpcResp.setType(method.getReturnType());
                rpcResp.setMessage("OK");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResp.setMessage(e.getMessage());
                rpcResp.setException(e);
            }
            ProtocolMessage.Header header = repMessage.getHeader();
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            ProtocolMessage<RpcResponse> respMessage = new ProtocolMessage<>(header, rpcResp);
            try {
                Buffer encode = ProtocolMessageEncoder.encode(respMessage);
                socket.write(encode);
            } catch (Exception e) {
                throw new RuntimeException("protocol message encode error", e);
            }
        });
    }

}

package fun.whitea.easyrpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import fun.whitea.easyrpc.RpcApplication;
import fun.whitea.easyrpc.constant.ProtocolConstant;
import fun.whitea.easyrpc.model.RpcRequest;
import fun.whitea.easyrpc.model.RpcResponse;
import fun.whitea.easyrpc.protocol.*;
import fun.whitea.easyrpc.registry.ServiceMetaInfo;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.SneakyThrows;

import java.util.concurrent.CompletableFuture;

public class VertxTcpClient {

    @SneakyThrows
    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) {
        NetClient netClient = Vertx.vertx().createNetClient();
        CompletableFuture<RpcResponse> future = new CompletableFuture<>();
        netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(),
                res -> {
                    if (!res.succeeded()) {
                        System.out.println("Fail to Connect to Tcp Server");
                        return;
                    }
                    NetSocket socket = res.result();
                    ProtocolMessage.Header header = new ProtocolMessage.Header(
                            ProtocolConstant.PROTOCOL_MAGIC,
                            ProtocolConstant.PROTOCOL_VERSION,
                            (byte) ProtocolMessageSerializerEnum.fromVal(RpcApplication.getConfig().getSerializer()).getKey(),
                            (byte) ProtocolMessageTypeEnum.REQUEST.getKey(),
                            (byte) ProtocolMessageStatusEnum.OK.getVal(),
                            IdUtil.getSnowflakeNextId(),
                            rpcRequest.toString().getBytes().length
                    );
                    ProtocolMessage<RpcRequest> rpcRequestProtocolMessage = new ProtocolMessage<>(header, rpcRequest);
                    try {
                        Buffer encode = ProtocolMessageEncoder.encode(rpcRequestProtocolMessage);
                        socket.write(encode);
                    } catch (Exception e) {
                        throw new RuntimeException("Protocol message encoding error", e);
                    }
                    TcpBufferHandlerWrapper tcpBufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
                        try {
                            ProtocolMessage<RpcResponse> responseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                            future.complete(responseProtocolMessage.getBody());
                        } catch (Exception e) {
                            throw new RuntimeException("Protocol message encoding error", e);
                        }
                    });
                    socket.handler(tcpBufferHandlerWrapper);
                });
        RpcResponse rpcResponse = future.get();
        netClient.close();
        return rpcResponse;
    }

}

package fun.whitea.easyrpc.porxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import fun.whitea.easyrpc.RpcApplication;
import fun.whitea.easyrpc.config.RpcConfig;
import fun.whitea.easyrpc.constant.ProtocolConstant;
import fun.whitea.easyrpc.constant.RpcConstant;
import fun.whitea.easyrpc.model.RpcRequest;
import fun.whitea.easyrpc.model.RpcResponse;
import fun.whitea.easyrpc.protocol.*;
import fun.whitea.easyrpc.registry.RegisterFactory;
import fun.whitea.easyrpc.registry.Registry;
import fun.whitea.easyrpc.registry.ServiceMetaInfo;
import fun.whitea.easyrpc.serializer.SerializeFactory;
import fun.whitea.easyrpc.serializer.Serializer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        RpcConfig rpcConfig = RpcApplication.getConfig();
        Serializer serializer = SerializeFactory.getInstance(rpcConfig.getSerializer());
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            Registry registry = RegisterFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfos)) {
                throw new RuntimeException("No service address");
            }
            ServiceMetaInfo selectServiceMetaInfo = serviceMetaInfos.getFirst();
            return sendTcpReq(selectServiceMetaInfo, rpcRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object sendHttpReq(ServiceMetaInfo selectServiceMetaInfo, RpcRequest rpcRequest, Serializer serializer) throws IOException {
        try (HttpResponse execute = HttpRequest.post(selectServiceMetaInfo.getServiceAddress())
                .body(serializer.serialize(rpcRequest))
                .execute()) {
            byte[] res = execute.bodyBytes();
            RpcResponse rpcResponse = serializer.deserialize(res, RpcResponse.class);
            return rpcResponse.getData();
        }
    }

    private Object sendTcpReq(ServiceMetaInfo selectServiceMetaInfo, RpcRequest rpcRequest) throws Exception {
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> future = new CompletableFuture<>();
        netClient.connect(selectServiceMetaInfo.getServicePort(), selectServiceMetaInfo.getServiceHost(), result -> {
            if (result.succeeded()) {
                System.out.println("Connect to Tcp Server");
                NetSocket socket = result.result();
                ProtocolMessage<RpcRequest> rpcRequestProtocolMessage = new ProtocolMessage<>();
                ProtocolMessage.Header header = new ProtocolMessage.Header();
                header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                header.setSerializer(Byte.parseByte(Objects.requireNonNull(ProtocolMessageSerializerEnum.fromVal(RpcApplication.getConfig().getSerializer())).getVal()));
                header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                header.setRequestId(IdUtil.getSnowflakeNextId());
                rpcRequestProtocolMessage.setHeader(header);
                rpcRequestProtocolMessage.setBody(rpcRequest);
                try {
                    Buffer encode = ProtocolMessageEncoder.encode(rpcRequestProtocolMessage);
                    socket.write(encode);
                } catch (Exception e) {
                    throw new RuntimeException("Protocol message encoding error", e);
                }
                socket.handler(buffer -> {
                    try {
                        ProtocolMessage<RpcResponse> responseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                        future.complete(responseProtocolMessage.getBody());
                    } catch (Exception e) {
                        throw new RuntimeException("Protocol message encoding error", e);
                    }
                });
            } else {
                System.out.println("Failed to connect to TCP server");
            }
        });
        RpcResponse rpcResponse = future.get();
        netClient.close();
        return rpcResponse.getData();
    }
}

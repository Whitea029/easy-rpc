package fun.whitea.easyrpc.porxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import fun.whitea.easyrpc.RpcApplication;
import fun.whitea.easyrpc.model.RpcRequest;
import fun.whitea.easyrpc.model.RpcResponse;
import fun.whitea.easyrpc.serializer.SerializeFactory;
import fun.whitea.easyrpc.serializer.Serializer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Serializer serializer = SerializeFactory.getInstance(RpcApplication.getConfig().getSerializer());
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            byte[] serialize = serializer.serialize(rpcRequest);
            try (HttpResponse execute = HttpRequest.post("http://127.0.0.1:9000")
                    .body(serialize)
                    .execute()) {
                byte[] res = execute.bodyBytes();
                RpcResponse rpcResponse = serializer.deserialize(res, RpcResponse.class);
                return rpcResponse.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

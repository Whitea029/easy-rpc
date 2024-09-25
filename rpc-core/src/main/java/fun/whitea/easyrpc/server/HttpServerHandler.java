package fun.whitea.easyrpc.server;

import fun.whitea.easyrpc.RpcApplication;
import fun.whitea.easyrpc.model.RpcRequest;
import fun.whitea.easyrpc.model.RpcResponse;
import fun.whitea.easyrpc.registry.LocalRegister;
import fun.whitea.easyrpc.serializer.SerializeFactory;
import fun.whitea.easyrpc.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;


public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest httpServerRequest) {
        Serializer serializer = SerializeFactory.getInstance(RpcApplication.getConfig().getSerializer());
        System.out.println("Receive request: " + httpServerRequest.method() + " " + httpServerRequest.uri());
        httpServerRequest.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcReq = null;
            try {
                rpcReq = serializer.deserialize(bytes, RpcRequest.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            RpcResponse rpcResp = new RpcResponse();
            if (rpcReq == null) {
                rpcResp.setMessage("rpcRequest is null");
                doResponse(httpServerRequest, rpcResp, serializer);
                return;
            }

            try {
                Class<?> implClass = LocalRegister.get(rpcReq.getServiceName());
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
            doResponse(httpServerRequest, rpcResp, serializer);
        });
    }

    void doResponse(HttpServerRequest request, RpcResponse response, Serializer serializer) {
        HttpServerResponse httpServerResponse = request.response().putHeader("content-type", "application/json; charset=utf-8");
        try {
            byte[] serialize = serializer.serialize(response);
            httpServerResponse.end(Buffer.buffer(serialize));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}

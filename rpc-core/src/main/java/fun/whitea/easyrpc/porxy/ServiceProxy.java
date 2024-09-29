package fun.whitea.easyrpc.porxy;

import cn.hutool.core.collection.CollUtil;
import fun.whitea.easyrpc.RpcApplication;
import fun.whitea.easyrpc.config.RpcConfig;
import fun.whitea.easyrpc.constant.RpcConstant;
import fun.whitea.easyrpc.model.RpcRequest;
import fun.whitea.easyrpc.registry.RegisterFactory;
import fun.whitea.easyrpc.registry.Registry;
import fun.whitea.easyrpc.registry.ServiceMetaInfo;
import fun.whitea.easyrpc.server.tcp.VertxTcpClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        RpcConfig rpcConfig = RpcApplication.getConfig();
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .serviceVersion(RpcConstant.DEFAULT_SERVICE_VERSION)
                .build();
        try {
            Registry registry = RegisterFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfos))
                throw new RuntimeException("No service address");
            ServiceMetaInfo selectServiceMetaInfo = serviceMetaInfos.getFirst();
            return VertxTcpClient.doRequest(rpcRequest, selectServiceMetaInfo).getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

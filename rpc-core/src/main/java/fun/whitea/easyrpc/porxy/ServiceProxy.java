package fun.whitea.easyrpc.porxy;

import cn.hutool.core.collection.CollUtil;
import fun.whitea.easyrpc.RpcApplication;
import fun.whitea.easyrpc.config.RpcConfig;
import fun.whitea.easyrpc.constant.RpcConstant;
import fun.whitea.easyrpc.fault.retry.RetryStrategy;
import fun.whitea.easyrpc.fault.retry.RetryStrategyFactory;
import fun.whitea.easyrpc.loadbalancer.LoadBalancer;
import fun.whitea.easyrpc.loadbalancer.LoadBalancerFactory;
import fun.whitea.easyrpc.model.RpcRequest;
import fun.whitea.easyrpc.model.RpcResponse;
import fun.whitea.easyrpc.registry.RegisterFactory;
import fun.whitea.easyrpc.registry.Registry;
import fun.whitea.easyrpc.registry.ServiceMetaInfo;
import fun.whitea.easyrpc.server.tcp.VertxTcpClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            LoadBalancer loadBalancer = LoadBalancerFactory.getLoadBalancer(rpcConfig.getLoadBalancer());
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("methodName", rpcRequest.getMethodName());
            ServiceMetaInfo select = loadBalancer.select(requestParams, serviceMetaInfos);
            RetryStrategy retryStrategy = RetryStrategyFactory.getsInstance(rpcConfig.getRetryStrategy());
            RpcResponse rpcResponse = retryStrategy.doRetry(() ->
                    VertxTcpClient.doRequest(rpcRequest, select)
            );
            return rpcResponse.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

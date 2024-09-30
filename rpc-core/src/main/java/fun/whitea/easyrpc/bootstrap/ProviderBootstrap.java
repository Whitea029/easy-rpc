package fun.whitea.easyrpc.bootstrap;

import fun.whitea.easyrpc.RpcApplication;
import fun.whitea.easyrpc.config.RegistryConfig;
import fun.whitea.easyrpc.config.RpcConfig;
import fun.whitea.easyrpc.constant.RpcConstant;
import fun.whitea.easyrpc.model.ServiceRegisterInfo;
import fun.whitea.easyrpc.registry.LocalRegistry;
import fun.whitea.easyrpc.registry.Registry;
import fun.whitea.easyrpc.registry.RegistryFactory;
import fun.whitea.easyrpc.registry.ServiceMetaInfo;
import fun.whitea.easyrpc.server.tcp.VertxTcpServer;

import java.util.List;

public class ProviderBootstrap {

    public static void init(List<ServiceRegisterInfo> serviceRegisterInfoList) {
        RpcApplication.init();
        final RpcConfig rpcConfig = RpcApplication.getConfig();

        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.getServiceName();
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());

            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo(
                    serviceName,
                    RpcConstant.DEFAULT_SERVICE_VERSION,
                    rpcConfig.getServerHost(),
                    rpcConfig.getServerPort()
            );
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + " fail to register service", e);
            }
        }

        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(rpcConfig.getServerPort());
    }

}

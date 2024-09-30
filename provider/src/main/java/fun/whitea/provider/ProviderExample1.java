package fun.whitea.provider;


import fun.whitea.common.service.UserService;
import fun.whitea.easyrpc.RpcApplication;
import fun.whitea.easyrpc.config.RegistryConfig;
import fun.whitea.easyrpc.config.RpcConfig;
import fun.whitea.easyrpc.constant.RpcConstant;
import fun.whitea.easyrpc.registry.LocalRegister;
import fun.whitea.easyrpc.registry.RegisterFactory;
import fun.whitea.easyrpc.registry.Registry;
import fun.whitea.easyrpc.registry.ServiceMetaInfo;
import fun.whitea.easyrpc.server.tcp.VertxTcpServer;

public class ProviderExample1 {
    public static void main(String[] args) {
        RpcApplication. init();

        LocalRegister.register(UserService.class.getName(), UserServiceImpl.class);

        RpcConfig config = RpcApplication.getConfig();
        RegistryConfig registryConfig = config.getRegistryConfig();
        Registry registry = RegisterFactory.getInstance(registryConfig.getRegistry());

        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(UserService.class.getName());
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        serviceMetaInfo.setServiceHost(config.getServerHost());
        serviceMetaInfo.setServicePort(config.getServerPort());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        VertxTcpServer vertxTcpServer = new VertxTcpServer();
//        VertxTcpTestServer vertxTcpServer = new VertxTcpTestServer();
        vertxTcpServer.doStart(10000);

    }
}

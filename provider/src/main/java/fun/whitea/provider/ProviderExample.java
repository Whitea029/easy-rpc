package fun.whitea.provider;


import fun.whitea.easyrpc.RpcApplication;
import fun.whitea.common.service.UserService;
import fun.whitea.easyrpc.register.LocalRegister;
import fun.whitea.easyrpc.server.HttpServerInterface;
import fun.whitea.easyrpc.server.VertxHttpServer;

public class ProviderExample {
    public static void main(String[] args) {
        RpcApplication.init();
        LocalRegister.register(UserService.class.getName(), UserServiceImpl.class);
        HttpServerInterface httpServer = new VertxHttpServer();
        httpServer.doStart(9000);
    }
}

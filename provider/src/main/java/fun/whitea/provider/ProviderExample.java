package fun.whitea.provider;


import fun.whitea.common.service.UserService;
import fun.whitea.register.LocalRegister;
import fun.whitea.server.HttpServerInterface;
import fun.whitea.server.VertxHttpServer;

public class ProviderExample {
    public static void main(String[] args) {
        LocalRegister.register(UserService.class.getName(), UserServiceImpl.class);
        HttpServerInterface httpServer = new VertxHttpServer();
        httpServer.doStart(9000);
    }
}

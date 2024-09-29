package fun.whitea.easyrpc.server.tcp;

import fun.whitea.easyrpc.server.HttpServerInterface;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;

public class VertxTcpServer implements HttpServerInterface {

    @Override
    public void doStart(int port) {
        NetServer server = Vertx.vertx().createNetServer();
        server.connectHandler(new TcpServerHandler());
        server.listen(port, res -> {
            if (res.succeeded()) {
                System.out.println("Server started on port " + port);
            } else {
                System.out.println("failed to start server ");
            }
        });
    }

}


package fun.whitea.easyrpc.server.tcp;

import fun.whitea.easyrpc.server.HttpServerInterface;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;

public class VertxTcpServer implements HttpServerInterface {

    private byte[] handleRequest(byte[] requestData) {
        return "Hello, client".getBytes();
    }

    @Override
    public void doStart(int port) {
        NetServer server = Vertx.vertx().createNetServer();
        server.connectHandler(socket -> {
            socket.handler(buffer -> {
                byte[] requestData = buffer.getBytes();
                byte[] responseData = handleRequest(requestData);
                socket.write(Buffer.buffer(responseData));
            });
        });
        server.listen(port, res -> {
            if (res.succeeded()) {
                System.out.println("Server started on port " + port);
            } else {
                System.out.println("failed to start server ");
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}

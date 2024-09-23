package fun.whitea.server;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;

public class VertxHttpServer implements HttpServerInterface {

    @Override
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();
        HttpServer httpServer = vertx.createHttpServer();
        httpServer.requestHandler(new HttpServerHandler());

        httpServer.listen(port, res -> {
            if (res.succeeded()) {
                System.out.println("Server started on port " + port);
            } else {
                System.out.println("failed to start server ");
            }
        });
    }

}

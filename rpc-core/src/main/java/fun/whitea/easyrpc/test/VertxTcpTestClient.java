package fun.whitea.easyrpc.test;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

public class VertxTcpTestClient {

    public void start() {
        NetClient client = Vertx.vertx().createNetClient();
        client.connect(9000, "localhost", res -> {
            if (res.succeeded()) {
                System.out.println("Connected to server");
                NetSocket socket = res.result();
//                for (int i = 0; i < 1000; i++) {
//                    socket.write("Hello Server!Hello Server!Hello Server!Hello Server!");
//                }

                for (int i = 0; i < 1000; i++) {
                    Buffer buffer = Buffer.buffer();
                    String s = "Hello Server!Hello Server!Hello Server!Hello Server!";
                    buffer.appendInt(0);
                    buffer.appendInt(s.getBytes().length);
                    buffer.appendBytes(s.getBytes());
                    socket.write(buffer);
                }

                socket.handler(buffer -> {
                    System.out.println("Received response from server "  + buffer.toString());
                });
            } else {
                System.out.println("Failed to connect to server");
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpTestClient().start();
    }

}

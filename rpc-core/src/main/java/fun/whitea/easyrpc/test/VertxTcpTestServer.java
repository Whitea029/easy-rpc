package fun.whitea.easyrpc.test;

import fun.whitea.easyrpc.server.HttpServerInterface;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;

public class VertxTcpTestServer implements HttpServerInterface {

    // tcp sticky packet and half packet test
    @Override
    public void doStart(int port) {
        NetServer server = Vertx.vertx().createNetServer();
//        server.connectHandler(new TcpServerHandler());
        server.connectHandler(socket -> {
//            socket.handler(buffer -> {
//                String testMessage = "Hello Server!Hello Server!Hello Server!Hello Server!";
//                int length = testMessage.getBytes().length;
//                if (buffer.length() < length) {
//                    System.out.println("half packet,  length = " + length);
//                } else if (buffer.length() > length) {
//                    System.out.println("sticky packet, length = " + length);
//                } else {
//                    System.out.println("Good packet");
//                }
//                System.out.println(buffer);
//            });

//            String testMessage = "Hello Server!Hello Server!Hello Server!Hello Server!";
//            int length = testMessage.getBytes().length;
//            RecordParser recordParser = RecordParser.newFixed(length);
//            recordParser.setOutput(buffer -> {
//                String s = new String(buffer.getBytes());
//                if (s.equals(testMessage)) {
//                    System.out.println("Good packet");
//                }
//            });
//            socket.handler(recordParser);

            RecordParser recordParser = RecordParser.newFixed(8);
            recordParser.setOutput(new Handler<Buffer>() {
                int size = -1;
                Buffer resBUffer = Buffer.buffer();
                @Override
                public void handle(Buffer buffer) {
                    if (size == -1) {
                        size = buffer.getInt(4);
                        recordParser.fixedSizeMode(size);
                        resBUffer.appendBuffer(buffer);
                    } else {
                        resBUffer.appendBuffer(buffer);
                        System.out.println(resBUffer.toString());
                        size = -1;
                        recordParser.fixedSizeMode(8);
                        resBUffer = Buffer.buffer();
                    }
                }
            });
            socket.handler(recordParser);
        });
        server.listen(port, res -> {
            if (res.succeeded()) {
                System.out.println("Server started on port " + port);
            } else {
                System.out.println("failed to start server ");
            }
        });
    }

}


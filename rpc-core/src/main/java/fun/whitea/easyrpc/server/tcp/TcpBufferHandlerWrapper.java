package fun.whitea.easyrpc.server.tcp;

import fun.whitea.easyrpc.constant.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.parsetools.RecordParser;
import io.vertx.core.buffer.Buffer;

public class TcpBufferHandlerWrapper implements Handler<Buffer> {

    private RecordParser recordParser;

    public TcpBufferHandlerWrapper(Handler<Buffer> handler) {
        this.recordParser = initRecordParser(handler);
    }

    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }

    private RecordParser initRecordParser(Handler<Buffer> bufferHandler) {
        RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);
        parser.setOutput(new Handler<Buffer>() {
            int size = -1;
            Buffer resBuffer = Buffer.buffer();
            @Override
            public void handle(Buffer buffer) {
                if (size == -1) {
                    size = buffer.getInt(13);
                    recordParser.fixedSizeMode(size);
                    resBuffer.appendBuffer(buffer);
                } else {
                    resBuffer.appendBuffer(buffer);
                    bufferHandler.handle(resBuffer);
                    size = -1;
                    recordParser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                    resBuffer = Buffer.buffer();
                }
            }
        });
        return parser;
    }

}

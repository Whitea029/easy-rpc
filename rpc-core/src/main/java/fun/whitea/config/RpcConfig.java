package fun.whitea.config;

import fun.whitea.serializer.SerializerKeys;
import lombok.Data;

@Data
public class RpcConfig {

    private String name = "easy-rpc";
    private String version = "1.0.0";
    private String serverHost = "127.0.0.1";
    private int serverPort = 8888;
    private boolean mock = false;
    private String serializer = SerializerKeys.JDK;
}

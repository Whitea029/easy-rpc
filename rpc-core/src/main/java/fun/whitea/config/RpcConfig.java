package fun.whitea.config;

import lombok.Data;

@Data
public class RpcConfig {

    private String name = "easy-rpc";
    private String version = "1.0.0";
    private String serverHost = "127.0.0.1";
    private int serverPort = 8888;

}

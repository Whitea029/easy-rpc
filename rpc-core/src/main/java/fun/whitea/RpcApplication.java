package fun.whitea;

import fun.whitea.config.RpcConfig;
import fun.whitea.constant.RpcConstant;
import fun.whitea.utils.ConfigUtils;


public class RpcApplication {

    private static volatile RpcConfig rpcConfig;

    public static void init(RpcConfig config) {
        rpcConfig = config;
        System.out.println("RpcApplication init, config: " + config.toString());
    }

    public static void init() {
        RpcConfig config;
        try {
            config = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            e.printStackTrace();
            config = new RpcConfig();
        }
        init(config);
    }

    public static RpcConfig getConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }


}

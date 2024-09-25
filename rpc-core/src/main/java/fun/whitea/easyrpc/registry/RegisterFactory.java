package fun.whitea.easyrpc.registry;

import fun.whitea.easyrpc.spi.SpiLoader;

public class RegisterFactory {

    static {
        SpiLoader.load(Registry.class);
    }

    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    public static Registry getInstance(String key) {
        return SpiLoader.getInstance(Registry.class, key);
    }


}

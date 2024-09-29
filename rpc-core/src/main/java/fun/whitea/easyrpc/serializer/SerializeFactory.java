package fun.whitea.easyrpc.serializer;

import fun.whitea.easyrpc.spi.SpiLoader;

public class SerializeFactory {

    static {
        SpiLoader.load(Serializer.class);
    }

    public static final Serializer DEFAULT_SERIALIZER = new HessianSerializer();

    public static Serializer getInstance(String key) {
        return SpiLoader.getInstance(Serializer.class, key);
    }

}

package fun.whitea.easyrpc.fault.tolerant;


import fun.whitea.easyrpc.spi.SpiLoader;

public class TolerantStrategyFactory {

    static {
        SpiLoader.load(TolerantStrategy.class);
    }

    private static final TolerantStrategy DEFAULT_RETRY_STRATEGY = new FailFastTolerantStrategy();

    public static TolerantStrategy getInstance(String key) {
        return SpiLoader.getInstance(TolerantStrategy.class, key);
    }

}

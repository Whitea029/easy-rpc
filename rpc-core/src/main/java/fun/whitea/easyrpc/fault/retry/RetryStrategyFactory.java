package fun.whitea.easyrpc.fault.retry;

import fun.whitea.easyrpc.spi.SpiLoader;

public class RetryStrategyFactory {

    static {
        SpiLoader.load(RetryStrategy.class);
    }

    public static RetryStrategy DEFAULT_STRATEGY = new FixedIntervalRetryStrategy();

    public static RetryStrategy getsInstance(String key) {
        return SpiLoader.getInstance(RetryStrategy.class, key);
    }

}

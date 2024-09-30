package fun.whitea.easyrpc.fault.retry;

import fun.whitea.easyrpc.spi.SpiLoader;

public class RetryStrategyFactory {

    static {
        SpiLoader.load(RetryStrategyFactory.class);
    }

    public static RetryStrategy DEFAULT_STRATEGY = new FixIntervalRetryStrategy();

    public static RetryStrategy getsInstance(String key) {
        return SpiLoader.getInstance(RetryStrategy.class, key);
    }

}

package fun.whitea.easyrpc.test;

import fun.whitea.easyrpc.fault.retry.FixIntervalRetryStrategy;
import fun.whitea.easyrpc.fault.retry.NoRetryStrategy;
import fun.whitea.easyrpc.fault.retry.RetryStrategy;
import fun.whitea.easyrpc.model.RpcResponse;
import org.junit.Test;

public class RetryStrategyTest {
    RetryStrategy retryStrategy = new FixIntervalRetryStrategy();

    @Test
    public void doRetry() {
        try {
            RpcResponse rpcResponse = retryStrategy.doRetry(() -> {
                System.out.println("测试重试");
                throw new RuntimeException("模拟重试失败");
            });
            System.out.println(rpcResponse);
        } catch (Exception e) {
            System.out.println("重试多次失败");
            e.printStackTrace();
        }
    }
}

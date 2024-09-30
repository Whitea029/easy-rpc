package fun.whitea.easyrpc.fault.retry;

import fun.whitea.easyrpc.model.RpcResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

@Slf4j
public class NoRetryStrategy implements RetryStrategy {

    /**
     * 重试
     *
     * @param callable
     * @return
     * @throws Exception
     */
    @SneakyThrows
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable){
        return callable.call();
    }

}
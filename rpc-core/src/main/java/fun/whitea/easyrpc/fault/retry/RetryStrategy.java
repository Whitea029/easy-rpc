package fun.whitea.easyrpc.fault.retry;

import fun.whitea.easyrpc.model.RpcResponse;

import java.util.concurrent.Callable;

public interface RetryStrategy {

    RpcResponse doRetry(Callable<RpcResponse> callable);

}

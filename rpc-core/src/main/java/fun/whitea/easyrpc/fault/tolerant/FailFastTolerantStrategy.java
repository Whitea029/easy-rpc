package fun.whitea.easyrpc.fault.tolerant;

import fun.whitea.easyrpc.model.RpcResponse;

import java.util.Map;

public class FailFastTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        throw new RuntimeException("service error", e);
    }
}

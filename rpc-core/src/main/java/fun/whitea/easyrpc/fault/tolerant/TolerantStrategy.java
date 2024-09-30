package fun.whitea.easyrpc.fault.tolerant;

import fun.whitea.easyrpc.model.RpcResponse;

import java.util.Map;

public interface TolerantStrategy {

    RpcResponse doTolerant(Map<String, Object> context, Exception e);

}

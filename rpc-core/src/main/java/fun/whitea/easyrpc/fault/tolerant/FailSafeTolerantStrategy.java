package fun.whitea.easyrpc.fault.tolerant;

import fun.whitea.easyrpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("handle exception silently", e);
        return new RpcResponse();
    }
}

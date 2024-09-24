package fun.whitea.easyrpc.model;

import fun.whitea.easyrpc.constant.RpcConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcRequest implements Serializable {

    private String serviceName;
    private String methodName;
    private String serviceVersion = RpcConstant.DEFAULT_SERVICE_VERSION;
    private Class<?>[] parameterTypes;
    private Object[] args;

}

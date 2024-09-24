package fun.whitea.easyrpc.register;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceMetaInfo {

    private String serviceName;

    private String serviceVersion;

    private String serviceHost;

    private Integer servicePort;

    private String serviceGroup = "default";

    public String getServiceKey() {
        // TODO 扩展服务分组
        // return String.format("%s:%s:%s", serviceName, serviceVersion, serviceGroup);
        return String.format("%s:%s", serviceName, serviceVersion);
    }

    public String getServiceNodeKey() {
        return String.format("%s/%s:%s", getServiceKey(), serviceHost, servicePort);
    }

}

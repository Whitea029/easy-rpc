package fun.whitea.easyrpc.registry;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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

    public String getServiceAddress() {
        if (!StrUtil.contains(serviceHost, "http")) {
            return String.format("http://%s:%s", serviceHost, servicePort);
        }
        return String.format("%s:%s", serviceHost, servicePort);
    }

    public ServiceMetaInfo(String serviceName, String serviceVersion, String serviceHost, Integer servicePort) {
        this.serviceName = serviceName;
        this.serviceVersion = serviceVersion;
        this.serviceHost = serviceHost;
        this.servicePort = servicePort;
    }


}

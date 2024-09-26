package fun.whitea.easyrpc.registry;

import fun.whitea.easyrpc.config.RegistryConfig;

import java.util.List;

public interface Registry {

    void init(RegistryConfig registryConfig);

    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    void unRegister(ServiceMetaInfo serviceMetaInfo);

    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    void heartbeat();

    void destroy();

    void watch(String serviceNodeKey);

}

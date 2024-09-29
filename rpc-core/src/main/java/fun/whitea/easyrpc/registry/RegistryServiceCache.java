package fun.whitea.easyrpc.registry;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegistryServiceCache {

    Map<String, List<ServiceMetaInfo>> serviceCache = new ConcurrentHashMap<>();

    void writeCache(String serviceKey, List<ServiceMetaInfo> serviceMetaInfos) {
        this.serviceCache.put(serviceKey, serviceMetaInfos);
    }

    List<ServiceMetaInfo> readCache(String serviceKey) {
        return this.serviceCache.get(serviceKey);
    }

    void clearCache(String serviceKey) {
        this.serviceCache.remove(serviceKey);
    }

}

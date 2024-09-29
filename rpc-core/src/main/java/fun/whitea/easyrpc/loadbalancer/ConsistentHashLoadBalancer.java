package fun.whitea.easyrpc.loadbalancer;

import cn.hutool.core.collection.CollUtil;
import fun.whitea.easyrpc.registry.ServiceMetaInfo;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConsistentHashLoadBalancer implements LoadBalancer {

    private final TreeMap<Integer, ServiceMetaInfo> virtualNodeMap = new TreeMap<>();

    private static final Integer VIRTUAL_NODE_NUM = 100;

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (CollUtil.isEmpty(serviceMetaInfoList)) return null;
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                int hash = getHash(serviceMetaInfo.getServiceAddress() + "#" + i);
                virtualNodeMap.put(hash, serviceMetaInfo);
            }
        }
        int hash = getHash(requestParams.toString());
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodeMap.ceilingEntry(hash);
        if (entry == null) entry = virtualNodeMap.firstEntry();
        return entry.getValue();
    }

    @SneakyThrows
    private int getHash(String key) {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(key.getBytes(StandardCharsets.UTF_8));
        return ((hashBytes[0] & 0xFF) << 24) | ((hashBytes[1] & 0xFF) << 16) |
                ((hashBytes[2] & 0xFF) << 8) | (hashBytes[3] & 0xFF);
    }
}

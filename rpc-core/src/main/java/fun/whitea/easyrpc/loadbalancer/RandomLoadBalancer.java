package fun.whitea.easyrpc.loadbalancer;

import cn.hutool.core.collection.CollUtil;
import fun.whitea.easyrpc.registry.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomLoadBalancer implements LoadBalancer {

    private final Random random = new Random();

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (CollUtil.isEmpty(serviceMetaInfoList)) return null;
        int size = serviceMetaInfoList.size();
        if (size == 1) return serviceMetaInfoList.getFirst();
        return serviceMetaInfoList.get(random.nextInt(size));
    }
}

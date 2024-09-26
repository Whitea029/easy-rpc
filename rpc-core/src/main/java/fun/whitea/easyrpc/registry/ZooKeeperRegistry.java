package fun.whitea.easyrpc.registry;

import cn.hutool.core.collection.CollUtil;
import fun.whitea.easyrpc.config.RegistryConfig;
import io.vertx.core.impl.ConcurrentHashSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class ZooKeeperRegistry implements Registry {

    private CuratorFramework client;

    private ServiceDiscovery<ServiceMetaInfo> serviceDiscovery;

    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    private final Set<String> watchingKetSet = new ConcurrentHashSet<>();

    private static final String ZK_ROOT_PATH = "/rpc/zk";

    @Override
    public void init(RegistryConfig registryConfig) {
        client = CuratorFrameworkFactory
                .builder()
                .connectString(registryConfig.getAddress())
                .retryPolicy(new ExponentialBackoffRetry(Math.toIntExact(registryConfig.getTimeout()), 3))
                .build();
        serviceDiscovery = ServiceDiscoveryBuilder
                .builder(ServiceMetaInfo.class)
                .basePath(ZK_ROOT_PATH)
                .client(client)
                .serializer(new JsonInstanceSerializer<>(ServiceMetaInfo.class))
                .build();
        try {
            client.start();
            serviceDiscovery.start();
        } catch (Exception e) {
            throw new RuntimeException("fail to init zookeeper registry", e);
        }
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        serviceDiscovery.registerService(buildServiceInstance(serviceMetaInfo));
        localRegisterNodeKeySet.add(ZK_ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey());
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        try {
            serviceDiscovery.unregisterService(buildServiceInstance(serviceMetaInfo));
        } catch (Exception e) {
            throw new RuntimeException("fail to unregister service", e);
        }
        localRegisterNodeKeySet.remove(ZK_ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey());
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        List<ServiceMetaInfo> serviceMetaInfos = registryServiceCache.readCache(serviceKey);
        if (!CollUtil.isEmpty(serviceMetaInfos)) {
            return serviceMetaInfos;
        }
        try {
            Collection<ServiceInstance<ServiceMetaInfo>> serviceMetaInfoServiceInstance = serviceDiscovery.queryForInstances(serviceKey);
            List<ServiceMetaInfo> serviceMetaInfoList = serviceMetaInfoServiceInstance.stream().map(ServiceInstance::getPayload).toList();
            registryServiceCache.writeCache(serviceKey, serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("fail to load service", e);
        }
    }

    @Override
    public void heartbeat() {
        // 无需心跳机制，建立了临时节点
    }

    @Override
    public void destroy() {
        log.info("The current node is offline");
        for (String key : localRegisterNodeKeySet) {
            try {
                client.delete().guaranteed().forPath(key);
            } catch (Exception e) {
                throw new RuntimeException(key + "fail to offline", e);
            }
        }
    }

    @Override
    public void watch(String serviceNodeKey) {
        String watchKey = ZK_ROOT_PATH + "/" + serviceNodeKey;
        boolean add = watchingKetSet.add(watchKey);
        if (add) {
            CuratorCache cache = CuratorCache.build(client, watchKey);
            cache.start();
            cache.listenable().addListener(
                    CuratorCacheListener
                            .builder()
                            .forDeletes(childData -> registryServiceCache.clearCache())
                            .forChanges((old, now) -> registryServiceCache.clearCache())
                            .build()
            );
        }
    }

    private ServiceInstance<ServiceMetaInfo> buildServiceInstance(ServiceMetaInfo serviceMetaInfo) {
        String serviceAddress = serviceMetaInfo.getServiceHost() + ":" + serviceMetaInfo.getServicePort();
        try {
            return ServiceInstance
                    .<ServiceMetaInfo>builder()
                    .id(serviceAddress)
                    .name(serviceMetaInfo.getServiceName())
                    .address(serviceAddress)
                    .payload(serviceMetaInfo)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("fail to build service instance", e);
        }
    }

}

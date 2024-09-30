package fun.whitea.easyrpcspringbootstarter.bootstrap;

import fun.whitea.easyrpc.RpcApplication;
import fun.whitea.easyrpc.config.RegistryConfig;
import fun.whitea.easyrpc.config.RpcConfig;
import fun.whitea.easyrpc.registry.LocalRegistry;
import fun.whitea.easyrpc.registry.Registry;
import fun.whitea.easyrpc.registry.RegistryFactory;
import fun.whitea.easyrpc.registry.ServiceMetaInfo;
import fun.whitea.easyrpcspringbootstarter.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

@Slf4j
public class RpcProviderBootstrap implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> aClass = bean.getClass();
        RpcService annotation = aClass.getAnnotation(RpcService.class);
        if (annotation != null) {
            Class<?> interfaceClass = annotation.interfaceClass();
            if (interfaceClass == void.class) {
                interfaceClass = aClass.getInterfaces()[0];
            }
            String serviceName = interfaceClass.getName();
            String serviceVersion = annotation.serviceVersion();
            LocalRegistry.register(serviceName, aClass);
            final RpcConfig config = RpcApplication.getConfig();
            Registry registry = RegistryFactory.getInstance(config.getRetryStrategy());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo(
                    serviceName,
                    serviceVersion,
                    config.getServerHost(),
                    config.getServerPort()
            );
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + " register failed", e);
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}

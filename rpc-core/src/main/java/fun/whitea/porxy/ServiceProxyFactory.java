package fun.whitea.porxy;

import fun.whitea.RpcApplication;

import java.lang.reflect.Proxy;

public class ServiceProxyFactory {

    public static <T> T getProxy(Class<T> serviceClass) {
        if (RpcApplication.getConfig().isMock()) {
            return getMockProxy(serviceClass);
        }
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy()
        );
    }

    public static <T> T getMockProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new MockServiceProxy()
        );
    }

}

package fun.whitea.easyrpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import fun.whitea.easyrpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SpiLoader {

    private static final Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();

    private static final Map<String, Object> instanceCache = new ConcurrentHashMap<>();

    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system";

    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom";

    private static final String[] SCAN_DIRS = {RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};

    private static final List<Class<?>> LOAD_CLASS_LIST = Collections.singletonList(Serializer.class);

    public static void loadAll() {
        log.info("load all SPI");
        for (Class<?> aClass : LOAD_CLASS_LIST) {
            load(aClass);
        }

    }

    public static <T> T getInstance(Class<T> clazz, String key) {
        String clazzName = clazz.getName();
        Map<String, Class<?>> keyClassMap = loaderMap.get(clazzName);
        if (keyClassMap == null) {
            throw new RuntimeException("can not find class " + clazzName);
        }
        if (!keyClassMap.containsKey(key)) {
            throw new RuntimeException("can not find key " + key);
        }
        Class<?> aClass = keyClassMap.get(key);
        String name = aClass.getName();
        if (!instanceCache.containsKey(name)) {
            try {
                instanceCache.put(name, aClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("fail to newInstance " + name, e);
            }
        }
        return (T) instanceCache.get(name);
    }

    public static Map<String, Class<?>> load(Class<?> loadClass) {
        log.info("load SPI {}", loadClass.getName());
        Map<String, Class<?>> keyClassMap = new HashMap<>();
        for (String scanDir : SCAN_DIRS) {
            List<URL> resources = ResourceUtil.getResources(scanDir + "\\" + loadClass.getName());
            for (URL resource : resources) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] split = line.split("=");
                        if (split.length > 1) {
                            String key = split[0];
                            String className = split[1];
                            keyClassMap.put(key, Class.forName(className));
                        }
                    }
                } catch (Exception e) {
                    log.error("spi load error", e);
                }
            }
        }
        loaderMap.put(loadClass.getName(), keyClassMap);
        return keyClassMap;
    }


}

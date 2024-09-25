package fun.whitea.easyrpc.utils;

import cn.hutool.core.util.StrUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class ConfigUtils {

    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        return loadConfig(tClass, prefix, "");
    }

    public static <T> T loadConfig(Class<T> tClass, String prefix, String environment) {
        StringBuilder stringBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            stringBuilder.append("-").append(environment);
        }
        stringBuilder.append(".yml");

        try (InputStream input = ConfigUtils.class.getClassLoader().getResourceAsStream(stringBuilder.toString())) {
            if (input == null) {
                throw new RuntimeException("application.yml not found");
            }
            Yaml yaml = new Yaml();
            Map<String, Object> yamlMap = yaml.load(input);

            Map<String, Object> configMap = (Map<String, Object>) yamlMap.get(prefix);

            if (configMap == null) {
                throw new RuntimeException("prefix: " + prefix + " config not found");
            }
            Yaml subYaml = new Yaml();
            return subYaml.loadAs(new Yaml().dump(configMap), tClass);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration file", e);
        }
    }

}

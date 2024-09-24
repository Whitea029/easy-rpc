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
                throw new RuntimeException("配置文件未找到");
            }
            // 使用 Yaml 加载整个配置文件
            Yaml yaml = new Yaml();
            Map<String, Object> yamlMap = yaml.load(input);

            // 根据前缀获取子配置
            Map<String, Object> configMap = (Map<String, Object>) yamlMap.get(prefix);

            if (configMap == null) {
                throw new RuntimeException("前缀 " + prefix + " 对应的配置未找到");
            }

            // 将子配置映射为目标对象
            Yaml subYaml = new Yaml();
            return subYaml.loadAs(new Yaml().dump(configMap), tClass);
        } catch (Exception e) {
            throw new RuntimeException("加载配置文件失败", e);
        }
    }

}

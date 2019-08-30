package cn.bucheng.rm.aware;

import org.apache.logging.log4j.util.Strings;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * @author ：yinchong
 * @create ：2019/8/30 8:44
 * @description：
 * @modified By：
 * @version:
 */
public class SpringEnvironmentAware implements EnvironmentAware {
    private static Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        SpringEnvironmentAware.environment = environment;
    }

    public static String getValue(String key) {
        return environment.getProperty(key);
    }

    public static Integer getIntValue(String key, int defaultValue) {
        String value = getValue(key);
        if (Strings.isBlank(value))
            return defaultValue;
        return Integer.parseInt(value);
    }
}

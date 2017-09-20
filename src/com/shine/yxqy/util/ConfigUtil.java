package com.shine.yxqy.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * 配置文件工具类 Created by xiew on 2017-08-25.
 */
public class ConfigUtil {
    private static String config_path; // 静态配置文件
    private static String param_path; // 动态配置文件
    private static Properties properties;
    private static Properties paramProperties;

    static {
        config_path = FileUtil.getWebRealPath() + Constant.CONFIG_FILENAME;
        properties = FileUtil.readProperties(config_path);

        param_path = FileUtil.getWebRealPath() + Constant.CONFIG_PARAM_FILENAME;
        paramProperties = FileUtil.readProperties(param_path);
    }

    public static String getProperty(String key) {
        return String.valueOf(properties.getProperty(key));
    }

    public static String getParamProperty(String key) {
        return String.valueOf(paramProperties.getProperty(key));
    }

    /**
     * 将更改后的paramProperties属性写入properties文件中<br>
     * 此方法会丢失注释，还没找到更好的办法
     */
    public static void write2PropFile() {
        OutputStream out = null;

        try {
            out = new FileOutputStream(param_path);
            paramProperties.store(out, "汉字\n测试");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 更新paramProperties请求的参数
     * @param key key值
     * @param value value值
     */
    public static void putParamProperties(String key, String value) {
        paramProperties.setProperty(key, value);
    }

}

package com.shine.yxqy.util;

import java.util.Properties;

/**
 * 配置文件工具类
 * Created by xiew on 2017-08-25.
 */
public class ConfigUtil {
    private static String config_path;
    private static Properties properties;

    static{
        config_path = FileUtil.getWebRealPath()+Constant.CONFIG_FILENAME;
        properties = FileUtil.readProperties(config_path);
    }

    public static String getProperty(String key) {
        return String.valueOf(properties.getProperty(key));
    }



}

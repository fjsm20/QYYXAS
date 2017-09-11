package com.shine.yxqy.util;

import java.util.Properties;

/**
 * 配置文件工具类
 * Created by xiew on 2017-08-25.
 */
public class ConfigUtil {
    private static String config_path;       // 静态配置文件
    private static String param_path;        //动态配置文件
    private static Properties properties;
    private static Properties paramProperties;

    static{
        config_path = FileUtil.getWebRealPath()+Constant.CONFIG_FILENAME;
        properties = FileUtil.readProperties(config_path);

        param_path = FileUtil.getWebRealPath() + Constant.CONFIG_PARAM_FILENAME;
		paramProperties = FileUtil.readProperties(param_path);
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
	}

    public static String getParamProperty(String key) {
        return paramProperties.getProperty(key);
    }





}

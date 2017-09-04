package com.shine.yxqy.util;

import java.util.Properties;

/**
 * 配置文件工具类
 * Created by xiew on 2017-08-25.
 */
public class ConfigUtil {
    private static String config_path;
    private static Properties properties;
    private static Properties paramProperties;

    static{
        config_path = FileUtil.getWebRealPath()+Constant.CONFIG_FILENAME;
        properties = FileUtil.readProperties(config_path);
        
        config_path = FileUtil.getWebRealPath() + Constant.CONFIG_PARAM_FILENAME;
		paramProperties = FileUtil.readProperties(config_path);
    }

    public static String getProperty(String key) {
		Object obj = properties.getProperty(key);
		if (obj == null) {
			obj = paramProperties.get(key);
		}
		return String.valueOf(obj);
	}



}

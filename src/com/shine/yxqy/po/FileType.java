package com.shine.yxqy.po;

import com.shine.yxqy.util.ConfigUtil;
import com.shine.yxqy.util.Constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件类型
 * Created by xiew on 2017-09-07.
 */
public class FileType {
    private final static String AFTER_ID = "身份证反面照";
    private final static String BEFORE_ID = "身份证正面照";
    private final static String POLICE_ID = "公安部身份证";
    private final static String GROUP_ID = "申请人与业务人员合影";
    private final static String AVATAR_ID = "个人头像";
    private final static String SIGN_FILE_ID = "客户手持开户已签字文件照";
    private final static String VIDEO_ID="视频见证";
    public static final String KHXY_ID="开户协议";
    public static final String BLXY_ID="办理协议";

    private static Map<String, String> map = new HashMap<String, String>();


    public static String getFileType(String typeName) {
        if (map == null || map.size() == 0) {
            map.put(AFTER_ID, ConfigUtil.getProperty(Constant.AFTER_ID));
            map.put(BEFORE_ID, ConfigUtil.getProperty(Constant.BEFORE_ID));
            map.put(POLICE_ID, ConfigUtil.getProperty(Constant.POLICE_ID));
            map.put(GROUP_ID, ConfigUtil.getProperty(Constant.GROUP_ID));
            map.put(AVATAR_ID, ConfigUtil.getProperty(Constant.AVATAR_ID));
            map.put(SIGN_FILE_ID, ConfigUtil.getProperty(Constant.SIGN_FILE_ID));
            map.put(VIDEO_ID, ConfigUtil.getProperty(Constant.VIDEO_ID));
            map.put(KHXY_ID, ConfigUtil.getProperty(Constant.KHXY_ID));
            map.put(BLXY_ID, ConfigUtil.getProperty(Constant.BLXY_ID));
        }

        return String.valueOf(map.get(typeName));
    }

}

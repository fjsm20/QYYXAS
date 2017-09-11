package com.shine.yxqy.po;

import com.shine.yxqy.util.ConfigUtil;
import com.shine.yxqy.util.Constant;

import java.util.HashMap;
import java.util.Map;

/**
 * �ļ�����
 * Created by xiew on 2017-09-07.
 */
public class FileType {
    private final static String AFTER_ID = "���֤������";
    private final static String BEFORE_ID = "���֤������";
    private final static String POLICE_ID = "���������֤";
    private final static String GROUP_ID = "��������ҵ����Ա��Ӱ";
    private final static String AVATAR_ID = "����ͷ��";
    private final static String SIGN_FILE_ID = "�ͻ��ֳֿ�����ǩ���ļ���";
    private final static String VIDEO_ID="��Ƶ��֤";
    public static final String KHXY_ID="����Э��";
    public static final String BLXY_ID="����Э��";

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

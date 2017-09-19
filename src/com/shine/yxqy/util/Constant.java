package com.shine.yxqy.util;

import net.sf.json.JSONObject;

import java.util.Map;

/**
 * 常量
 * Created by xiew on 2017-08-25.
 */
public class Constant {

    public static final String APP_ID="app_id";
    public static final String URL_IP="url_ip";
    public static final String WT_IP="wt_ip";
    public static final String LOCAL_TMP_PATH="local_tmp_path";

    public static final String KH_BUSI_CODE="kh_busi_code";
    public static final String YWBL_BUSI_CODE="ywbl_busi_code";
    public static final String CONFIG_FILENAME="qyyxas_conf.properties";
    public static final String FTP_URL="ftp_url";
    public static final String CONFIG_PARAM_FILENAME="req_param.properties";

    public static final String TOKEN_SEAL = "/fileservice/fs/client/token/bus?app_id=";
    public static final String SIGNAL_UPLOAD_FILE="/fileservice/fs/client/io/upload/signal?check_code=";
    public static final String SUBMIT_PROCSCANINFO="/ECIMC/jsp/service/encrypt.do?check_code=";
    public static final String REQ_GET_FILE_URL = "/dataButtAction.do?method=exportExpressAccountByDay";


    public static final String OK_TXT="ok_txt";
    public static final String CUST_XML="cust.xml";

    public static final String OPERATE_TIME="operate_time";
    public static final String DATE_TYPE="date_type";
    public static final String DAY_OF_MONTH="day_of_month";


    public static final String AFTER_ID="AFTER_ID";
    public static final String BEFORE_ID="BEFORE_ID";
    public static final String POLICE_ID="POLICE_ID";
    public static final String GROUP_ID="GROUP_ID";
    public static final String AVATAR_ID="AVATAR_ID";
    public static final String SIGN_FILE_ID="SIGN_FILE_ID";
    public static final String VIDEO_ID="VIDEO_ID";
    public static final String KHXY_ID="KHXY_ID";
    public static final String BLXY_ID="BLXY_ID";

    //URL接口，请求参数source，其值分别为wzh:无纸化，dazx:档案中心
    public static final String SOURCE_TYPE = "source";




}

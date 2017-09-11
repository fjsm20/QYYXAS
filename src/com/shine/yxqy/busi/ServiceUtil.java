package com.shine.yxqy.busi;

import com.shine.yxqy.util.ConfigUtil;
import com.shine.yxqy.util.Constant;
import com.shine.yxqy.util.HttpUtil;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务器请求集合
 *
 * @author xiew 20170825
 */
public class ServiceUtil {
    private static Logger log = Logger.getLogger(ServiceUtil.class);

    //busToken(业务系统令牌)创建日期
    private static Date createDate = null;
    //文件服务器URL
    private static String fsServerURL = null;
    //业务系统令牌
    private static String busToken = null;

    private ServiceUtil() {
        if (fsServerURL == null || StringUtils.isEmpty(fsServerURL)) {
            fsServerURL = ConfigUtil.getProperty(Constant.URL_IP);
        }
    }


    /**
     * 初始化TokenManager,只需初始化一次
     *
     * @param serverURL 文件服务器URL
     * @return 是否初始化成功
     */
    public static boolean init(String serverURL) throws Exception {
        fsServerURL = serverURL;
        if (busToken == null) {
            log.debug("缓存的业务令牌为空,从文件服务器获取业务系统令牌");
            return iniLocalBusToken();
        } else {
            if (isExpired()) {
                log.debug("缓存的业务令牌失效,令牌值[" + busToken + "],创建日期[" + createDate.toString() + "],从文件服务器获取业务系统令牌");
                return iniLocalBusToken();
            } else {
                log.debug("缓存的业务令牌有效,令牌值[" + busToken + "],创建日期[" + createDate.toString() + "]");
                return true;
            }
        }
    }

    /**
     * 判断业务系统令牌是否失效
     *
     * @return 是否失效
     */
    private static boolean isExpired() {
        if (createDate == null)
            return true;
        Calendar expiredCalendar = Calendar.getInstance();
        expiredCalendar.setTime(createDate);
        expiredCalendar.set(Calendar.HOUR_OF_DAY, 23);
        expiredCalendar.set(Calendar.MINUTE, 59);
        expiredCalendar.set(Calendar.SECOND, 59);
        Calendar nowCalendar = Calendar.getInstance();
        //如果当前时间 在当天的23:59:59 后则判定为失效
        return nowCalendar.after(expiredCalendar);
    }

    /**
     * 初始化本地业务系统令牌(从文件服务器申请业务系统令牌),并更新创建时间
     *
     * @return 是否初始化成功
     */
    private static boolean iniLocalBusToken() throws Exception {
        String tToken = getRemoteBusToken();
        if (tToken != null && tToken.length() > 0) {
            busToken = tToken;
            createDate = new Date();
            return true;
        } else {
            createDate = null;
            return false;
        }
    }

    /**
     * 从文件服务器申请业务系统令牌
     *
     * @return 业务系统令牌
     */
    public static String getRemoteBusToken() throws Exception {

        if (fsServerURL == null || StringUtils.isEmpty(fsServerURL)) {
            fsServerURL = ConfigUtil.getProperty(Constant.URL_IP);
        }
        String appId = ConfigUtil.getProperty(Constant.APP_ID);
        if (fsServerURL.length() <= 1 || appId.length() <= 1) {
            log.info("基础配置异常：url_ip=" + fsServerURL + " ,app_id=" + appId);
        }

        String data = HttpUtil.remoteRequest(fsServerURL + Constant.TOKEN_SEAL + appId, "");
        Map<String, String> map = null;
        try {
            map = (Map<String, String>) JSONObject.fromObject(data);
        } catch (Exception e) {
            log.error("不是有有效的JSON格式");
            throw e;
        }
        String token = map.get("check_code");
        String retcode = map.get("retcode");
        if (retcode.equals("-1")) {
            log.error("获取影像文件服务器令牌出错,无权限访问");
            throw new Exception("获取影像文件服务器令牌出错,无权限访问");
        }
        if (retcode.equals("1301")) {
            log.error("令牌无效");
            throw new Exception("令牌无效");
        }
        if (StringUtils.isEmpty(token)) {
            log.error("获取影像文件服务器令牌的为空");
        }
        if (retcode.equals("0") && !StringUtils.isEmpty(token)) {
            return token;
        }
        return null;
    }

    /**
     * 申请业务系统令牌
     *
     * @return 业务系统令牌
     */
    public synchronized static String getBusToken() throws Exception {
        if (!isExpired() && busToken != null && busToken.length() > 0) {
            return busToken;
        } else {
            if (iniLocalBusToken()) {
                return busToken;
            } else {
                return null;
            }
        }
    }


    /**
     * 单上传文件
     *
     * @param fileName  文件名称 例如a.txt
     * @param certCode  证件号码
     * @param localFile 待上传文件
     * @return string 相应结果报文
     * @throws Exception
     */
    public static String doUploadFile(String fileName, String certCode, File localFile) throws Exception {
        String token = getBusToken();
        String urlStr = fsServerURL + Constant.SIGNAL_UPLOAD_FILE + token;
        Map<String, String> param = new HashMap<String, String>();
        param.put("file_name", fileName);
        param.put("cert_code", certCode);

        String data = HttpUtil.remoteRequest(urlStr, param, new FileInputStream(localFile));
        log.info("上传文件结果：" + data);
        //{"retcode":"0","retdesc":"操作成功","sign_type":"1","file_id":"2967","total_md5":"a4f8ab95fe1d93a09be7b45403e43aa9","retvalue":"2967"}
        return data;
    }


    /**
     * 事中影像信息提交
     * @param paramMap
     * @return
     * @throws Exception
     */
    public static String submitProcScanInfo(Map<String,String> paramMap) throws Exception{
        String token = getBusToken();
        String urlStr = fsServerURL+Constant.SUBMIT_PROCSCANINFO+token;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("method_id","020123");
        jsonObject.put("src_order_no",paramMap.get("src_order_no"));
        jsonObject.put("busi_code",paramMap.get("busi_code"));
        jsonObject.put("cust_prop",paramMap.get("cust_prop"));
        jsonObject.put("acct_code",paramMap.get("acct_code"));
        jsonObject.put("cert_type",paramMap.get("cert_type"));
        jsonObject.put("cert_code",paramMap.get("cert_code"));
        jsonObject.put("cust_name",paramMap.get("cust_name"));
        jsonObject.put("opr_date",paramMap.get("opr_date"));
        jsonObject.put("dep_code",paramMap.get("dep_code"));
        jsonObject.put("file_num","1");
        jsonObject.put("file_check","1");
        jsonObject.put("source_nos",paramMap.get("source_nos"));
        jsonObject.put("user_code",paramMap.get("user_code"));
        jsonObject.put("app_id",paramMap.get("app_id"));
        jsonObject.put("action_type","data_inte");

        System.out.println(jsonObject.toString());

        String rsData = HttpUtil.remoteRequest(urlStr,jsonObject.toString());

        log.info("事中影像信息提交：" + rsData);
        //{"retcode":"0","retdesc":"提交事中影像信息成功！","ret_code":"0","ret_msg":"提交事中影像信息成功！","retvalue":"提交事中影像信息成功！","check_code":""}
        return rsData;
    }

    /**
     * 影像归档接口
     * @param paramMap
     * @return
     * @throws Exception
     */
    public static String archive020524(Map<String,String> paramMap) throws Exception{
        String token = getBusToken();
        String urlStr = fsServerURL+Constant.SUBMIT_PROCSCANINFO+token;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("method_id","020524");
        jsonObject.put("src_order_no",paramMap.get("src_order_no"));
        jsonObject.put("busi_code",paramMap.get("busi_code"));
        jsonObject.put("cust_prop",paramMap.get("cust_prop"));
        jsonObject.put("acct_code",paramMap.get("acct_code"));
        jsonObject.put("cert_type",paramMap.get("cert_type"));
        jsonObject.put("cert_code",paramMap.get("cert_code"));
        jsonObject.put("cust_name",paramMap.get("cust_name"));
        jsonObject.put("dep_code",paramMap.get("dep_code"));
        jsonObject.put("khfs",paramMap.get("2"));
        jsonObject.put("op_user_code",paramMap.get("user_code"));
        jsonObject.put("app_id",paramMap.get("app_id"));
        jsonObject.put("opr_date",paramMap.get("opr_date"));
        jsonObject.put("action_type","data_inte");


        System.out.println(jsonObject.toString());

        String rsData = HttpUtil.remoteRequest(urlStr,jsonObject.toString());

        log.info("事中影像信息提交：" + rsData);
        //{"retcode":"0","retdesc":"提交事中影像信息成功！","ret_code":"0","ret_msg":"提交事中影像信息成功！","retvalue":"提交事中影像信息成功！","check_code":""}
        return rsData;
    }

}

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
     * @param param
     * @return
     * @throws Exception
     */
    public static String submitProcScanInfo(String param) throws Exception{
        String token = getBusToken();
        String urlStr = fsServerURL+Constant.SUBMIT_PROCSCANINFO+token;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("method_id","020123");
        jsonObject.put("src_order_no","XY0012229");
        jsonObject.put("busi_code","2100500000");
        jsonObject.put("cust_prop","0");
        jsonObject.put("acct_code","10000000911");
        jsonObject.put("cert_type","0");
        jsonObject.put("cert_code","1122331");
        jsonObject.put("cust_name","张三");
        jsonObject.put("opr_date","20170826");
        jsonObject.put("dep_code","3106");
        jsonObject.put("file_num","1");
        jsonObject.put("file_check","1");
        jsonObject.put("source_nos","70,2945|429,2947");
        jsonObject.put("user_code",Constant.APP_ID);
        jsonObject.put("app_id","JZYY");
        jsonObject.put("action_type","data_inte");

        System.out.println(jsonObject.toString());

        String re = HttpUtil.remoteRequest(urlStr,jsonObject.toString());
        System.out.println("result="+re);
        //{"retcode":"0","retdesc":"提交事中影像信息成功！","ret_code":"0","ret_msg":"提交事中影像信息成功！","retvalue":"提交事中影像信息成功！","check_code":""}
        return null;
    }


}

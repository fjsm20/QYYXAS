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
 * ���������󼯺�
 *
 * @author xiew 20170825
 */
public class ServiceUtil {
    private static Logger log = Logger.getLogger(ServiceUtil.class);

    //busToken(ҵ��ϵͳ����)��������
    private static Date createDate = null;
    //�ļ�������URL
    private static String fsServerURL = null;
    //ҵ��ϵͳ����
    private static String busToken = null;

    private ServiceUtil() {
        if (fsServerURL == null || StringUtils.isEmpty(fsServerURL)) {
            fsServerURL = ConfigUtil.getProperty(Constant.URL_IP);
        }
    }


    /**
     * ��ʼ��TokenManager,ֻ���ʼ��һ��
     *
     * @param serverURL �ļ�������URL
     * @return �Ƿ��ʼ���ɹ�
     */
    public static boolean init(String serverURL) throws Exception {
        fsServerURL = serverURL;
        if (busToken == null) {
            log.debug("�����ҵ������Ϊ��,���ļ���������ȡҵ��ϵͳ����");
            return iniLocalBusToken();
        } else {
            if (isExpired()) {
                log.debug("�����ҵ������ʧЧ,����ֵ[" + busToken + "],��������[" + createDate.toString() + "],���ļ���������ȡҵ��ϵͳ����");
                return iniLocalBusToken();
            } else {
                log.debug("�����ҵ��������Ч,����ֵ[" + busToken + "],��������[" + createDate.toString() + "]");
                return true;
            }
        }
    }

    /**
     * �ж�ҵ��ϵͳ�����Ƿ�ʧЧ
     *
     * @return �Ƿ�ʧЧ
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
        //�����ǰʱ�� �ڵ����23:59:59 �����ж�ΪʧЧ
        return nowCalendar.after(expiredCalendar);
    }

    /**
     * ��ʼ������ҵ��ϵͳ����(���ļ�����������ҵ��ϵͳ����),�����´���ʱ��
     *
     * @return �Ƿ��ʼ���ɹ�
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
     * ���ļ�����������ҵ��ϵͳ����
     *
     * @return ҵ��ϵͳ����
     */
    public static String getRemoteBusToken() throws Exception {

        if (fsServerURL == null || StringUtils.isEmpty(fsServerURL)) {
            fsServerURL = ConfigUtil.getProperty(Constant.URL_IP);
        }
        String appId = ConfigUtil.getProperty(Constant.APP_ID);
        if (fsServerURL.length() <= 1 || appId.length() <= 1) {
            log.info("���������쳣��url_ip=" + fsServerURL + " ,app_id=" + appId);
        }

        String data = HttpUtil.remoteRequest(fsServerURL + Constant.TOKEN_SEAL + appId, "");
        Map<String, String> map = null;
        try {
            map = (Map<String, String>) JSONObject.fromObject(data);
        } catch (Exception e) {
            log.error("��������Ч��JSON��ʽ");
            throw e;
        }
        String token = map.get("check_code");
        String retcode = map.get("retcode");
        if (retcode.equals("-1")) {
            log.error("��ȡӰ���ļ����������Ƴ���,��Ȩ�޷���");
            throw new Exception("��ȡӰ���ļ����������Ƴ���,��Ȩ�޷���");
        }
        if (retcode.equals("1301")) {
            log.error("������Ч");
            throw new Exception("������Ч");
        }
        if (StringUtils.isEmpty(token)) {
            log.error("��ȡӰ���ļ����������Ƶ�Ϊ��");
        }
        if (retcode.equals("0") && !StringUtils.isEmpty(token)) {
            return token;
        }
        return null;
    }

    /**
     * ����ҵ��ϵͳ����
     *
     * @return ҵ��ϵͳ����
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
     * ���ϴ��ļ�
     *
     * @param fileName  �ļ����� ����a.txt
     * @param certCode  ֤������
     * @param localFile ���ϴ��ļ�
     * @return string ��Ӧ�������
     * @throws Exception
     */
    public static String doUploadFile(String fileName, String certCode, File localFile) throws Exception {
        String token = getBusToken();
        String urlStr = fsServerURL + Constant.SIGNAL_UPLOAD_FILE + token;
        Map<String, String> param = new HashMap<String, String>();
        param.put("file_name", fileName);
        param.put("cert_code", certCode);

        String data = HttpUtil.remoteRequest(urlStr, param, new FileInputStream(localFile));
        log.info("�ϴ��ļ������" + data);
        //{"retcode":"0","retdesc":"�����ɹ�","sign_type":"1","file_id":"2967","total_md5":"a4f8ab95fe1d93a09be7b45403e43aa9","retvalue":"2967"}
        return data;
    }


    /**
     * ����Ӱ����Ϣ�ύ
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
        jsonObject.put("cust_name","����");
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
        //{"retcode":"0","retdesc":"�ύ����Ӱ����Ϣ�ɹ���","ret_code":"0","ret_msg":"�ύ����Ӱ����Ϣ�ɹ���","retvalue":"�ύ����Ӱ����Ϣ�ɹ���","check_code":""}
        return null;
    }


}

package com.shine.yxqy.thread;

import com.shine.yxqy.busi.ServiceUtil;
import com.shine.yxqy.po.UserDocument;
import com.shine.yxqy.po.YXFile;
import com.shine.yxqy.util.ConfigUtil;
import com.shine.yxqy.util.Constant;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * �ɼ�Ӱ�����ݴ����߼��߳�
 * Created by xiew on 2017-08-27.
 */
public class CollectDataExecuter implements Runnable {
    private static final Logger log = Logger.getLogger(CollectDataExecuter.class);
    private UserDocument userDocument;


    public CollectDataExecuter(UserDocument userDocument) {
        this.userDocument = userDocument;
    }


    @Override
    public void run() {
        try {
            String appId = ConfigUtil.getProperty(Constant.APP_ID);
            //��ȡ������ļ�
            List<YXFile> fileList = userDocument.getFileList();
            StringBuffer sourceNos = new StringBuffer();

            //�ϴ��ļ�
            if (fileList != null && fileList.size() >= 0) {
                for (YXFile yxFile : fileList) {

                    String filePath = yxFile.getAbsPath();
                    File file = new File(filePath);
                    String data = ServiceUtil.doUploadFile(yxFile.getFileName(), userDocument.getCertCode(), file);
                    JSONObject jsonMap = JSONObject.fromObject(data);
                    if (String.valueOf(jsonMap.get("retcode")).equals("0")) {
                        String fileId = String.valueOf(jsonMap.get("file_id"));
                        if (sourceNos.length() >= 1) {
                            sourceNos.append("|").append(yxFile.getSourceNo()).append(",").append(fileId);
                        } else {
                            sourceNos.append(yxFile.getSourceNo()).append(",").append(fileId);
                        }
                    } else {
                        log.info("�ϴ��ļ�ʧ��..");
                    }

                }

                if (StringUtils.isEmpty(userDocument.getDepCode())) {
                    userDocument.setDepCode("0004");
                }
                //�ύ��Ϣ
                Map<String, String> jsonData = new HashMap<String, String>();
                jsonData.put("method_id", "020123");
                jsonData.put("src_order_no", "WT"+userDocument.getId());
                jsonData.put("busi_code", userDocument.getBusiCode());
                jsonData.put("cust_prop", "0");
                jsonData.put("acct_code", userDocument.getAcctCode());
                jsonData.put("cert_type", userDocument.getCertType());
                jsonData.put("cert_code", userDocument.getCertCode());
                jsonData.put("cust_name", userDocument.getCustName());
                jsonData.put("opr_date",  userDocument.getOprDate());
                jsonData.put("dep_code",  userDocument.getDepCode());
                jsonData.put("file_num", "1");
                jsonData.put("file_check", "1");
                jsonData.put("source_nos", sourceNos.toString());
                jsonData.put("user_code", (userDocument.getUserCode().length()>=1)?userDocument.getUserCode(): appId);
                jsonData.put("app_id", appId);
                jsonData.put("action_type", "data_inte");


                String rsData = ServiceUtil.submitProcScanInfo(jsonData);
                JSONObject rsObj = JSONObject.fromObject(rsData);
                if (String.valueOf(rsObj.get("retcode")).equals("0")) {
                    log.info("�ύ��Ϣ�ɹ�");
                } else {
                    log.info("�ύ��Ϣʧ��");

                }


                //�鵵��Ϣ
                Map<String, String> jsonDataArch = new HashMap<String, String>();
                jsonDataArch.put("method_id", "020524");
                jsonDataArch.put("src_order_no", "WT"+userDocument.getId());
                jsonDataArch.put("busi_code", "{'2001':[]}");
                jsonDataArch.put("cust_prop", "0");
                jsonDataArch.put("acct_code", userDocument.getAcctCode());
                jsonDataArch.put("cert_type", userDocument.getCertType());
                jsonDataArch.put("cert_code", userDocument.getCertCode());
                jsonDataArch.put("cust_name", userDocument.getCustName());
                jsonDataArch.put("opr_date",  userDocument.getOprDate());
                jsonDataArch.put("dep_code",  userDocument.getDepCode());
                jsonDataArch.put("khfs","2");
                //���ʹ�õ���op_user_code ��key
                jsonDataArch.put("op_user_code", (userDocument.getUserCode().length()>=1)?userDocument.getUserCode(): appId);
                jsonDataArch.put("app_id", appId);
                jsonDataArch.put("stage", "9");
                jsonDataArch.put("sync", "0");
                jsonDataArch.put("expire_time", "10");
                jsonDataArch.put("action_type", "data_inte");




                System.out.println("�鵵��Ϣ��"+jsonDataArch.toString());
                rsData = ServiceUtil.archive020524(jsonDataArch);
                rsObj = JSONObject.fromObject(rsData);
                if (String.valueOf(rsObj.get("retcode")).equals("0")) {
                    log.info("�鵵��Ϣ�ɹ�\n" + rsData);
                } else {
                    log.info("�鵵��Ϣʧ��\n" + rsData);

                }


            }


        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("ҵ���߳�ִ��...Name=" + Thread.currentThread().getName() + " ," + userDocument.getCustName());
    }
}

package com.shine.yxqy.thread;

import com.shine.yxqy.busi.ServiceUtil;
import com.shine.yxqy.po.UserDocument;
import com.shine.yxqy.po.YXFile;
import com.shine.yxqy.util.ConfigUtil;
import com.shine.yxqy.util.Constant;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 采集影像数据处理逻辑线程
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
            //获取对象的文件
            List<YXFile> fileList = userDocument.getFileList();
            StringBuffer sourceNos = new StringBuffer();

            //上传文件
            if (fileList != null && fileList.size() >= 0) {
                for (YXFile yxFile : fileList) {

                    String filePath = yxFile.getRootPath() + File.separator + yxFile.getRelaPath() + File.separator + yxFile.getFileName();
                    File file = new File(filePath);
                    String data = ServiceUtil.doUploadFile(yxFile.getFileName(), userDocument.getCertCode(), file);
                    JSONObject jsonMap = JSONObject.fromObject(data);
                    if (String.valueOf(jsonMap.get("retcode")).equals("0")) {
                        String fileId = String.valueOf(jsonMap.get("file_id"));
                        if (sourceNos.length() >= 1) {
                            sourceNos.append("|").append(yxFile.getFileType()).append(",").append(fileId);
                        } else {
                            sourceNos.append(yxFile.getFileType()).append(fileId);
                        }
                    } else {
                        log.info("上传文件失败..");
                    }

                }

                //提交信息
                Map<String, String> jsonData = new HashMap<String, String>();
                jsonData.put("method_id", "020123");
                jsonData.put("src_order_no", "XY0012229");
                jsonData.put("busi_code", userDocument.getBusiCode());
                jsonData.put("cust_prop", userDocument.getCustProp());
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
                    log.info("提交信息成功");
                } else {
                    log.info("提交信息失败");

                }


            }


        } catch (Exception e) {

        }
        System.out.println("业务线程执行...Name=" + Thread.currentThread().getName() + " ," + userDocument.getCustName());
    }
}

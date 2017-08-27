package com.shine.yxqy.quarz;

import com.shine.yxqy.po.UserDocument;
import com.shine.yxqy.thread.TaskThreadPool;
import com.shine.yxqy.util.Constant;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DataCollectTask {
    public static Logger log = Logger.getLogger(DataCollectTask.class);


    public void requestLifeCtrl() {
        log.info("执行调度任务");
        System.out.println("执行调度任务："+new Date());
        String token = null;
        try {
//            token = ServiceUtil.getRemoteBusToken();
            System.out.println("token="+token);

//            /****上传文件***/
//            File file = new File("D:\\test\\aaf.pdf");
////
//            String data = ServiceUtil.doUploadFile(file.getName(),"2017001",file);
//            System.out.println("执行结果："+data);

            Map<String,String> jsonObject = new HashMap<String,String>();
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
            jsonObject.put("user_code", Constant.APP_ID);
            jsonObject.put("app_id","JZYY");
            jsonObject.put("action_type","data_inte");

//            ServiceUtil.submitProcScanInfo(null);

            for(int  i=0; i<=10;i++){
                UserDocument ud = new UserDocument();
                ud.setCertName(i+"one");

                TaskThreadPool.addTask(ud);
            }

            System.out.println("one--end");
            for(int  i=0; i<=10;i++){
                UserDocument ud = new UserDocument();
                ud.setCertName(i+",two");

                TaskThreadPool.addTask(ud);
            }

            System.out.println("two--end");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void execute()  {
        long ms = System.currentTimeMillis();
        System.out.println("\t\t" + new Date(ms));
    }


}

package com.shine.yxqy.quarz;

import com.shine.yxqy.busi.ServiceUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Date;

public class DataCollectTask {
    public static Logger log = Logger.getLogger(DataCollectTask.class);



    public void requestLifeCtrl() {
        log.info("ִ�е�������");
        System.out.println("ִ�е�������"+new Date());
        String token = null;
        try {
            token = ServiceUtil.getRemoteBusToken();
            System.out.println("token="+token);

//            /****�ϴ��ļ�***/
//            File file = new File("D:\\test\\aaf.pdf");
////
//            String data = ServiceUtil.doUploadFile(file.getName(),"2017001",file);
//            System.out.println("ִ�н����"+data);

            ServiceUtil.submitProcScanInfo(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void execute()  {
        long ms = System.currentTimeMillis();
        System.out.println("\t\t" + new Date(ms));
    }


}

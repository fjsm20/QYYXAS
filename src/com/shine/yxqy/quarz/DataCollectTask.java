package com.shine.yxqy.quarz;

import org.apache.log4j.Logger;

import java.util.Date;

public class DataCollectTask {
    public static Logger log = Logger.getLogger(DataCollectTask.class);



    public void requestLifeCtrl() {
        log.info("ִ�е�������");
        System.out.println("ִ�е�������"+new Date());
    }

    protected void execute()  {
        long ms = System.currentTimeMillis();
        System.out.println("\t\t" + new Date(ms));
    }


}

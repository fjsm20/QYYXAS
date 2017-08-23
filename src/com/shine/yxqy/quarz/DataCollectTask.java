package com.shine.yxqy.quarz;

import org.apache.log4j.Logger;

import java.util.Date;

public class DataCollectTask {
    public static Logger log = Logger.getLogger(DataCollectTask.class);



    public void requestLifeCtrl() {
        log.info("执行调度任务");
        System.out.println("执行调度任务："+new Date());
    }

    protected void execute()  {
        long ms = System.currentTimeMillis();
        System.out.println("\t\t" + new Date(ms));
    }


}

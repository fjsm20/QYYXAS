package com.shine.yxqy.thread;

import com.shine.yxqy.po.UserDocument;
import org.apache.log4j.Logger;

/**
 * �ɼ�Ӱ�����ݴ����߼��߳�
 * Created by zgfjs on 2017-08-27.
 */
public class CollectDataExecuter implements Runnable{
    private static final Logger logger = Logger.getLogger(CollectDataExecuter.class);
    private UserDocument userDocument;


    public CollectDataExecuter(UserDocument userDocument){
        this.userDocument = userDocument;
    }


    @Override
    public void run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("ҵ���߳�ִ��...Name="+Thread.currentThread().getName()+" ,"+userDocument.getCertName());
    }
}

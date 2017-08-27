package com.shine.yxqy.thread;

import com.shine.yxqy.po.UserDocument;
import org.apache.log4j.Logger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * �̳߳�
 * Created by xiew on 2017-08-27
 */
public class TaskThreadPool {
    private static final Logger log = Logger.getLogger(TaskThreadPool.class);
    public static ThreadPoolExecutor threadPoolExecutor;
    public int corePoolSize;				//�̳߳�ά���̵߳���������
    public int maximumPoolSize;   		//�̳߳�ά���̵߳��������
    public int keepAliveTime;    		//�̳߳�ά���߳�������Ŀ���ʱ��
    public int arrayBlockingQueueSize;	//������г���

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public int getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(int keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public int getArrayBlockingQueueSize() {
        return arrayBlockingQueueSize;
    }

    public void setArrayBlockingQueueSize(int arrayBlockingQueueSize) {
        this.arrayBlockingQueueSize = arrayBlockingQueueSize;
    }

    public void init() {
        //�����̳߳�
        if(threadPoolExecutor == null)
            threadPoolExecutor = new ThreadPoolExecutor(					//����Ĭ�ϲ��ԣ�����ʧ��ʱ���׳��쳣����������
                    corePoolSize,
                    maximumPoolSize,
                    keepAliveTime,
                    TimeUnit.SECONDS,										 //����ʱ��ĵ�λ
                    new ArrayBlockingQueue<Runnable>(arrayBlockingQueueSize),//������У����������������������߳�
                    Executors.defaultThreadFactory()						 //�߳�Ĭ�ϴ�������
            );

    }

    /**
     * �������Ӹ��̳߳ش���
     * @throws Exception
     */
    public static void addTask(UserDocument userDocument) throws Exception {
        if(userDocument!=null) {
            threadPoolExecutor.execute(new CollectDataExecuter(userDocument));
        }else{
            log.info("�������Ϊ�գ������д���");
        }
    }
}

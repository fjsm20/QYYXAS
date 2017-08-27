package com.shine.yxqy.thread;

import com.shine.yxqy.po.UserDocument;
import org.apache.log4j.Logger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池
 * Created by xiew on 2017-08-27
 */
public class TaskThreadPool {
    private static final Logger log = Logger.getLogger(TaskThreadPool.class);
    public static ThreadPoolExecutor threadPoolExecutor;
    public int corePoolSize;				//线程池维护线程的最少数量
    public int maximumPoolSize;   		//线程池维护线程的最大数量
    public int keepAliveTime;    		//线程池维护线程所允许的空闲时间
    public int arrayBlockingQueueSize;	//任务队列长度

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
        //创建线程池
        if(threadPoolExecutor == null)
            threadPoolExecutor = new ThreadPoolExecutor(					//采用默认策略：任务失败时，抛出异常，放弃任务
                    corePoolSize,
                    maximumPoolSize,
                    keepAliveTime,
                    TimeUnit.SECONDS,										 //空闲时间的单位
                    new ArrayBlockingQueue<Runnable>(arrayBlockingQueueSize),//任务队列，用来存放所定义的任务处理线程
                    Executors.defaultThreadFactory()						 //线程默认创建工厂
            );

    }

    /**
     * 将任务扔给线程池处理
     * @throws Exception
     */
    public static void addTask(UserDocument userDocument) throws Exception {
        if(userDocument!=null) {
            threadPoolExecutor.execute(new CollectDataExecuter(userDocument));
        }else{
            log.info("处理对象为空，不进行处理");
        }
    }
}

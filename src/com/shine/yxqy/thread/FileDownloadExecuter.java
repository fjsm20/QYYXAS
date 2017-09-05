package com.shine.yxqy.thread;

import com.shine.yxqy.po.UserDocument;
import com.shine.yxqy.util.Constant;
import com.shine.yxqy.util.FtpUtil;
import com.shine.yxqy.xml.XMLService;
import com.shine.yxqy.xml.impl.GJXMLServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 整个文件夹下载处理逻辑线程
 * Created by xiew on 2017-09-05.
 */
public class FileDownloadExecuter implements Runnable {
    private static final Logger log = Logger.getLogger(CollectDataExecuter.class);
    @Autowired
    XMLService gjxmlService;
    private String ftpUrl;      //FTP地址
    private String relaPath;    //FTP要下载的相对路径
    private String localPath;   //下载到本地的绝对路径

    public FileDownloadExecuter(String ftpUrl,String relaPath,String localPath){
        this.ftpUrl = ftpUrl;
        this.relaPath = relaPath;
        this.localPath = localPath;
    }


    @Override
    public void run() {
        log.info("进入文件夹下载线程：path="+relaPath);
        System.out.println("ftp="+ftpUrl+" ,relaPath="+relaPath+" ,localPaht="+localPath);
        long startTime = System.currentTimeMillis();
        try{
            FtpUtil.getDirFiles(ftpUrl,relaPath,localPath,new FtpUtil.FtpCallback() {
                @Override
                public void postSend() {}

                @Override
                public void onReceive(List<FtpUtil.FFile> fFileList) throws Exception {
                    if(fFileList ==null &&  fFileList.size()>=1){
                        throw new IOException("下载结束，总共下载文件个数：0");
                    }
                    System.out.println("下载结束，总共下载文件个数："+fFileList.size());

                    //进行XML文件的解析
                    String xmlPath = localPath+"/"+relaPath+"/"+ Constant.CUST_XML;
                    List<UserDocument> listUD = new ArrayList<UserDocument>();
                    System.out.println("进行文件解析：");
                    gjxmlService.iterateAnalysisXML(xmlPath,listUD);

                    if(listUD!=null && listUD.size()>=1){
                        System.out.println("开始业务上传：");
                        for(UserDocument ud :listUD) {
                            TaskThreadPool.addTask(ud);
                        }
                        System.out.println("结束业务上传：");
                    }
                }

                @Override
                public void onException(Exception e) {
                    e.printStackTrace();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("文件下载path="+relaPath+" ,耗时："+(endTime-startTime)+" 毫秒");
    }
}

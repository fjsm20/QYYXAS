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
 * �����ļ������ش����߼��߳�
 * Created by xiew on 2017-09-05.
 */
public class FileDownloadExecuter implements Runnable {
    private static final Logger log = Logger.getLogger(CollectDataExecuter.class);
    @Autowired
    XMLService gjxmlService;
    private String ftpUrl;      //FTP��ַ
    private String relaPath;    //FTPҪ���ص����·��
    private String localPath;   //���ص����صľ���·��

    public FileDownloadExecuter(String ftpUrl,String relaPath,String localPath){
        this.ftpUrl = ftpUrl;
        this.relaPath = relaPath;
        this.localPath = localPath;
    }


    @Override
    public void run() {
        log.info("�����ļ��������̣߳�path="+relaPath);
        System.out.println("ftp="+ftpUrl+" ,relaPath="+relaPath+" ,localPaht="+localPath);
        long startTime = System.currentTimeMillis();
        try{
            FtpUtil.getDirFiles(ftpUrl,relaPath,localPath,new FtpUtil.FtpCallback() {
                @Override
                public void postSend() {}

                @Override
                public void onReceive(List<FtpUtil.FFile> fFileList) throws Exception {
                    if(fFileList ==null &&  fFileList.size()>=1){
                        throw new IOException("���ؽ������ܹ������ļ�������0");
                    }
                    System.out.println("���ؽ������ܹ������ļ�������"+fFileList.size());

                    //����XML�ļ��Ľ���
                    String xmlPath = localPath+"/"+relaPath+"/"+ Constant.CUST_XML;
                    List<UserDocument> listUD = new ArrayList<UserDocument>();
                    System.out.println("�����ļ�������");
                    gjxmlService.iterateAnalysisXML(xmlPath,listUD);

                    if(listUD!=null && listUD.size()>=1){
                        System.out.println("��ʼҵ���ϴ���");
                        for(UserDocument ud :listUD) {
                            TaskThreadPool.addTask(ud);
                        }
                        System.out.println("����ҵ���ϴ���");
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
        System.out.println("�ļ�����path="+relaPath+" ,��ʱ��"+(endTime-startTime)+" ����");
    }
}

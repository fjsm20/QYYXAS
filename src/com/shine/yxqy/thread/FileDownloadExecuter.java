package com.shine.yxqy.thread;

import com.shine.yxqy.po.UserDocument;
import com.shine.yxqy.po.YXFile;
import com.shine.yxqy.util.Constant;
import com.shine.yxqy.util.FileUtil;
import com.shine.yxqy.util.FtpUtil;
import com.shine.yxqy.xml.XMLService;
import com.shine.yxqy.xml.impl.GJXMLServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private FTPClient ftpClient;

    public FileDownloadExecuter(String ftpUrl,String relaPath,String localPath){
        this.ftpUrl = ftpUrl;
        this.relaPath = relaPath;
        this.localPath = localPath;



        Map<String, Object> param = beforeOpenConnect(ftpUrl);
        try {
            String username = String.valueOf(param.get("username"));
            String password = String.valueOf(param.get("password"));
            String ip = String.valueOf(param.get("ip"));
            int port = Integer.parseInt(String.valueOf(param.get("port")));

            ftpLogin(ip, port, username, password);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        log.info(Thread.currentThread().getName()+"  �����ļ��������̣߳�path="+relaPath);
        System.out.println(Thread.currentThread().getName()+" ftp="+ftpUrl+" ,relaPath="+relaPath+" ,localPaht="+localPath);
        long startTime = System.currentTimeMillis();
        List<FtpUtil.FFile> fFileList = new ArrayList<FtpUtil.FFile>();
        try {
            if (ftpClient != null) {
                if (relaPath.contains(".")) {
                    System.out.println("������Ч�ļ�·��");
                } else {
                    System.out.println("��ʼ�����ļ�");
                    ftpClient.changeWorkingDirectory(new String(relaPath.getBytes("gbk"), "iso-8859-1"));
                    localPath = localPath + "/" + relaPath;

                    iterateFTPDir(localPath, fFileList);
                    System.out.println("����������size="+fFileList.size());

                    List<UserDocument> listUD = new ArrayList<UserDocument>();
                    XMLService xmlService = new GJXMLServiceImpl();
                    String custxmlPath = localPath+"/"+Constant.CUST_XML;
                    xmlService.iterateAnalysisXML(custxmlPath,listUD);
                    System.out.println("xml����������ͳ�ƣ�"+listUD.size());

                    for( UserDocument ud:listUD){
                        System.out.println("������"+ud.getCustName());
                        System.out.println("��Ч�ļ���"+ud.getFileList().size());
                        System.out.println("��Ч�ļ���"+ud.getLostFileList().size());
                        List<YXFile> list = ud.getFileList();
                        for(YXFile yf:list){
                            System.out.println(yf.getSourceNo()+" ,"+yf.getModuleName());
                        }

                        TaskThreadPool.addTask(ud);

                        //System.out.println(ud);
                    }


                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("�ļ�����path="+relaPath+" ,��ʱ��"+(endTime-startTime)+" ����");
    }



    public void ftpLogin(String url, int port, String username, String password) throws Exception {
        try {
            if (ftpClient != null && ftpClient.isConnected() && FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                return;
            }

            ftpLogout();
            ftpClient = null;
            ftpClient = new FTPClient();
            ftpClient.connect(url, port);
            if (!ftpClient.login(username, password)) {
                ftpClient.disconnect();
                ftpClient = null;
                throw new Exception("FPT���Ե�¼ʧ��");
            }
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            System.out.println("FTP����ɹ�");

        } catch (Exception e) {
            log.error("FTP���ӵ�¼�쳣��" + e.getMessage());
            throw e;
        }
    }

    /**
     * FTP�ǳ�
     */
    public void ftpLogout() {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                log.error("FTP�ǳ��쳣��" + e.getMessage());
            }
        }
    }

    /**
     * beforeOpenConnect���û�����ip�˿ڷŵ�map��
     * @param url ftp��url����ȡ���û�����ip�˿�
     * @return ����װ�Ų�����map
     */
    public Map<String, Object> beforeOpenConnect(String url) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isEmpty(url)) {
            return null;
        } else {
            String username = url.substring(6, url.indexOf(":", 4));
            String password = url.substring(url.indexOf(":", 4) + 1, url.lastIndexOf("@"));
            String ip = url.substring(url.lastIndexOf("@") + 1, url.lastIndexOf(":"));
            //�ж����һ��/��λ��
            int port = 21;
            int index = url.lastIndexOf("/");
            if (index > 5) {
                port = Integer.parseInt(url.substring(url.lastIndexOf(":") + 1, url.lastIndexOf("/")));
            } else {
                port = Integer.parseInt(url.substring(url.lastIndexOf(":") + 1));

            }
            map.put("username", username);
            map.put("password", password);
            map.put("ip", ip);
            map.put("port", port);
        }
        return map;
    }

    /**
     * ����FTPĿ¼����������Ч�ļ�
     * @param localPath ���ر����ļ���Ŀ¼��ַ
     * @param fFileList �ļ��м��ļ��б�
     * @throws IOException
     */
    public void iterateFTPDir(String localPath, List<FtpUtil.FFile> fFileList) throws IOException {
        OutputStream outputStream =null;
        try {
            FTPFile[] ftpFiles = ftpClient.listFiles();

            //�ж�ftpFiles�Ƿ���ֵ�������ļ�������0
            if (ftpFiles != null && ftpFiles.length > 0) {
                for (int i = 0; i < ftpFiles.length; i++) {
                    FTPFile file = ftpFiles[i];
                    if (file.isFile()) {
                        FileUtil.insureDirExist(localPath);
                        try {
                            //�����ļ���ַ�ַ���ʹ��ISO-8859-1���룬��ʹ��gbk���룬����ļ����������ģ����������
                            //����ʹ��utf-8����󣬾�����ʾ���������ġ�
                            outputStream = new FileOutputStream(localPath + "/" + new String(file.getName().getBytes("iso-8859-1"), "utf-8"));
                            System.out.println(new String((localPath + "/" + file.getName()).getBytes("iso-8859-1"), "utf-8"));
                        }catch (Exception e){
                            System.out.println("FTP��ȡ���ļ���Ϊ�Ƿ�·����"+new String(file.getName().getBytes("iso-8859-1"), "utf-8"));
                            e.printStackTrace();
                            continue;
                        }
                        //�����ļ�
                        ftpClient.retrieveFile(file.getName(), outputStream);
                        //���ļ���Ϣ��ӵ�fFileList��
                        fFileList.add(new FtpUtil.FFile("", file.getName(), file.getSize()));
                        if(outputStream !=null){
                            outputStream.flush();
                            outputStream.close();
                        }
                    } else {
                        String tmp = new String((file.getName()).getBytes("iso-8859-1"), "gbk");
                        System.out.println("--����Ŀ¼��" + tmp);
                        ftpClient.changeWorkingDirectory(file.getName().toString());
                        localPath = localPath + "/" + tmp;
                        iterateFTPDir(localPath,  fFileList);
                        Thread.sleep(10);
                        ftpClient.changeToParentDirectory();
                        localPath = localPath.substring(0,localPath.lastIndexOf("/"));
                        System.out.println("--���ظ���:"+tmp);

                    }
                }
            } else {
                System.out.println("Ŀ¼û����Ҫ���ص��ļ���"  );
//                log.info("Ŀ¼û����Ҫ���ص��ļ���" + relaPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(outputStream != null){
                outputStream.flush();
                outputStream.close();
            }
        }
    }
}

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
        log.info(Thread.currentThread().getName()+"  进入文件夹下载线程：path="+relaPath);
        System.out.println(Thread.currentThread().getName()+" ftp="+ftpUrl+" ,relaPath="+relaPath+" ,localPaht="+localPath);
        long startTime = System.currentTimeMillis();
        List<FtpUtil.FFile> fFileList = new ArrayList<FtpUtil.FFile>();
        try {
            if (ftpClient != null) {
                if (relaPath.contains(".")) {
                    System.out.println("不是有效文件路径");
                } else {
                    System.out.println("开始下载文件");
                    ftpClient.changeWorkingDirectory(new String(relaPath.getBytes("gbk"), "iso-8859-1"));
                    localPath = localPath + "/" + relaPath;

                    iterateFTPDir(localPath, fFileList);
                    System.out.println("解析结束：size="+fFileList.size());

                    List<UserDocument> listUD = new ArrayList<UserDocument>();
                    XMLService xmlService = new GJXMLServiceImpl();
                    String custxmlPath = localPath+"/"+Constant.CUST_XML;
                    xmlService.iterateAnalysisXML(custxmlPath,listUD);
                    System.out.println("xml解析结束，统计："+listUD.size());

                    for( UserDocument ud:listUD){
                        System.out.println("姓名："+ud.getCustName());
                        System.out.println("有效文件："+ud.getFileList().size());
                        System.out.println("无效文件："+ud.getLostFileList().size());
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
        System.out.println("文件下载path="+relaPath+" ,耗时："+(endTime-startTime)+" 毫秒");
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
                throw new Exception("FPT尝试登录失败");
            }
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            System.out.println("FTP登入成功");

        } catch (Exception e) {
            log.error("FTP连接登录异常：" + e.getMessage());
            throw e;
        }
    }

    /**
     * FTP登出
     */
    public void ftpLogout() {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                log.error("FTP登出异常：" + e.getMessage());
            }
        }
    }

    /**
     * beforeOpenConnect将用户密码ip端口放到map中
     * @param url ftp的url，提取出用户密码ip端口
     * @return 返回装着参数的map
     */
    public Map<String, Object> beforeOpenConnect(String url) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isEmpty(url)) {
            return null;
        } else {
            String username = url.substring(6, url.indexOf(":", 4));
            String password = url.substring(url.indexOf(":", 4) + 1, url.lastIndexOf("@"));
            String ip = url.substring(url.lastIndexOf("@") + 1, url.lastIndexOf(":"));
            //判断最后一个/的位置
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
     * 遍历FTP目录，并下载有效文件
     * @param localPath 本地保存文件的目录地址
     * @param fFileList 文件夹及文件列表
     * @throws IOException
     */
    public void iterateFTPDir(String localPath, List<FtpUtil.FFile> fFileList) throws IOException {
        OutputStream outputStream =null;
        try {
            FTPFile[] ftpFiles = ftpClient.listFiles();

            //判断ftpFiles是否有值，并且文件数大于0
            if (ftpFiles != null && ftpFiles.length > 0) {
                for (int i = 0; i < ftpFiles.length; i++) {
                    FTPFile file = ftpFiles[i];
                    if (file.isFile()) {
                        FileUtil.insureDirExist(localPath);
                        try {
                            //这里文件地址字符串使用ISO-8859-1解码，在使用gbk编码，如果文件名包含中文，会出现乱码
                            //反而使用utf-8编码后，就能显示正常的中文。
                            outputStream = new FileOutputStream(localPath + "/" + new String(file.getName().getBytes("iso-8859-1"), "utf-8"));
                            System.out.println(new String((localPath + "/" + file.getName()).getBytes("iso-8859-1"), "utf-8"));
                        }catch (Exception e){
                            System.out.println("FTP获取的文件名为非法路径："+new String(file.getName().getBytes("iso-8859-1"), "utf-8"));
                            e.printStackTrace();
                            continue;
                        }
                        //下载文件
                        ftpClient.retrieveFile(file.getName(), outputStream);
                        //将文件信息添加到fFileList中
                        fFileList.add(new FtpUtil.FFile("", file.getName(), file.getSize()));
                        if(outputStream !=null){
                            outputStream.flush();
                            outputStream.close();
                        }
                    } else {
                        String tmp = new String((file.getName()).getBytes("iso-8859-1"), "gbk");
                        System.out.println("--迭代目录：" + tmp);
                        ftpClient.changeWorkingDirectory(file.getName().toString());
                        localPath = localPath + "/" + tmp;
                        iterateFTPDir(localPath,  fFileList);
                        Thread.sleep(10);
                        ftpClient.changeToParentDirectory();
                        localPath = localPath.substring(0,localPath.lastIndexOf("/"));
                        System.out.println("--返回父层:"+tmp);

                    }
                }
            } else {
                System.out.println("目录没有需要下载的文件："  );
//                log.info("目录没有需要下载的文件：" + relaPath);
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

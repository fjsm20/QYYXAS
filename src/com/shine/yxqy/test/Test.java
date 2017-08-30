package com.shine.yxqy.test;


import com.shine.yxqy.busi.ServiceUtil;
import com.shine.yxqy.util.ConfigUtil;
import com.shine.yxqy.util.Constant;
import com.shine.yxqy.util.FileUtil;
import com.shine.yxqy.util.FtpUtil;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Test {
    public static void main(String[] args) {
        String url ="http://10.189.145.56:8080/ECIMC/jsp/service/encrypt.do";
        String param="{\n" +
                "'method_id' : '030921',\n" +
                "'app_id' : ' GJWT',\n" +
                "'busi_code': [],\n" +
                "'cust_name' : '发顺丰trtrtr',\n" +
                "'cert_type' : '0',\n" +
                "'cert_code' : '511423198708210016',\n" +
                "'begin_date':'20170801',\n" +
                "'end_date':'20170831',\n" +
                "'dep_code' : '',\n" +
                "'op_user_code' : '004318',\n" +
                "'action_type' : 'data_inte',\n" +
                "'cur_page':'1',\n" +
                "'page_size':'10'\n" +
                "}";
        try {
            /***http 请求***/
//            String rs = HttpUtil.remoteRequest(url,param);
//            System.out.println(rs);

            /***ftp 请求***/
            String addr = "ftp://administrator:gtja@2016@10.189.145.56:2122/";
            final String  ftpFile =  "/wd";
//            addr = ConfigUtil.getProperty(Constant.FTP_URL);
            System.out.println(addr);
//            FtpUtil.getFile(addr, ftpFile,new FtpUtil.FtpCallback() {
//                @Override
//                public void postSend() {
////                    logger.debug("[FTP]正在发起下载文件请求");
//                }
//
//                @Override，
//                public void onReceive(InputStream inStream) throws IOException {
//                    if(inStream ==null || (inStream!=null && inStream.available()<=0)){
//                        throw new IOException("FTP获取的文件流为空");
//                    }
//                    System.out.println(inStream.available());
////                    ctx.setFileSize(inStream.available());
////                    ctx.setFileStream(inStream);
////                    ctx.setIsSuccess(true);
//                    try {
//                        FileUtil.write("D:/test/",ftpFile,inStream);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onException(Exception e) {
//                    e.printStackTrace();
//                }
//            });

//            downloadDirFiles("A","D://test/123//","345");

             boolean flag = FtpUtil.getDirFiles(addr,"",new FtpUtil.FtpCallback(){

             });
            System.out.println("结果："+flag);

//            /****上传文件***/
//            /****上传文件***/
//            File file = new File("D:\\test\\aaf.pdf");
//            String data = ServiceUtil.doUploadFile(file.getName(),"2017001",file);
//            System.out.println("执行结果："+data);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 获取整个文件夹及其文件
     *
     * @param remotePath 文件夹名称（abc 或者 /1/2/abc）
     */
    //本方法用于下载FTP上的目录结构到本地中
    public static void downloadDirFiles(String remotePath, String rootPath, String relaPath) throws IOException {
        if (remotePath != null && !remotePath.equals("")) {
            try {
                FtpUtil.ftpLogin("10.189.145.56", 2122, "administrator", "gtja@2016");
                FtpUtil.ftpClient.changeWorkingDirectory("A");
                FTPFile[] ftpFiles = FtpUtil.ftpClient.listFiles();
                if (ftpFiles != null && ftpFiles.length > 0) {
                    for (int i = 0; i < ftpFiles.length; i++) {
                        System.out.println("fileName="+ftpFiles[i].getName()+",》="+ftpFiles[i].isFile());
                    }
                }
                System.out.println(ftpFiles.length);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                FtpUtil.ftpLogout();
            }


//            //在本地建立一个相同的文件目录
//            File localFile = new File(rootPath + File.separator + relaPath);
//            localFile.mkdirs();
//            String localPath = localFile.getAbsolutePath();
//            System.out.println(localPath);
//            //获得FTPFile对象数组
//            FTPFile[] ftpFiles = ftpClient.listFiles();
//            if (ftpFiles != null && ftpFiles.length > 0) {
//                for (int i = 0; i < ftpFiles.length; i++) {
//                    FTPFile subFile = ftpFiles[i];
//                    if (subFile.isDirectory()) {
//                        String tmpRemotepath = remotePath + File.separator + subFile.getName();
//                        downloadDirFiles(tmpRemotepath, rootPath, subFile.getName());
//                    } else {
//                        downloadFileFromFtp(remotePath, subFile.getName());
//                    }
//                }
//            }
        }

    }

//    /**
//     * 方法用于从FTP服务器中下载文件
//     *
//     * @param remotePath :下载文件所处FTP中路径
//     * @param fileName :下载的文件名称
//     * @param outputSream :下载文件的输出流对象
//     *
//     * @return boolean :表示是否上传成功
//     *
//     */
//    public static boolean downloadFileFromFtp(String remotePath, String fileName) {
//        boolean bool = false;
//        try {
//            ftpClient.changeWorkingDirectory(remotePath);
//            FTPFile[] ftpFile = ftpClient.listFiles();
//            for (int i = 0; i < ftpFile.length; i++) {
//                if (fileName.equals(ftpFile[i].getName())) {
//                    InputStream is = ftpClient.retrieveFileStream(fileName);
//                    FileUtil.write("D://test//123/",fileName,is);
//                }
//            }
//            bool = true;
//        } catch (IOException e) {
//            e.printStackTrace();
//            bool = false;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return bool;
//    }
}

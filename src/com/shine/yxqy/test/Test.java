package com.shine.yxqy.test;


import com.shine.yxqy.busi.ServiceUtil;
import com.shine.yxqy.util.ConfigUtil;
import com.shine.yxqy.util.Constant;
import com.shine.yxqy.util.FileUtil;
import com.shine.yxqy.util.FtpUtil;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.util.List;

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
            final String  ftpFile =  "/91";
            final String localFile= "D://test//";
//            addr = ConfigUtil.getProperty(Constant.FTP_URL);
            long sTime = System.currentTimeMillis();
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




            FtpUtil.getDirFiles(addr, ftpFile,localFile,new FtpUtil.FtpCallback() {
                @Override
                public void postSend() {
//                    logger.debug("[FTP]正在发起下载文件请求");
                }

                @Override
                public void onReceive(List<FtpUtil.FFile> fFileList) throws IOException {
                    if(fFileList ==null &&  fFileList.size()>=1){
                        throw new IOException("没有需要下载的文件");
                    }
                    System.out.println("解析结束，总共文件下载:size="+fFileList.size());
//                    for (FtpUtil.FFile fFile : fFileList) {
//                        System.out.println("结果分析：" + fFile.getRelapath() + "/" + fFile.getFileName() + ",size=" + fFile.getFileSize());
//                    }
                }

                @Override
                public void onException(Exception e) {
                    e.printStackTrace();
                }
            });
            long eTime = System.currentTimeMillis();
            System.out.println("耗时："+(eTime-sTime));

//            downloadDirFiles("A","D://test/123//","345");
//
//            boolean flag = FtpUtil.getDirFiles(addr,"",new FtpUtil.FtpCallback(){
//
//            });
//            System.out.println("结果："+flag);

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
     *
     * 下载FTP文件
     * 当你需要下载FTP文件的时候，调用此方法
     * 根据<b>获取的文件名，本地地址，远程地址</b>进行下载
     *
     * @param ftpFile
     * @param relativeLocalPath
     * @param relativeRemotePath
     */
//    private  static void downloadFile(FTPFile ftpFile, String relativeLocalPath,String relativeRemotePath) {
//        if (ftpFile.isFile()) {
//            if (ftpFile.getName().indexOf("?") == -1) {
//                OutputStream outputStream = null;
//                try {
//                    File locaFile= new File(relativeLocalPath+ ftpFile.getName());
//                    //判断文件是否存在，存在则返回
//                    if(locaFile.exists()){
//                        return;
//                    }else{
//                        outputStream = new FileOutputStream(relativeLocalPath+ ftpFile.getName());
//                        ftp.retrieveFile(ftpFile.getName(), outputStream);
//                        outputStream.flush();
//                        outputStream.close();
//                    }
//                } catch (Exception e) {
//                    System.out.println(e);
//                } finally {
//                    try {
//                        if (outputStream != null){
//                            outputStream.close();
//                        }
//                    } catch (IOException e) {
//                        System.out.println(e);
//                    }
//                }
//            }
//        } else {
//            String newlocalRelatePath = relativeLocalPath + ftpFile.getName();
//            String newRemote = new String(relativeRemotePath+ ftpFile.getName().toString());
//            File fl = new File(newlocalRelatePath);
//            if (!fl.exists()) {
//                fl.mkdirs();
//            }
//            try {
//                newlocalRelatePath = newlocalRelatePath + '/';
//                newRemote = newRemote + "/";
//                String currentWorkDir = ftpFile.getName().toString();
//                boolean changedir = ftp.changeWorkingDirectory(currentWorkDir);
//                if (changedir) {
//                    FTPFile[] files = null;
//                    files = ftp.listFiles();
//                    for (int i = 0; i < files.length; i++) {
//                        downloadFile(files[i], newlocalRelatePath, newRemote);
//                    }
//                }
//                if (changedir){
//                    ftp.changeToParentDirectory();
//                }
//            } catch (Exception e) {
//                logger.error(e);
//            }
//        }
//    }
//
//
//    public static void main(String[] args) throws Exception{
//        Ftp f=new Ftp();
//        f.setIpAddr("1111");
//        f.setUserName("root");
//        f.setPwd("111111");
//        FtpUtil.connectFtp(f);
//        File file = new File("F:/test/com/test/Testng.java");
//        FtpUtil.upload(file);//把文件上传在ftp上
//        FtpUtil.startDown(f, "e:/",  "/xxtest");//下载ftp文件测试
//        System.out.println("ok");
//
//    }

}

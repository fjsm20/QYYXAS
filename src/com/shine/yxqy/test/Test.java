package com.shine.yxqy.test;


import com.shine.yxqy.busi.ServiceUtil;
import com.shine.yxqy.thread.TaskThreadPool;
import com.shine.yxqy.util.ConfigUtil;
import com.shine.yxqy.util.Constant;
import com.shine.yxqy.util.FileUtil;
import com.shine.yxqy.util.FtpUtil;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

            testDownloadThread();


//            FtpUtil.getDirFiles(addr, ftpFile,localFile,new FtpUtil.FtpCallback() {
//                @Override
//                public void postSend() {
////                    logger.debug("[FTP]正在发起下载文件请求");
//                }
//
//                @Override
//                public void onReceive(List<FtpUtil.FFile> fFileList) throws IOException {
//                    if(fFileList ==null &&  fFileList.size()>=1){
//                        throw new IOException("没有需要下载的文件");
//                    }
//                    System.out.println("解析结束，总共文件下载:size="+fFileList.size());
////                    for (FtpUtil.FFile fFile : fFileList) {
////                        System.out.println("结果分析：" + fFile.getRelapath() + "/" + fFile.getFileName() + ",size=" + fFile.getFileSize());
////                    }
//                }
//
//                @Override
//                public void onException(Exception e) {
//                    e.printStackTrace();
//                }
//            });
//            long eTime = System.currentTimeMillis();
//            System.out.println("耗时："+(eTime-sTime));

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




    //业务信息提交测试
    public void submitProcScanInfo() throws Exception {
        Map<String, String> jsonObject = new HashMap<String, String>();
        jsonObject.put("method_id", "020123");
        jsonObject.put("src_order_no", "XY0012229");
        jsonObject.put("busi_code", "2100500000");
        jsonObject.put("cust_prop", "0");
        jsonObject.put("acct_code", "10000000911");
        jsonObject.put("cert_type", "0");
        jsonObject.put("cert_code", "1122331");
        jsonObject.put("cust_name", "张三");
        jsonObject.put("opr_date", "20170826");
        jsonObject.put("dep_code", "3106");
        jsonObject.put("file_num", "1");
        jsonObject.put("file_check", "1");
        jsonObject.put("source_nos", "70,2945|429,2947");
        jsonObject.put("user_code", Constant.APP_ID);
        jsonObject.put("app_id", "JZYY");
        jsonObject.put("action_type", "data_inte");

        ServiceUtil.submitProcScanInfo(jsonObject);

    }


    public static void testDownloadThread(){
        String ftpUrl = "ftp://administrator:gtja@2016@10.189.145.56:2122/";
        String relaPath = "20150416";
        String localpath="D//test//test";

        try {
            TaskThreadPool.addDownloadTask(ftpUrl,relaPath,localpath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

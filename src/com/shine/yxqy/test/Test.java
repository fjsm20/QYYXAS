package com.shine.yxqy.test;


import com.shine.yxqy.util.FtpUtil;

import java.io.File;
import java.io.FileOutputStream;
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
            String addr = "ftp://ftp2:ftp@192.168.1.7:21";
            String ftpFile =  "/f.txt";

            FtpUtil.getFile(addr, ftpFile,new FtpUtil.FtpCallback() {
                @Override
                public void postSend() {
//                    logger.debug("[FTP]正在发起下载文件请求");
                }

                @Override
                public void onReceive(InputStream inStream) throws IOException {
                    if(inStream ==null || (inStream!=null && inStream.available()<=0)){
                        throw new IOException("FTP获取的文件流为空");
                    }
                    System.out.println(inStream.available());
//                    ctx.setFileSize(inStream.available());
//                    ctx.setFileStream(inStream);
//                    ctx.setIsSuccess(true);
                    FileOutputStream fop = new FileOutputStream(new File("D:/test/1.txt"));
                    int len = 0;
                    byte[] buf = new byte[1024];
                    while((len=inStream.read(buf))>=0) {
                        fop.write(buf, 0, len);
                    }
                    fop.close();
                }

                @Override
                public void onException(Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

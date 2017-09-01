package com.shine.yxqy.util;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FTP工具包
 * Created by xiew on 2017/5/24.
 */
public class FtpUtil {
    private static final Logger log = Logger.getLogger(FtpUtil.class);
    public static FTPClient ftpClient;

    /**
     * 获取整个文件夹及其文件
     *
     * @param remotePath 文件夹名称（abc 或者 /1/2/abc）
     */
    //本方法用于下载FTP上的目录结构到本地中
    public static boolean getDirFiles(String url,String remotePath,FtpUtil.FtpListener listener) throws IOException {
        boolean doFlag = false;
        Map<String, Object> param;
        FtpHelper ftpHelper = new FtpHelper(listener);
        param = ftpHelper.beforeOpenConnect(url);
        try {
            String username = String.valueOf(param.get("username"));
            String password = String.valueOf(param.get("password"));
            String ip = String.valueOf(param.get("ip"));
            int port = Integer.parseInt(String.valueOf(param.get("port")));

            ftpHelper.beforeConnect();
            ftpLogin(ip, port, username, password);
            ftpHelper.onConnect();

            List<FFile> fFileList = new ArrayList<FFile>();

            if (ftpClient != null) {
                iterateFTPDir(remotePath, remotePath, fFileList);
                doFlag= true;
                for (FFile fFile : fFileList) {
                    System.out.println("结果分析：" + fFile.relapath + "/" + fFile.fileName + ",size=" + fFile.getFileSize());
                }
//                if(ftpClient.changeWorkingDirectory(remotePath)){
//                    FTPFile[] ftpFiles = ftpClient.listFiles();
//                    if (ftpFiles != null && ftpFiles.length > 0) {
//                        for (int i = 0; i < ftpFiles.length; i++) {
//                            FTPFile file = ftpFiles[i];
//                            if(file.isFile()){
//                                fFileList.add(new FFile(remotePath,file.getName(),file.getSize()));
//                                System.out.println("fileName="+file.getName()+",》="+file.isFile());
//                            }else{
//                                getDirFiles(url,remotePath+File.separator+file.getName(),listener);
//                            }
//                        }
//
//                        for(FFile fFile:fFileList){
//                            System.out.println(fFile.relapath+File.separator+fFile.fileName+",size="+fFile.getFileSize());
//                        }
//                    }else{
//                        log.info("目录没有需要下载的文件："+remotePath);
//                    }
//
//
//                }else{
//                    log.info("FTP切换目录失败："+remotePath);
//                }

            }



        } catch (Exception e) {
            e.printStackTrace();
        }
        return doFlag;

    }

    public static void iterateFTPDir(String rootPath,String relaPath,List<FFile> fFileList) throws IOException {
        boolean flag = false;
        try{
            System.out.println("当前目录1："+new String(ftpClient.printWorkingDirectory().getBytes("iso-8859-1"),"gbk"));
            flag=  ftpClient.changeWorkingDirectory(rootPath);
            flag=  ftpClient.changeWorkingDirectory(new String(relaPath.getBytes("gbk"),"iso-8859-1"));
            System.out.println("当前目录2："+new String(ftpClient.printWorkingDirectory().getBytes("iso-8859-1"),"gbk")+" ,FTP切换目录："+relaPath);

        }catch (Exception e){
            e.printStackTrace();
        }
        if(flag){
            FTPFile[] ftpFiles = ftpClient.listFiles();
            if (ftpFiles != null && ftpFiles.length > 0) {
                for (int i = 0; i < ftpFiles.length; i++) {
                    FTPFile file = ftpFiles[i];
                    if(file.isFile()){
                        fFileList.add(new FFile(relaPath,file.getName(),file.getSize()));
                    }else{
                        String tmpPath = relaPath+"/"+new String((file.getName()).getBytes("iso-8859-1"),"gbk")+"/";
                        iterateFTPDir(rootPath,tmpPath,fFileList);
                    }
                }
            }else{
                System.out.println("目录没有需要下载的文件："+relaPath);
                log.info("目录没有需要下载的文件："+relaPath);
            }
        }else{
            System.out.println("当前目录："+new String(ftpClient.printWorkingDirectory().getBytes("iso-8859-1"),"gbk")+" ,FTP切换目录失败："+relaPath);
            log.info("FTP切换目录失败："+relaPath);
        }

    }


    /**
     * 获取文件
     *
     * @param url
     */
    public static void getFile(String url, String ftpFile, FtpListener listener) throws Exception {
        Map<String, Object> param;
        InputStream is;
        FtpHelper ftpHelper = new FtpHelper(listener);
        param = ftpHelper.beforeOpenConnect(url);
        try {
            String username = String.valueOf(param.get("username"));
            String password = String.valueOf(param.get("password"));
            String ip = String.valueOf(param.get("ip"));
            int port = Integer.parseInt(String.valueOf(param.get("port")));

            ftpHelper.beforeConnect();
            ftpLogin(ip, port, username, password);
            ftpHelper.onConnect();

            if (ftpClient != null) {
                is = ftpClient.retrieveFileStream(ftpFile);
                Thread.sleep(100);
                if (is == null) {
                    throw new Exception("流为空：ftpFile=" + ftpFile);
                }
                ftpHelper.onReceive(is);
            }


        } catch (Exception e) {
            ftpHelper.onException(e);
            log.error("FTP获取文件失败," + e.getMessage());
            throw new Exception(e);
        } finally {
            ftpLogout();
        }
    }

    /**
     * 登录
     *
     * @param url
     * @param port
     * @param username
     * @param password
     * @throws Exception
     */
    public static void ftpLogin(String url, int port, String username, String password) throws Exception {
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

        } catch (Exception e) {
            log.error("FTP连接登录异常：" + e.getMessage());
            throw e;
        }
    }

    /**
     * FTP登出
     */
    public static void ftpLogout() {
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
	 * 获得FTP服务器上指定目录的文件列表包括文件和文件夹
	 * @param ftpUrl 例如ftp://username:password@ip:port
	 * @param path ftp服务器文件路径
	 * @return 返回FTPFile的数组
	 */
	public static FTPFile[] listFiles(String ftpUrl, String path) {
		Map<String, Object> param;
		FtpHelper ftpHelper = new FtpHelper(new FtpUtil.FtpCallback());
		param = ftpHelper.beforeOpenConnect(ftpUrl);
		try {
			String username = String.valueOf(param.get("username"));
			String password = String.valueOf(param.get("password"));
			String ip = String.valueOf(param.get("ip"));
			int port = Integer.parseInt(String.valueOf(param.get("port")));

			ftpHelper.beforeConnect();
			ftpLogin(ip, port, username, password);
			ftpHelper.onConnect();

			if (ftpClient != null) {
				return ftpClient.listFiles(path);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ftpLogout();
		}

		return null;
	}

    private interface FtpListener {
        void onException(Exception e);

        Map<String, String> addParam();

        InputStream addStream();

        byte[] addBytes();

        void postSend();

        void onReceive(InputStream inStream) throws IOException;
    }

    /**
     * 回调函数
     */
    public static class FtpCallback implements FtpListener {
        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }

        @Override
        public void onException(Exception e) {

        }

        @Override
        public Map<String, String> addParam() {
            return null;
        }

        @Override
        public InputStream addStream() {
            return null;
        }

        @Override
        public byte[] addBytes() {
            return new byte[0];
        }

        @Override
        public void postSend() {

        }

        @Override
        public void onReceive(InputStream inStream) throws IOException {

        }
    }


    private static class FtpHelper {
        protected boolean isConnectOpened;
        protected boolean isConnect;
        protected boolean isBroken = false;
        private FtpListener listener;

        public FtpHelper(FtpListener listener) {
            this.listener = listener;
        }


        /**
         * 打开连接前触发
         * 解析ftp的URL：ftp://ftpuser:123321@127.0.0.1:21/33
         * 注意：如果有相对路径，只允许'/'
         *
         * @param url 请求地址
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
         * 建立连接前触发
         */
        public void beforeConnect() {
            isConnectOpened = true;
        }

        /**
         * 建立连接后触发
         */
        public void onConnect() {
            isConnect = true;
        }

        /**
         * 请求数据发送结束后触发
         */
        public void postSend() {
            if (isConnected())
                listener.postSend();
        }

        /**
         * 接收到请求响应时触发
         *
         * @param inStream InputStream
         */
        public void onReceive(InputStream inStream) throws IOException {
            if (isConnect && !isBroken) {
                listener.onReceive(inStream);
            }
        }

        /**
         * 任意时刻发生异常时触发
         *
         * @param e Exception
         */
        public void onException(Exception e) {
            isBroken = true;
            listener.onException(e);
        }

        protected boolean isConnected() {
            return isConnect && !isBroken;
        }

    }

    /**
     * FTP文件自定义FILE类
     */
    private static class FFile{
        private String fileName;
        private Long fileSize;
        private String relapath;

        public FFile(String relapath,String fileName,Long fileSize){
            this.relapath = relapath;
            this.fileName = fileName;
            this.fileSize = fileSize;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public Long getFileSize() {
            return fileSize;
        }

        public void setFileSize(Long fileSize) {
            this.fileSize = fileSize;
        }

        public String getRelapath() {
            return relapath;
        }

        public void setRelapath(String relapath) {
            this.relapath = relapath;
        }
    }
}

package com.shine.yxqy.util;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FTP���߰�
 * Created by xiew on 2017/5/24.
 */
public class FtpUtil {
    private static final Logger log = Logger.getLogger(FtpUtil.class);
    public static FTPClient ftpClient;

    /**
     * ��ȡ�����ļ��м����ļ�����������FTP�ϵ�Ŀ¼�ṹ�������У�
     * @param url FTP��URL��ftp://ftpuser:123321@127.0.0.1:21/33
     * @param remotePath FTP�����ص��ļ��У�abc ���� /1/2/abc��
     * @param localPath  ��ű���·����C://ABC//102��
     * @param listener �ص�����
     */
    public static boolean getDirFiles(String url,String remotePath,String localPath,FtpUtil.FtpListener listener) throws IOException {
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
            System.out.println("FTP����ɹ�");
            ftpHelper.onConnect();

            List<FFile> fFileList = new ArrayList<FFile>();

            if (ftpClient != null) {
                if(remotePath.contains(".")) {
                    System.out.println("������Ч�ļ�·��");
                }else {
                    ftpClient.changeWorkingDirectory(new String(remotePath.getBytes("gbk"), "iso-8859-1"));
                    localPath = localPath+"/"+remotePath;
                    iterateFTPDir(localPath,  fFileList);
                    doFlag = true;
                    ftpHelper.onReceive(fFileList);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            System.out.println("�ǳ�");
            ftpLogout();
        }
        return doFlag;

    }

    /**
     * ���������ļ���
     *
     * @param localPath  ftp��·��
     * @param relaPath  ftp���·��
     * @param fFileList ��ű����ļ����
     * @throws IOException
     */
    public static void iterateFTPDir(String localPath, List<FFile> fFileList) throws IOException {
        OutputStream outputStream =null;
        try {
            FTPFile[] ftpFiles = ftpClient.listFiles();
            if (ftpFiles != null && ftpFiles.length > 0) {
                for (int i = 0; i < ftpFiles.length; i++) {
                    FTPFile file = ftpFiles[i];
                    if (file.isFile()) {
                        FileUtil.insureDirExist(localPath);
                        try {
                            outputStream = new FileOutputStream(localPath + "/" + new String(file.getName().getBytes("iso-8859-1"), "gbk"));
                            System.out.println(new String((localPath + "/" + file.getName()).getBytes("iso-8859-1"), "gbk"));
                        }catch (Exception e){
                            System.out.println("FTP��ȡ���ļ���Ϊ�Ƿ�·����"+new String(file.getName().getBytes("iso-8859-1"), "gbk"));
                            e.printStackTrace();
                            continue;
                        }
                        ftpClient.retrieveFile(file.getName(), outputStream);
                        fFileList.add(new FFile("", file.getName(), file.getSize()));
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
            if(outputStream !=null){
                outputStream.flush();
                outputStream.close();
            }
        }

    }
 

    /**
     * ��ȡ�ļ�
     * @param url ftp��ַ
     * @param relaPath ftp���·��
     * @param ftpFile �ļ�����
     * @param listener �ص�����
     */
    public static void getFile(String url,String relaPath, String ftpFile, FtpListener listener) throws Exception {
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
                if(StringUtils.isNotEmpty(relaPath)){
                    ftpClient.changeWorkingDirectory(relaPath);
                }
                is = ftpClient.retrieveFileStream(ftpFile);
                Thread.sleep(100);
                if (is == null) {
                    throw new Exception("��Ϊ�գ�ftpFile=" + ftpFile);
                }
                ftpHelper.onReceive(is,relaPath);
            }


        } catch (Exception e) {
            ftpHelper.onException(e);
            log.error("FTP��ȡ�ļ�ʧ��," + e.getMessage());
            throw new Exception(e);
        } finally {
            ftpLogout();
        }
    }

    /**
     * ��¼
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
                throw new Exception("FPT���Ե�¼ʧ��");
            }
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

        } catch (Exception e) {
            log.error("FTP���ӵ�¼�쳣��" + e.getMessage());
            throw e;
        }
    }

    /**
     * FTP�ǳ�
     */
    public static void ftpLogout() {
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
	 * ���FTP��������ָ��Ŀ¼���ļ��б�����ļ����ļ���
	 * @param ftpUrl ����ftp://username:password@ip:port
	 * @param path ftp�������ļ�·��
	 * @return ����FTPFile������
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

        void onReceive(List<FFile> fFileList) throws Exception;

        void onReceive(InputStream inStream,String relaPath) throws Exception;

    }

    /**
     * �ص�����
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

        @Override
        public void onReceive(List<FFile> fFileList) throws Exception {

        }

        @Override
        public void onReceive(InputStream inStream,String relaPath) throws Exception {

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
         * ������ǰ����
         * ����ftp��URL��ftp://ftpuser:123321@127.0.0.1:21/33
         * ע�⣺��������·����ֻ����'/'
         *
         * @param url �����ַ
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
         * ��������ǰ����
         */
        public void beforeConnect() {
            isConnectOpened = true;
        }

        /**
         * �������Ӻ󴥷�
         */
        public void onConnect() {
            isConnect = true;
        }

        /**
         * �������ݷ��ͽ����󴥷�
         */
        public void postSend() {
            if (isConnected())
                listener.postSend();
        }

        /**
         * ���յ�������Ӧʱ����
         *
         * @param inStream InputStream
         */
        public void onReceive(InputStream inStream) throws IOException {
            if (isConnect && !isBroken) {
                listener.onReceive(inStream);
            }
        }

        /**
         * ���յ�������Ӧʱ����
         *
         * @param fFileList
         */
        public void onReceive(List<FFile> fFileList) throws Exception {
            if (isConnect && !isBroken) {
                listener.onReceive(fFileList);
            }
        }

        /**
         * ���յ�������Ӧʱ����
         *
         * @param relaPath ���·��
         */
        public void onReceive(InputStream inStream,String relaPath) throws Exception {
            if (isConnect && !isBroken) {
                listener.onReceive(inStream,relaPath);
            }
        }

        /**
         * ����ʱ�̷����쳣ʱ����
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
     * FTP�ļ��Զ���FILE��
     */
    public static class FFile{
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

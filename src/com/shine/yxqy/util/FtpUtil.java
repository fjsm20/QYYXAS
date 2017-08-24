package com.shine.yxqy.util;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * FTP���߰�
 * Created by xiew on 2017/5/24.
 */
public class FtpUtil {
    private static final Logger log = Logger.getLogger(FtpUtil.class);
    private static FTPClient ftpClient;

    /**
     * ��ȡ�ļ�
     *
     * @param url
     */
    public static void getFile(String url, String ftpFile, FtpListener listener) throws Exception{
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
                if(is ==null){
                    throw new Exception("��Ϊ�գ�ftpFile="+ftpFile);
                }
                ftpHelper.onReceive(is);
            }


        } catch (Exception e) {
            ftpHelper.onException(e);
            log.error("FTP��ȡ�ļ�ʧ��," + e.getMessage());
            throw new Exception(e);
        } finally {
//            ftpLogout();
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


    private interface FtpListener {
        void onException(Exception e);

        Map<String, String> addParam();

        InputStream addStream();

        byte[] addBytes();

        void postSend();

        void onReceive(InputStream inStream) throws IOException;
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
}

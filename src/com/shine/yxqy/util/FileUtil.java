package com.shine.yxqy.util;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;

/**
 * �ļ���������
 */
public class FileUtil {
    private static final Logger log = Logger.getLogger(FileUtil.class);

    /**
     * �ж��ļ�Ŀ¼�Ƿ���ڣ����Ŀ¼�����ھʹ���Ŀ¼
     */
    public static void createFileDir(String filePath) {
        String dirPath = getFileDirByPath(filePath);
        File file = new File(dirPath);
        if (file.exists() == false)
            file.mkdirs();
    }

    /**
     * �����ļ�·������ļ�������Ŀ¼
     *
     * @return
     */
    public static String getFileDirByPath(String filePath) {
        int endIndex1 = filePath.lastIndexOf("/");
        int endIndex2 = filePath.lastIndexOf("\\");
        if (endIndex1 < endIndex2)
            endIndex1 = endIndex2;

        return filePath.substring(0, endIndex1 + 1);
    }

    /**
     * ���Ŀ¼�Ƿ���ڣ������������½�
     *
     * @param fileDir �ļ�Ŀ¼·��
     * @return �Ƿ����
     */
    public static boolean insureDirExist(String fileDir) {
        File file = new File(fileDir);
        return file.exists() || file.mkdirs() || file.exists();
    }

    /**
     * ������ȡ�ֽ�
     *
     * @param in
     * @return
     */
    public static byte[] inputStreamToBytes(InputStream in) {
        int exceptionLength = 0;
        try {
            int len = 0;
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((len = in.read(buffer)) != -1) {
                exceptionLength = exceptionLength + len;
                baos.write(buffer, 0, len);
            }
            log.info("���ļ����ж�ȡ���ļ�����[" + baos.size() + "]");
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("��ȡ�ļ���ʧ��,�Ѷ�ȡ[" + exceptionLength + "]");
        }
        return null;
    }

    /**
     * ���ļ���д�뵽ָ�����ļ���
     *
     * @param fileDir  �ļ�Ŀ¼
     * @param fileName �ļ���
     * @param in       �ļ���
     */
    public static long write(String fileDir, String fileName, InputStream in) throws Exception {
        byte[] data = inputStreamToBytes(in);
        return write(fileDir, fileName, data);
    }

    /**
     * ���ļ���д�뵽ָ�����ļ���
     *
     * @param fileDir  �ļ�Ŀ¼
     * @param fileName �ļ���
     * @param data     �ļ���
     */
    public static long write(String fileDir, String fileName, byte[] data) throws Exception {
        if (!insureDirExist(fileDir)) {
            throw new Exception("�����ļ��洢Ŀ¼ʧ��!");
        }
        File file = new File(fileDir + fileName);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    throw new Exception("�����ļ�ʧ��!");
                }
            } catch (IOException e) {
                throw new Exception("�����ļ�ʧ��!", e);
            }
        }
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            try {
                raf.write(data);
                return raf.length();
            } catch (IOException e) {
                e.printStackTrace();
                throw new Exception("д���ļ�ʧ��!", e);
            } finally {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * ��ȡ��Ŀ��class�������·�������� D:/Tomcat7/webapps/EeamsWeb/WEB-INF/classes/
     * @return
     * @throws Exception
     */
    public static String getWebRealPath() {
        String realPath="";
        try{
            //�����Ŀ��class·��   path���ӣ� /D:/Tomcat7/webapps/EeamsWeb/WEB-INF/classes/
            String path = FileUtil.class.getClassLoader().getResource("").getPath();
            path = path.substring(0,path.indexOf("classes") ) ;
            if(path.startsWith("/")) {
                realPath = path.substring(1);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return realPath;
    }

    /**
     * �����ṩ��·������ȡproperties�ļ�
     * @param path properties�ļ���·��
     * @return
     * @throws Exception
     */
    public static Properties readProperties(String path) {
        Properties property = new Properties();
        File file = new File(path);
        InputStream is = null;
        try {
            is = new  FileInputStream(file);
            property.load(is);

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(is!= null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return property;
    }


}

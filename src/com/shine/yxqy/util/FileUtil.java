package com.shine.yxqy.util;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;

/**
 * 文件处理工具类
 */
public class FileUtil {
    private static final Logger log = Logger.getLogger(FileUtil.class);

    /**
     * 判断文件目录是否存在，如果目录不存在就创建目录
     */
    public static void createFileDir(String filePath) {
        String dirPath = getFileDirByPath(filePath);
        File file = new File(dirPath);
        if (file.exists() == false)
            file.mkdirs();
    }

    /**
     * 根据文件路径获得文件的所在目录
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
     * 检查目录是否存在，若不存在则新建
     *
     * @param fileDir 文件目录路径
     * @return 是否存在
     */
    public static boolean insureDirExist(String fileDir) {
        File file = new File(fileDir);
        return file.exists() || file.mkdirs() || file.exists();
    }

    /**
     * 从流中取字节
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
            log.info("从文件流中读取到文件长度[" + baos.size() + "]");
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("读取文件流失败,已读取[" + exceptionLength + "]");
        }
        return null;
    }

    /**
     * 将文件流写入到指定的文件中
     *
     * @param fileDir  文件目录
     * @param fileName 文件名
     * @param in       文件流
     */
    public static long write(String fileDir, String fileName, InputStream in) throws Exception {
        byte[] data = inputStreamToBytes(in);
        return write(fileDir, fileName, data);
    }

    /**
     * 将文件流写入到指定的文件中
     *
     * @param fileDir  文件目录
     * @param fileName 文件名
     * @param data     文件流
     */
    public static long write(String fileDir, String fileName, byte[] data) throws Exception {
        if (!insureDirExist(fileDir)) {
            throw new Exception("创建文件存储目录失败!");
        }
        File file = new File(fileDir + fileName);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    throw new Exception("创建文件失败!");
                }
            } catch (IOException e) {
                throw new Exception("创建文件失败!", e);
            }
        }
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            try {
                raf.write(data);
                return raf.length();
            } catch (IOException e) {
                e.printStackTrace();
                throw new Exception("写入文件失败!", e);
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
     * 获取项目的class级别绝对路径：例如 D:/Tomcat7/webapps/EeamsWeb/WEB-INF/classes/
     * @return
     * @throws Exception
     */
    public static String getWebRealPath() {
        String realPath="";
        try{
            //获得项目的class路径   path例子： /D:/Tomcat7/webapps/EeamsWeb/WEB-INF/classes/
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
     * 根据提供的路径，读取properties文件
     * @param path properties文件的路径
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

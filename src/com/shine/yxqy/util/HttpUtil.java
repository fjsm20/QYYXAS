package com.shine.yxqy.util;

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * http请求工具类
 */
public class HttpUtil {
    private static Logger log = Logger.getLogger(HttpUtil.class);
    /**
     * HTTP请求远程数据
     * @param requestUrl 请求地址
     * @param count 当前请求的次数
     * @return 令牌
     */
    public static String remoteRequest(String requestUrl,String param) throws Exception {
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Content-Type", "multipart/octet-stream");
            connection.connect();
            OutputStream out = connection.getOutputStream();
            out.write(param.getBytes("UTF-8"));
            out.flush();
            out.close();
            StringBuilder sb = new StringBuilder();
            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                String line= "";
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                reader.close();

                connection.disconnect();
            } else {
                connection.disconnect();
                log.debug("URL请求:[" + requestUrl + "]失败");
                throw new Exception("URL请求:[" + requestUrl + "]失败");
            }
            if (sb.length() > 0) {
                return sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.debug("URL请求:[" + requestUrl + "]失败");
            throw new Exception("URL请求:[" + requestUrl + "]失败");
        }
        return null;
    }

    /**
     * 下载文件
     * @param urlStr
     * @param jsonString
     * @param savePath
     * @throws ShineException
     */
    public static void requestFile(String urlStr,String jsonString,String savePath) throws Exception {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setRequestProperty("contentType", "UTF-8");
            connection.setRequestProperty("Content-Type", "application/json;");
            connection.connect();
            OutputStream out = connection.getOutputStream();

            try {
                Object obj = JSONObject.fromObject(jsonString);
            } catch (Exception e) {
                throw new Exception("json格式错误!");
            }

            out.write(jsonString.getBytes("UTF-8"));
            out.flush();
            out.close();


            //获取所下载文件的InputStream对象
            InputStream inputStream=connection.getInputStream();
            //创建保存文件对象
            File saveFile = new File(savePath);
            FileUtil.createFileDir(saveFile.getAbsolutePath());
            //获取一个写入文件流对象
            OutputStream output =new FileOutputStream(saveFile);
            //循环读取下载的文件到buffer对象数组中
            byte buffer[] = new byte[4*1024];
            int len = 0;
            //使用一个输入流从buffer里把数据读取出来
            while( (len=inputStream.read(buffer)) != -1 ){
                //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                output.write(buffer, 0, len);
            }
            output.close();
            inputStream.close();
            // 断开连接
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("调用方法失败:"+ e.getMessage());
        }
    }

}

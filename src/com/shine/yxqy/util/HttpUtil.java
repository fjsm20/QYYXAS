package com.shine.yxqy.util;

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * http���󹤾���
 */
public class HttpUtil {
    private static Logger log = Logger.getLogger(HttpUtil.class);
    /**
     * HTTP����Զ������
     * @param requestUrl �����ַ
     * @param count ��ǰ����Ĵ���
     * @return ����
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
                log.debug("URL����:[" + requestUrl + "]ʧ��");
                throw new Exception("URL����:[" + requestUrl + "]ʧ��");
            }
            if (sb.length() > 0) {
                return sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.debug("URL����:[" + requestUrl + "]ʧ��");
            throw new Exception("URL����:[" + requestUrl + "]ʧ��");
        }
        return null;
    }

    /**
     * �����ļ�
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
                throw new Exception("json��ʽ����!");
            }

            out.write(jsonString.getBytes("UTF-8"));
            out.flush();
            out.close();


            //��ȡ�������ļ���InputStream����
            InputStream inputStream=connection.getInputStream();
            //���������ļ�����
            File saveFile = new File(savePath);
            FileUtil.createFileDir(saveFile.getAbsolutePath());
            //��ȡһ��д���ļ�������
            OutputStream output =new FileOutputStream(saveFile);
            //ѭ����ȡ���ص��ļ���buffer����������
            byte buffer[] = new byte[4*1024];
            int len = 0;
            //ʹ��һ����������buffer������ݶ�ȡ����
            while( (len=inputStream.read(buffer)) != -1 ){
                //���������buffer��д�����ݣ��м����������ĸ�λ�ÿ�ʼ����len�����ȡ�ĳ���
                output.write(buffer, 0, len);
            }
            output.close();
            inputStream.close();
            // �Ͽ�����
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("���÷���ʧ��:"+ e.getMessage());
        }
    }

}

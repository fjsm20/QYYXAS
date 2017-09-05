package com.shine.yxqy.util;

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * http���󹤾���
 */
public class HttpUtil {
    private static Logger log = Logger.getLogger(HttpUtil.class);
    /**
     * HTTP����Զ������
     * @param requestUrl �����ַ
     * @param param �������
     * @return ������
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
     * HTTP����Զ�����ݣ������ļ�����
     * @param requestUrl �����ַ
     * @param headParam ��ǰ����ͷ����
     * @param param ��ǰ����Ĵ���
     * @return ����
     */
    public static String remoteRequest(String requestUrl, Map<String,String> headParam, InputStream is) throws Exception {
        HttpURLConnection connection = null;
        StringBuffer sb = new StringBuffer();
        try {
            URL url = new URL(requestUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Content-Type", "multipart/octet-stream");

            if(headParam!=null && headParam.size()>=1){
                Iterator it= headParam.entrySet().iterator();
                while(it.hasNext()){
                    Map.Entry entry = (Map.Entry)it.next();
                    String key=String.valueOf(entry.getKey());
                    String value= String.valueOf(entry.getValue());
                    connection.setRequestProperty(key,value);
                }
            }

            OutputStream out = connection.getOutputStream();
            // �ϴ��ļ�
            DataInputStream fin = new DataInputStream(is);
            int bytes = 0;
            byte[] bufferOut = new byte[1024 * 8];
            while ((bytes = fin.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            out.flush();
            out.close();
            fin.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));

            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
//            System.out.println(sb);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("�ļ��ϴ�ʧ��");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return sb.toString();
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
            File saveFile = new File(savePath);
            FileUtil.createFileDir(saveFile.getAbsolutePath());
            OutputStream output =new FileOutputStream(saveFile);
            byte buffer[] = new byte[4*1024];
            int len = 0;
            while( (len=inputStream.read(buffer)) != -1 ){
                output.write(buffer, 0, len);
            }
            output.close();
            inputStream.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("���÷���ʧ��:"+ e.getMessage());
        }
    }

}

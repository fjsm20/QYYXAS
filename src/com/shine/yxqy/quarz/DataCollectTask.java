package com.shine.yxqy.quarz;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

import com.shine.yxqy.thread.TaskThreadPool;
import com.shine.yxqy.util.ConfigUtil;
import com.shine.yxqy.util.Constant;
import com.shine.yxqy.util.FtpUtil;
import com.shine.yxqy.util.HttpUtil;

public class DataCollectTask {
	public static Logger log = Logger.getLogger(DataCollectTask.class);


	public void requestLifeCtrl() {
		String wt_ip=ConfigUtil.getProperty(Constant.WT_IP);
		String operateTime = ConfigUtil.getParamProperty(Constant.OPERATE_TIME);
		String sourceType = ConfigUtil.getParamProperty(Constant.SOURCE_TYPE);
		String dateType = "0"; //0:����  1��ҵ�����

		try{

			/**
			 * ���������������������������һ��source
			 */
			String wt_url= wt_ip+Constant.REQ_GET_FILE_URL+"&Operate_time="+operateTime+"&dataType="+dateType + "&source=" + sourceType;
			log.info("����[��������]����������������ʱ�� Operate_time="+operateTime+",dataType="+dateType+"");
			String redata = HttpUtil.remoteRequest(wt_url,"");
			log.info("����[��������]����������Ӧ�����"+redata);
			//{"state": 0}
			Map<String, Object> map = new HashMap<String, Object>();
			map = (Map<String, Object>) JSONObject.fromObject(redata);
			if(String.valueOf(map.get("state")).equals("1")){
				log.info("����[��������]�������������ͳɹ�������ʱ�� Operate_time="+operateTime+",dataType="+dateType+"");
			}else{
				log.info("����[��������]��������������ʧ�ܣ�����ʱ�� Operate_time="+operateTime+",dataType="+dateType+"");
			}


			/**
			 * ҵ�������������
			 */
			dateType="1";
			wt_url= wt_ip+Constant.REQ_GET_FILE_URL+"&Operate_time="+operateTime+"&dataType="+dateType + "&source=" + sourceType;
			log.info("����[ҵ�����]����������������ʱ�� Operate_time="+operateTime+",dataType="+dateType+"");
			redata = HttpUtil.remoteRequest(wt_url,null);
			log.info("����[ҵ�����]����������Ӧ�����"+redata);
			Map<String, Object> blmap = new HashMap<String, Object>();
			blmap = (Map<String, Object>) JSONObject.fromObject(redata);
			if(String.valueOf(blmap.get("state")).equals("1")){
				log.info("����[ҵ�����]�������������ͳɹ�������ʱ�� Operate_time="+operateTime+",dataType="+dateType+"");
			}else{
				log.info("����[ҵ�����]��������������ʧ�ܣ�����ʱ�� Operate_time="+operateTime+",dataType="+dateType+"");
			}


		} catch (Exception e) {
			e.printStackTrace();
			log.info("������������������ʧ�ܣ�����ʱ�� Operate_time="+operateTime+",dataType="+dateType+""+e.getMessage());
		}
	}

	/**
	 * ��ʱȥ�񡾿�������ok.txt�ļ������ļ��Ƿ�ȫ��������
	 */
	public void getKHFile() {
		try {
			log.info("ִ�ж�ȡok.txt��������");

			final String ftpUrl = ConfigUtil.getProperty(Constant.FTP_URL);
			final String okFileName = ConfigUtil.getProperty(Constant.OK_TXT);
			final String localTempPath = ConfigUtil.getProperty(Constant.LOCAL_TMP_PATH);

			String operateTime = ConfigUtil.getParamProperty(Constant.OPERATE_TIME);
			String dayOfMonth = ConfigUtil.getParamProperty(Constant.DAY_OF_MONTH); //��������
			String dataType = "kh";
			//�˴η���ִ��Ҫ���ҵ�Ŀ���ļ� ok.txt

			//ftp·��������20170828/12
			StringBuilder ftpFilePath = new StringBuilder();
			ftpFilePath.append(operateTime).append("/").append(dayOfMonth);

			FTPFile[] ftpFiles = FtpUtil.listFiles(ftpUrl, ftpFilePath.toString());

			if (ftpFiles == null || ftpFiles.length <= 0) {
				log.info(ftpFilePath + "Ŀ¼��û���ļ����ļ��У��޷��ҵ�ok.txt�ļ�");
				return;
			}

			/**
			 * ������ڴ���������Ŀ¼�㼶������ѭ����������,�Է��������������࣬������ε��������򣺴�0�ļ��п�ʼ��ÿ���ļ����а���5W������
			 * ,�������11W���ݣ�������0,1,2�ļ��У�����Ҫѭ�������ļ��в鿴�Ƿ���ok.txt
			 */
			for (FTPFile ftpFile : ftpFiles) {
				//�鿴�Ƿ���ok.txt
				String relaPath = new String(ftpFilePath.toString().getBytes("gbk"), "iso-8859-1")+"/"+ftpFile.getName()+"/"+dataType;

				FtpUtil.getFile(ftpUrl, relaPath,okFileName,new FtpUtil.FtpCallback() {
					@Override
					public void postSend() {
						//                    logger.debug("[FTP]���ڷ��������ļ�����");
					}

					@Override
					public void onReceive(InputStream inStream, String rPath) throws IOException {
						if(inStream ==null || (inStream!=null && inStream.available()<=0)){
							throw new IOException("FTP��ȡ���ļ���Ϊ��");
						}
						if(inStream.available()>=1){
							//���ļ�������Խ����ļ�������
							try {
								//System.out.println(rPath);
								TaskThreadPool.addDownloadTask(ftpUrl, rPath, localTempPath);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					}

					@Override
					public void onException(Exception e) {
						e.printStackTrace();
					}
				});


			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}


	/**
	 * ��ʱȥ��ҵ�������ok.txt�ļ������ļ��Ƿ�ȫ��������
	 */
	public void getYWBLFile() {
		try {
			log.info("ִ�ж�ȡok.txt��������");
			final String ftpUrl = ConfigUtil.getProperty(Constant.FTP_URL);
			final String okFileName = ConfigUtil.getProperty(Constant.OK_TXT);
			final String local_tmp_path = ConfigUtil.getProperty(Constant.LOCAL_TMP_PATH);

			String operateTime = ConfigUtil.getParamProperty(Constant.OPERATE_TIME);
			String dayOfMonth = ConfigUtil.getParamProperty(Constant.DAY_OF_MONTH); //��������
			//�˴η���ִ��Ҫ���ҵ�Ŀ���ļ� ok.txt

			//ftp·��������20170828/12
			StringBuilder ftpFilePath = new StringBuilder();
			ftpFilePath.append(operateTime).append("/").append(dayOfMonth);


			FTPFile[] ftpFiles = FtpUtil.listFiles(ftpUrl, ftpFilePath.toString());

			if (ftpFiles == null || ftpFiles.length <= 0) {//20170628\24\
				log.info(ftpFilePath + "Ŀ¼��û���ļ����ļ��У��޷��ҵ�ok.txt�ļ�");
				return;
			}

			/**
			 * ������ڴ���������Ŀ¼�㼶������ѭ����������,�Է��������������࣬������ε��������򣺴�0�ļ��п�ʼ��ÿ���ļ����а���5W������
			 * ,�������11W���ݣ�������0,1,2�ļ��У�����Ҫѭ�������ļ��в鿴�Ƿ���ok.txt
			 * 20170628/24/0/ywbl/
			 */
			for (FTPFile ftpFile : ftpFiles) {
				//�鿴�Ƿ���ok.txt
				String relaPath = new String(ftpFilePath.toString().getBytes("gbk"), "iso-8859-1") + "/" + ftpFile.getName() + "/ywbl";
				FtpUtil.getFile(ftpUrl, relaPath, okFileName, new FtpUtil.FtpCallback() {
					@Override
					public void postSend() {
					}

					@Override
					public void onReceive(InputStream inStream,String rPath) throws IOException {
						if (inStream == null || (inStream != null && inStream.available() <= 0)) {
							throw new IOException("FTP��ȡ���ļ���Ϊ��");
						}
						if (inStream.available() >= 1) {
							//���ļ�������Խ����ļ�������
							try {
								TaskThreadPool.addDownloadTask(ftpUrl, rPath, local_tmp_path);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					}

					@Override
					public void onException(Exception e) {
						e.printStackTrace();
					}
				});

			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
//			FtpUtil.ftpLogout();
		}
	}

	public void test(){
		String ftpUrl = ConfigUtil.getProperty(Constant.FTP_URL);
		String relaPath = "20150416";
		String localpath = ConfigUtil.getProperty(Constant.LOCAL_TMP_PATH);
//
//		try {
//			TaskThreadPool.addDownloadTask(ftpUrl,relaPath,localpath);
//			System.out.println("addDownloadTask....");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		getYWBLFile();
		getKHFile();
//		List<UserDocument> listUD = new ArrayList<UserDocument>();
//		XMLService xmlService = new GJXMLServiceImpl();
//		String custxmlPath = "F:\\shine\\��Ŀ����\\EEAMS\\����֤ȯ\\Ǩ������\\����\\20170625\\06/0/ywbl/"+Constant.CUST_XML;
//		xmlService.iterateAnalysisXML(custxmlPath,listUD);
//		System.out.println("xml��������"+listUD.size());
	}

	public void changeReqParam() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		String dayOfMonth = sdf.format(new Date());

		ConfigUtil.putParamProperties(Constant.DAY_OF_MONTH, dayOfMonth);
	}
}

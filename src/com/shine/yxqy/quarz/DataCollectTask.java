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
		String dateType = "0"; //0:开户  1：业务办理

		try{

			/**
			 * 网厅开户增量请求，请求参数多了一个source
			 */
			String wt_url= wt_ip+Constant.REQ_GET_FILE_URL+"&Operate_time="+operateTime+"&dataType="+dateType + "&source=" + sourceType;
			log.info("发送[网厅开户]增量数据请求：请求时间 Operate_time="+operateTime+",dataType="+dateType+"");
			String redata = HttpUtil.remoteRequest(wt_url,"");
			log.info("请求[网厅开户]增量数据响应结果："+redata);
			//{"state": 0}
			Map<String, Object> map = new HashMap<String, Object>();
			map = (Map<String, Object>) JSONObject.fromObject(redata);
			if(String.valueOf(map.get("state")).equals("1")){
				log.info("请求[网厅开户]增量数据请求发送成功：请求时间 Operate_time="+operateTime+",dataType="+dateType+"");
			}else{
				log.info("请求[网厅开户]增量数据请求发送失败：请求时间 Operate_time="+operateTime+",dataType="+dateType+"");
			}


			/**
			 * 业务办理增量请求
			 */
			dateType="1";
			wt_url= wt_ip+Constant.REQ_GET_FILE_URL+"&Operate_time="+operateTime+"&dataType="+dateType + "&source=" + sourceType;
			log.info("发送[业务办理]增量数据请求：请求时间 Operate_time="+operateTime+",dataType="+dateType+"");
			redata = HttpUtil.remoteRequest(wt_url,null);
			log.info("请求[业务办理]增量数据响应结果："+redata);
			Map<String, Object> blmap = new HashMap<String, Object>();
			blmap = (Map<String, Object>) JSONObject.fromObject(redata);
			if(String.valueOf(blmap.get("state")).equals("1")){
				log.info("请求[业务办理]增量数据请求发送成功：请求时间 Operate_time="+operateTime+",dataType="+dateType+"");
			}else{
				log.info("请求[业务办理]增量数据请求发送失败：请求时间 Operate_time="+operateTime+",dataType="+dateType+"");
			}


		} catch (Exception e) {
			e.printStackTrace();
			log.info("请求增量数据请求发送失败：请求时间 Operate_time="+operateTime+",dataType="+dateType+""+e.getMessage());
		}
	}

	/**
	 * 定时去获【开户】的ok.txt文件，查文件是否全部被导出
	 */
	public void getKHFile() {
		try {
			log.info("执行读取ok.txt调度任务");

			final String ftpUrl = ConfigUtil.getProperty(Constant.FTP_URL);
			final String okFileName = ConfigUtil.getProperty(Constant.OK_TXT);
			final String localTempPath = ConfigUtil.getProperty(Constant.LOCAL_TMP_PATH);

			String operateTime = ConfigUtil.getParamProperty(Constant.OPERATE_TIME);
			String dayOfMonth = ConfigUtil.getParamProperty(Constant.DAY_OF_MONTH); //请求日期
			String dataType = "kh";
			//此次方法执行要查找的目的文件 ok.txt

			//ftp路径，例如20170828/12
			StringBuilder ftpFilePath = new StringBuilder();
			ftpFilePath.append(operateTime).append("/").append(dayOfMonth);

			FTPFile[] ftpFiles = FtpUtil.listFiles(ftpUrl, ftpFilePath.toString());

			if (ftpFiles == null || ftpFiles.length <= 0) {
				log.info(ftpFilePath + "目录下没有文件或文件夹，无法找到ok.txt文件");
				return;
			}

			/**
			 * 如果存在大数据量，目录层级递增，循环处理此情况,对方可能因数量过多，会分批次导出，规则：从0文件夹开始，每个文件夹中包含5W的数据
			 * ,例如存在11W数据，则将生成0,1,2文件夹，故需要循环遍历文件夹查看是否都有ok.txt
			 */
			for (FTPFile ftpFile : ftpFiles) {
				//查看是否有ok.txt
				String relaPath = new String(ftpFilePath.toString().getBytes("gbk"), "iso-8859-1")+"/"+ftpFile.getName()+"/"+dataType;

				FtpUtil.getFile(ftpUrl, relaPath,okFileName,new FtpUtil.FtpCallback() {
					@Override
					public void postSend() {
						//                    logger.debug("[FTP]正在发起下载文件请求");
					}

					@Override
					public void onReceive(InputStream inStream, String rPath) throws IOException {
						if(inStream ==null || (inStream!=null && inStream.available()<=0)){
							throw new IOException("FTP获取的文件流为空");
						}
						if(inStream.available()>=1){
							//有文件，则可以进行文件夹下载
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
	 * 定时去获【业务办理】的ok.txt文件，查文件是否全部被导出
	 */
	public void getYWBLFile() {
		try {
			log.info("执行读取ok.txt调度任务");
			final String ftpUrl = ConfigUtil.getProperty(Constant.FTP_URL);
			final String okFileName = ConfigUtil.getProperty(Constant.OK_TXT);
			final String local_tmp_path = ConfigUtil.getProperty(Constant.LOCAL_TMP_PATH);

			String operateTime = ConfigUtil.getParamProperty(Constant.OPERATE_TIME);
			String dayOfMonth = ConfigUtil.getParamProperty(Constant.DAY_OF_MONTH); //请求日期
			//此次方法执行要查找的目的文件 ok.txt

			//ftp路径，例如20170828/12
			StringBuilder ftpFilePath = new StringBuilder();
			ftpFilePath.append(operateTime).append("/").append(dayOfMonth);


			FTPFile[] ftpFiles = FtpUtil.listFiles(ftpUrl, ftpFilePath.toString());

			if (ftpFiles == null || ftpFiles.length <= 0) {//20170628\24\
				log.info(ftpFilePath + "目录下没有文件或文件夹，无法找到ok.txt文件");
				return;
			}

			/**
			 * 如果存在大数据量，目录层级递增，循环处理此情况,对方可能因数量过多，会分批次导出，规则：从0文件夹开始，每个文件夹中包含5W的数据
			 * ,例如存在11W数据，则将生成0,1,2文件夹，故需要循环遍历文件夹查看是否都有ok.txt
			 * 20170628/24/0/ywbl/
			 */
			for (FTPFile ftpFile : ftpFiles) {
				//查看是否有ok.txt
				String relaPath = new String(ftpFilePath.toString().getBytes("gbk"), "iso-8859-1") + "/" + ftpFile.getName() + "/ywbl";
				FtpUtil.getFile(ftpUrl, relaPath, okFileName, new FtpUtil.FtpCallback() {
					@Override
					public void postSend() {
					}

					@Override
					public void onReceive(InputStream inStream,String rPath) throws IOException {
						if (inStream == null || (inStream != null && inStream.available() <= 0)) {
							throw new IOException("FTP获取的文件流为空");
						}
						if (inStream.available() >= 1) {
							//有文件，则可以进行文件夹下载
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
//		String custxmlPath = "F:\\shine\\项目资料\\EEAMS\\国君证券\\迁移资料\\网厅\\20170625\\06/0/ywbl/"+Constant.CUST_XML;
//		xmlService.iterateAnalysisXML(custxmlPath,listUD);
//		System.out.println("xml解析结束"+listUD.size());
	}

	public void changeReqParam() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		String dayOfMonth = sdf.format(new Date());

		ConfigUtil.putParamProperties(Constant.DAY_OF_MONTH, dayOfMonth);
	}
}

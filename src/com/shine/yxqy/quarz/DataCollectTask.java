package com.shine.yxqy.quarz;

import com.shine.yxqy.po.UserDocument;
import com.shine.yxqy.thread.TaskThreadPool;
import com.shine.yxqy.util.Constant;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DataCollectTask {
    public static Logger log = Logger.getLogger(DataCollectTask.class);


    public void requestLifeCtrl() {
        log.info("执行调度任务");
        System.out.println("执行调度任务："+new Date());
        String token = null;
        try {
//            token = ServiceUtil.getRemoteBusToken();
            System.out.println("token="+token);

//            /****上传文件***/
//            File file = new File("D:\\test\\aaf.pdf");
////
//            String data = ServiceUtil.doUploadFile(file.getName(),"2017001",file);
//            System.out.println("执行结果："+data);

            Map<String,String> jsonObject = new HashMap<String,String>();
            jsonObject.put("method_id","020123");
            jsonObject.put("src_order_no","XY0012229");
            jsonObject.put("busi_code","2100500000");
            jsonObject.put("cust_prop","0");
            jsonObject.put("acct_code","10000000911");
            jsonObject.put("cert_type","0");
            jsonObject.put("cert_code","1122331");
            jsonObject.put("cust_name","张三");
            jsonObject.put("opr_date","20170826");
            jsonObject.put("dep_code","3106");
            jsonObject.put("file_num","1");
            jsonObject.put("file_check","1");
            jsonObject.put("source_nos","70,2945|429,2947");
            jsonObject.put("user_code", Constant.APP_ID);
            jsonObject.put("app_id","JZYY");
            jsonObject.put("action_type","data_inte");

//            ServiceUtil.submitProcScanInfo(null);

            for(int  i=0; i<=10;i++){
                UserDocument ud = new UserDocument();
                ud.setCertName(i+"one");

//                TaskThreadPool.addTask(ud);
            }

            System.out.println("one--end");
            for(int  i=0; i<=10;i++){
                UserDocument ud = new UserDocument();
                ud.setCertName(i+",two");

//                TaskThreadPool.addTask(ud);
            }

            System.out.println("two--end");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readingFile() {
		log.info("执行读取ok.txt调度任务");

		String ftpUrl = ConfigUtil.getProperty("ftp_url");
		String operateTime = ConfigUtil.getProperty("operate_time");
		String dateType = ConfigUtil.getProperty("date_type");
		String dayOfMonth = ConfigUtil.getProperty("day_of_month");
		dateType = "0".equals(dateType) ? "kh" : "ywbl";
		//此次方法执行要查找的目的文件 ok.txt
		final String targetFilename = ConfigUtil.getProperty("ok_txt");

		//ftp路径，例如20170828/12
		StringBuilder ftpFilePath = new StringBuilder(20);
		ftpFilePath.append(operateTime).append("/");
		ftpFilePath.append(dayOfMonth).append("/");

		FTPFile[] ftpFiles = FtpUtil.listFiles(ftpUrl, ftpFilePath.toString());

		if (ftpFiles == null || ftpFiles.length <= 0) {
			log.info(ftpFilePath + "目录下没有文件或文件夹，无法找到ok.txt文件");
			return;
		}
		
		//如果存在大数据量，目录层级递增，循环处理此情况
		for (FTPFile ftpFile : ftpFiles) {

			//okFilePath：要下载的ok.txt文件的路径
			StringBuilder okFilePath = new StringBuilder(ftpFilePath);
			okFilePath.append(ftpFile.getName()).append("/");
			okFilePath.append(dateType).append("/");

			//savePath：下载的保存路径
			final String savePath = ConfigUtil.getProperty("root_path") + okFilePath.toString();
			
			okFilePath.append(targetFilename);
			
			try {
				//下载ok.txt
				FtpUtil.getFile(ftpUrl, okFilePath.toString(), new FtpUtil.FtpCallback() {
					public void onReceive(InputStream inStream) throws IOException {
						if (inStream == null || (inStream != null && inStream.available() <= 0)) {
							throw new IOException("FTP获取的文件流为空");
						}
						try {
							FileUtil.write(savePath, targetFilename, inStream);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					public void onException(Exception e) {
						e.printStackTrace();
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
				log.error("文件下载失败：" + okFilePath.toString());
			}

			File targetFile = new File(savePath, targetFilename);
			if (!targetFile.exists()) {
				log.info("此文件不存在：" + targetFile.getPath());
				return;
			}

			long fileSize = targetFile.length();
			log.info("此文件大小：" + fileSize + "，文件路径：" + targetFile.getPath());

			if (fileSize < 20) {
				log.info("此文件存在缺损：" + targetFile.getPath());
				return;
			}

			log.info(targetFile.getPath() + "文件存在，并且文件大小符合要求。");
			
		}
	}
    
    protected void execute()  {
        long ms = System.currentTimeMillis();
        System.out.println("\t\t" + new Date(ms));
    }	
	


}

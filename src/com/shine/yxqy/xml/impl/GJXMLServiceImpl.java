package com.shine.yxqy.xml.impl;

import com.shine.yxqy.po.UserDocument;
import com.shine.yxqy.xml.XMLService;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 国泰君安XML文件解析
 *
 */
public class GJXMLServiceImpl extends XMLService{
    private static Logger log = Logger.getLogger(GJXMLServiceImpl.class);
    
    private String partOfRelaPath;
	private static String ROOT_PATH = ConfigUtil.getProperty("root_path");

    public void iterateAnalysisXML(String filename, List<UserDocument> listUD) {
		log.info("进行XML解析[filename=" + filename + "]");
		long beginTime = System.currentTimeMillis();

		//截取一部分的相对路径
		this.setPartOfRelaPath(filename);
		
		SAXReader saxReader = new SAXReader();
		try {
			Document document = saxReader.read(new File(filename));
			Element root = document.getRootElement();
			for (Iterator iter = root.elementIterator(); iter.hasNext();) {
				Element element = (Element) iter.next();
				UserDocument ud = new UserDocument();
				List<YXFile> xyFileList = new ArrayList<YXFile>();
				ud.setFileList(xyFileList);

				// 遍历Khxx结点的所有子节点（ID，USER_NAME...），并进行处理
				for (Iterator iterInner = element.elementIterator(); iterInner.hasNext();) {
					Element elementInner = (Element) iterInner.next();
					if (elementInner.getName().equals("USER_NAME")) {
						ud.setCustName(elementInner.getText());
					}
					if (elementInner.getName().equals("CARD_NUM")) {
						ud.setCertCode(elementInner.getText());
					}

					if ("CUST_ID".equals(elementInner.getName())) {
						ud.setCustId(elementInner.getText());
					}

					if ("CZY".equals(elementInner.getName())) {
						ud.setUserCode(elementInner.getText());
					}
					// 各种类型的文件ID
					if (!"CUST_ID".equals(elementInner.getName()) && elementInner.getName().contains("_ID")) {
						this.tagVal2Id(ud, elementInner);
					}

					if (elementInner.getName().contains("YXZL")) {
						this.setFileDetail(ud, elementInner);
					}
				}
				//设置用户文件对象的busi_code
				ud.setBusiCode(ConfigUtil.getProperty("busi_code"));
				listUD.add(ud);
			}

		} catch (DocumentException e) {
			log.info("XML解析失败[filename=" + filename + "] ," + e.getMessage());
		} finally {
			long endTime = System.currentTimeMillis();
			log.info("XML解析结束，总共耗时：" + (endTime - beginTime));
		}
	}

	/**
	 * 读取文件ID标签，并将文件信息放入对应UserDocument对象的字段fileList中
	 * @param ud UserDocument，每一个khxx标签的对象类
	 * @param ele dom4j标签元素，khxx标签下的子标签
	 */
	private void tagVal2Id(UserDocument ud, Element ele) {
		if (StringUtils.isEmpty(ele.getText())) {
			return;
		}

		YXFile xf = new YXFile();

		// 设置文件ID和文件类型
		xf.setId(ele.getText());
		xf.setFileType(ele.getName().substring(0, ele.getName().lastIndexOf("_ID")));
		ud.getFileList().add(xf);
	}

	/**
	 * 根据YXZL或YXZL_SP标签，设置详细的YXFile对象信息
	 * @param userDoc 用户对象
	 * @param element xml文件中YXZL或YXZL_SP标签元素
	 */
	private void setFileDetail(UserDocument userDoc, Element element) {
		//迭代器获取YXZL或YXZL_SP标签的子标签元素
		Iterator<?> eleIt = element.elementIterator();
		List<YXFile> fileList = userDoc.getFileList();
		YXFile yf = new YXFile();
		//设置根路径
		yf.setRootPath(ROOT_PATH);

		StringBuilder relaPathSb = new StringBuilder(30);
		while (eleIt.hasNext()) {
			Element ele = (Element) eleIt.next();

			//获取ID值
			if ("ID".equals(ele.getName())) {
				yf.setId(ele.getText());
			}
			//获取文件名称
			if ("FILE_NAME".equals(ele.getName())) {
				yf.setFileName(ele.getText());
				//设置相对路径
				relaPathSb.append(this.partOfRelaPath);
				relaPathSb.append(userDoc.getCustId()).append(File.separator).append(ele.getText());
				yf.setRelaPath(relaPathSb.toString());
			}
		}

		File f = new File(yf.getRootPath() + yf.getRelaPath());
		yf.setFileSize(String.valueOf(f.length()));
		/*重写过YXFile类的equals方法，contains()方法以FileId为判断依据，
		 *如果fileList中已经存在于yf对象相同的fileId的对象，则返回contains()方法返回true
		 */
		if (fileList.contains(yf) && StringUtils.isNotEmpty(yf.getFileName())) {
			//设置fileName
			yf.setFileType(fileList.get(fileList.indexOf(yf)).getFileType());
			fileList.set(fileList.indexOf(yf), yf);
			
		} else if ("YXZL_SP".equals(element.getName())) {
			//YXZL_SP标签下信息数据直接添加到fileList中
			yf.setFileType("VIDEO");
			fileList.add(yf);
		}
	}

	public void setPartOfRelaPath(String filename) {
		String tmp = filename;
		int beginIndex = 0;
		if (tmp.indexOf(ROOT_PATH) > -1) {
			beginIndex = ROOT_PATH.length();
		}
		int endIndex = tmp.lastIndexOf("cust.xml");
		
		tmp = tmp.substring(beginIndex, endIndex);
		if (StringUtils.isNotEmpty(tmp)) {
			this.partOfRelaPath = tmp;
		} else {
			this.partOfRelaPath = "";
		}
	}





    public static void main(String args[]){
        XMLService service = new GJXMLServiceImpl();
        List<UserDocument> listUD = new ArrayList<UserDocument>();
        service.iterateAnalysisXML("D:\\test\\cust.xml",listUD);
        for(UserDocument ud: listUD) {
            System.out.println("------------");
            System.out.println(ud.getCertName());
            System.out.println(ud.getCertCode());
        }

    }
}

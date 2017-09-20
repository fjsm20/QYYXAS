package com.shine.yxqy.xml.impl;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import com.shine.yxqy.po.FileType;
import com.shine.yxqy.po.UserDocument;
import com.shine.yxqy.po.YXFile;
import com.shine.yxqy.util.ConfigUtil;
import com.shine.yxqy.util.Constant;
import com.shine.yxqy.xml.XMLService;

/**
 * 国泰君安XML文件解析
 *
 */
@Component
public class GJXMLServiceImpl extends XMLService {
	private static Logger log = Logger.getLogger(GJXMLServiceImpl.class);


	@SuppressWarnings("rawtypes")
	@Override
	public void iterateAnalysisXML(String filename, List<UserDocument> listUD) {
		log.info("进行XML解析[filename=" + filename + "]");
		long beginTime = System.currentTimeMillis();
		String relaPath = filename.substring(0, filename.lastIndexOf("/"));
		String relaFilePath = "";
		System.out.println("相对路径是：" + relaPath);
		String busiCode = "";
		String oprDate = ConfigUtil.getParamProperty(Constant.OPERATE_TIME);
		if(relaPath.toLowerCase().contains("/ywbl")){
			busiCode= ConfigUtil.getProperty(Constant.YWBL_BUSI_CODE);
		}else{
			busiCode= ConfigUtil.getProperty(Constant.KH_BUSI_CODE);
		}

		SAXReader saxReader = new SAXReader();
		try {
			Document document = saxReader.read(new File(filename));
			Element root = document.getRootElement();
			for (Iterator iter = root.elementIterator(); iter.hasNext(); ) {
				Element element = (Element) iter.next();
				UserDocument ud = new UserDocument();
				ud.setOprDate(oprDate);

				// 遍历Khxx结点的所有子节点（ID，USER_NAME...），并进行处理
				for (Iterator iterInner = element.elementIterator(); iterInner.hasNext(); ) {
					Element elementInner = (Element) iterInner.next();
					if ("ID".equals(elementInner.getName())) {
						ud.setId(elementInner.getText());
						continue;
					}
					if ("USER_NAME".equals(elementInner.getName())) {
						ud.setCustName(elementInner.getText());
						ud.setCertType("0");//默认设置客户代码为身份证
						continue;
					}
					if ("CARD_NUM".equals(elementInner.getName())) {
						ud.setCertCode(elementInner.getText());
						continue;
					}
					if ("CUST_ID".equals(elementInner.getName())) {
						ud.setCustId(elementInner.getText());
						relaFilePath = relaPath + "/" + elementInner.getText();//设置文件的相对路径
//						System.out.println(relaFilePath);
//						System.out.println(elementInner.getText());
						continue;
					}
					if ("CZY".equals(elementInner.getName())) {
						ud.setUserCode(elementInner.getText());
						continue;
					}
					if ("AFTER_ID".equals(elementInner.getName())) {
						ud.setAfterId(elementInner.getText());
						continue;
					}
					if ("BEFORE_ID".equals(elementInner.getName())) {
						ud.setBeforeId(elementInner.getText());
						continue;
					}
					if ("POLICE_ID".equals(elementInner.getName())) {
						ud.setPoliceId(elementInner.getText());
						continue;
					}
					if ("GROUP_ID".equals(elementInner.getName())) {
						ud.setGroupId(elementInner.getText());
						continue;
					}
					if ("AVATAR_ID".equals(elementInner.getName())) {
						ud.setAvatarId(elementInner.getText());
						continue;
					}
					if ("SIGN_FILE_ID".equals(elementInner.getName())) {
						ud.setSignFileId(elementInner.getText());
						continue;
					}
					if ("BRANCH_NO".equals(elementInner.getName())) {
						ud.setDepCode(elementInner.getTextTrim());
						continue;
					}

					if (elementInner.getName().contains("YXZL")
							|| elementInner.getName().contains("YXZL_SP")
							|| elementInner.getName().contains("XY")) {
						doFileDetail(elementInner, ud, relaFilePath);
					}
				}
				//设置用户文件对象的busi_code
				ud.setBusiCode(busiCode);
				listUD.add(ud);

			}

			System.out.println("结束="+listUD.size());

		} catch (DocumentException e) {
			log.info("XML解析失败[filename=" + filename + "] ," + e.getMessage());
		} finally {
			long endTime = System.currentTimeMillis();
			log.info("XML解析结束，解析有效文件数量："+listUD.size()+" ,总共耗时：" + (endTime - beginTime) + " 毫秒");
		}
	}


	/**
	 * 根据YXZL、YXZL_SP、XY标签，设置详细的YXFile对象信息
	 * @param element 元素
	 * @param userDoc 用户对象
	 * @param relaPath 相对路径
	 */
	@SuppressWarnings("unchecked")
	private void doFileDetail(Element element, UserDocument userDoc, String relaPath) {
//		List<YXFile> existsFileList = userDoc.getFileList();
//		List<YXFile> notExistsFileList = userDoc.getLostFileList();
		List<Element> elist = element.elements();
		int size = elist.size();

		//获取YXZL或YXZL_SP标签的子标签元素
		YXFile yf = new YXFile();
		for(int i=0; i<size; i++){
			Element ele = (Element) elist.get(i);
			//获取ID值
			if ("ID".equals(ele.getName()) 								//因为测试环境中存在，一个<AFTER_ID>或者其他项
					&& (ele.getText().equals(userDoc.getAfterId())		//对应着多个<YXZL>标签
					|| ele.getText().equals(userDoc.getBeforeId())		//也就是说，一个图片类型有多个多个图片文件
					|| ele.getText().equals(userDoc.getAvatarId())		//故而，加上这几个判断条件，予以排除
					|| ele.getText().equals(userDoc.getSignFileId())	//使得，一个图片类型只有一张图片
					|| ele.getText().equals(userDoc.getPoliceId())		//并且取的图片是<AFTER_ID>或者同类型对标签中值对应的那个<YXZL>
			) ) {
				yf.setId(ele.getText());
				continue;
			}
			//获取文件名称
			if ("FILE_NAME".equals(ele.getName())) {
				yf.setFileName(ele.getText());
				yf.setAbsPath((relaPath + "/" + ele.getText()).trim());
				continue;
			}

			//业务ID
			if ("MODULE_ID".equals(ele.getName())) {
				yf.setModuleId(ele.getText());
				continue;
			}

			if ("ADD_DATE".equals(ele.getName())) {
				yf.setAdddate(ele.getText());
				continue;
			}
			if ("MODULE_NAME".equals(ele.getName())) {
				yf.setModuleName(ele.getText());
				//System.out.println(FileType.getFileType(ele.getTextTrim()));
				yf.setSourceNo(FileType.getFileType(ele.getTextTrim()));
				continue;
			}

			//处理协议文件
			if ("File".equals(ele.getName())) {
				yf.setFileName(ele.getText());
				yf.setAbsPath(relaPath + "/" + ele.getText().trim());
				if(relaPath.toLowerCase().contains("/ywbl/")){
					yf.setSourceNo(ConfigUtil.getProperty(Constant.BLXY_ID));
				}else{
					yf.setSourceNo(ConfigUtil.getProperty(Constant.KHXY_ID));
				}
			}
		}

//		System.out.println(yf.getAbsPath());

		if (StringUtils.isNotEmpty(yf.getAbsPath())
				&& new File(yf.getAbsPath()).exists()
				&& (yf.getSourceNo() != null
				&& !yf.getSourceNo().equals("null"))) {
			//System.out.println("**=" + yf.getSourceNo());
			userDoc.getFileList().add(yf);
		} else {
			userDoc.getLostFileList().add(yf);
		}
	}


}

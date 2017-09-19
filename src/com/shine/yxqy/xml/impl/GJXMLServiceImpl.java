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
 * ��̩����XML�ļ�����
 *
 */
@Component
public class GJXMLServiceImpl extends XMLService {
	private static Logger log = Logger.getLogger(GJXMLServiceImpl.class);


	@SuppressWarnings("rawtypes")
	@Override
	public void iterateAnalysisXML(String filename, List<UserDocument> listUD) {
		log.info("����XML����[filename=" + filename + "]");
		long beginTime = System.currentTimeMillis();
		String relaPath = filename.substring(0, filename.lastIndexOf("/"));
		String relaFilePath = "";
		System.out.println("���·���ǣ�" + relaPath);
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

				// ����Khxx���������ӽڵ㣨ID��USER_NAME...���������д���
				for (Iterator iterInner = element.elementIterator(); iterInner.hasNext(); ) {
					Element elementInner = (Element) iterInner.next();
					if ("ID".equals(elementInner.getName())) {
						ud.setId(elementInner.getText());
						continue;
					}
					if ("USER_NAME".equals(elementInner.getName())) {
						ud.setCustName(elementInner.getText());
						ud.setCertType("0");//Ĭ�����ÿͻ�����Ϊ���֤
						continue;
					}
					if ("CARD_NUM".equals(elementInner.getName())) {
						ud.setCertCode(elementInner.getText());
						continue;
					}
					if ("CUST_ID".equals(elementInner.getName())) {
						ud.setCustId(elementInner.getText());
						relaFilePath = relaPath + "/" + elementInner.getText();//�����ļ������·��
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
				//�����û��ļ������busi_code
				ud.setBusiCode(busiCode);
				listUD.add(ud);

			}

			System.out.println("����="+listUD.size());

		} catch (DocumentException e) {
			log.info("XML����ʧ��[filename=" + filename + "] ," + e.getMessage());
		} finally {
			long endTime = System.currentTimeMillis();
			log.info("XML����������������Ч�ļ�������"+listUD.size()+" ,�ܹ���ʱ��" + (endTime - beginTime) + " ����");
		}
	}


	/**
	 * ����YXZL��YXZL_SP��XY��ǩ��������ϸ��YXFile������Ϣ
	 * @param element Ԫ��
	 * @param userDoc �û�����
	 * @param relaPath ���·��
	 */
	@SuppressWarnings("unchecked")
	private void doFileDetail(Element element, UserDocument userDoc, String relaPath) {
//		List<YXFile> existsFileList = userDoc.getFileList();
//		List<YXFile> notExistsFileList = userDoc.getLostFileList();
		List<Element> elist = element.elements();
		int size = elist.size();

		//��ȡYXZL��YXZL_SP��ǩ���ӱ�ǩԪ��
		YXFile yf = new YXFile();
		for(int i=0; i<size; i++){
			Element ele = (Element) elist.get(i);
			//��ȡIDֵ
			if ("ID".equals(ele.getName()) 								//��Ϊ���Ի����д��ڣ�һ��<AFTER_ID>����������
					&& (ele.getText().equals(userDoc.getAfterId())		//��Ӧ�Ŷ��<YXZL>��ǩ
					|| ele.getText().equals(userDoc.getBeforeId())		//Ҳ����˵��һ��ͼƬ�����ж�����ͼƬ�ļ�
					|| ele.getText().equals(userDoc.getAvatarId())		//�ʶ��������⼸���ж������������ų�
					|| ele.getText().equals(userDoc.getSignFileId())	//ʹ�ã�һ��ͼƬ����ֻ��һ��ͼƬ
					|| ele.getText().equals(userDoc.getPoliceId())		//����ȡ��ͼƬ��<AFTER_ID>����ͬ���ͶԱ�ǩ��ֵ��Ӧ���Ǹ�<YXZL>
			) ) {
				yf.setId(ele.getText());
				continue;
			}
			//��ȡ�ļ�����
			if ("FILE_NAME".equals(ele.getName())) {
				yf.setFileName(ele.getText());
				yf.setAbsPath((relaPath + "/" + ele.getText()).trim());
				continue;
			}

			//ҵ��ID
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

			//����Э���ļ�
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

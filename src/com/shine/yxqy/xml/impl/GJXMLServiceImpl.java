package com.shine.yxqy.xml.impl;

import com.shine.yxqy.po.FileType;
import com.shine.yxqy.po.UserDocument;
import com.shine.yxqy.po.YXFile;
import com.shine.yxqy.util.ConfigUtil;
import com.shine.yxqy.util.Constant;
import com.shine.yxqy.xml.XMLService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import javax.sound.midi.Soundbank;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
						relaFilePath = relaPath + "/" + element.getText();//�����ļ������·��
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

					if (elementInner.getName().contains("YXZL") || elementInner.getName().contains("YXZL_SP") ||
							elementInner.getName().contains("XY")) {
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
	private void doFileDetail(Element element,UserDocument userDoc,String relaPath) {
		List<YXFile> existsFileList = userDoc.getFileList();
		List<YXFile> notExistsFileList = userDoc.getLostFileList();
		List<Element> elist = element.elements();
		int size = elist.size();
		//��ȡYXZL��YXZL_SP��ǩ���ӱ�ǩԪ��
		YXFile yf = new YXFile();
		for(int i=0;i<size;i++){
			Element ele = (Element) elist.get(i);
			//��ȡIDֵ
			if ("ID".equals(ele.getName())) {
				yf.setId(ele.getText());
			}
			//��ȡ�ļ�����
			if ("FILE_NAME".equals(ele.getName())) {
				yf.setFileName(ele.getText());
				yf.setAbsPath((relaPath+"/"+userDoc.getCustId()+"/"+ele.getText()).trim());
			}

			//ҵ��ID
			if ("MODULE_ID".equals(ele.getName())) {
				yf.setModuleId(ele.getText());
			}

			if ("ADD_DATE".equals(ele.getName())) {
				yf.setAdddate(ele.getText());
			}
			if ("MODULE_NAME".equals(ele.getName())) {
				yf.setModuleName(ele.getText());
				System.out.println(FileType.getFileType(ele.getTextTrim()));
				yf.setSourceNo(FileType.getFileType(ele.getTextTrim()));
			}

			if ("File".equals(ele.getName())) {
				yf.setFileName(ele.getText());
				yf.setAbsPath((relaPath+"/"+userDoc.getCustId()+"/"+ele.getText().trim()));
				if(relaPath.toLowerCase().contains("/ywbl/")){
					yf.setSourceNo(ConfigUtil.getParamProperty(Constant.BLXY_ID));
				}else{
					yf.setSourceNo(ConfigUtil.getParamProperty(Constant.KHXY_ID));
				}
			}
		}


		if (StringUtils.isNotEmpty(yf.getAbsPath()) && new File(yf.getAbsPath()).exists()
				&& (yf.getSourceNo() != null && !yf.getSourceNo().equals("null"))) {
			System.out.println("**=" + yf.getSourceNo());
			userDoc.getFileList().add(yf);
		} else {
			userDoc.getLostFileList().add(yf);
		}
	}


}

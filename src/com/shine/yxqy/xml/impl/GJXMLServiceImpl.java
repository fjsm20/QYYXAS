package com.shine.yxqy.xml.impl;

import com.shine.yxqy.PO.UserDocument;
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
    public static Logger log = Logger.getLogger(GJXMLServiceImpl.class);

    @Override
    public void iterateAnalysisXML(String filename, List<UserDocument> listUD){
        log.info("进行XML解析[filename="+filename+"]");
        long beginTime = System.currentTimeMillis();

        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(new File(filename));
            Element root = document.getRootElement();
            for (Iterator iter = root.elementIterator(); iter.hasNext(); ) {
                Element element = (Element) iter.next();
                UserDocument ud = new UserDocument();

                //遍历Khxx结点的所有子节点（ID，USER_NAME...），并进行处理
                for (Iterator iterInner = element.elementIterator(); iterInner.hasNext(); ) {
                    Element elementInner = (Element) iterInner.next();
                    if(elementInner.getName().equals("USER_NAME")) {
                        ud.setCertName(elementInner.getText());
                    }
                    if(elementInner.getName().equals("CARD_NUM")) {
                        ud.setCertCode(elementInner.getText());
                    }
                }
                listUD.add(ud);
            }

        } catch (DocumentException e) {
            log.info("XML解析失败[filename="+filename+"] ,"+e.getMessage());
        }finally {
            long endTime = System.currentTimeMillis();
            log.info("XML解析结束，总共耗时："+(endTime-beginTime));
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

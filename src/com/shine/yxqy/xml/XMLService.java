package com.shine.yxqy.xml;

import com.shine.yxqy.po.UserDocument;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * XML解析服务类
 */
public abstract class XMLService {
    public static Logger log = Logger.getLogger(XMLService.class);


    /**
     * XML文件解析
     *
     * @param filename 文件路径
     * @param listUD   存储对象
     */
    public abstract void iterateAnalysisXML(String filename, List<UserDocument> listUD);

}

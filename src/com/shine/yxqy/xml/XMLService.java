package com.shine.yxqy.xml;

import com.shine.yxqy.po.UserDocument;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * XML����������
 */
public abstract class XMLService {
    public static Logger log = Logger.getLogger(XMLService.class);


    /**
     * XML�ļ�����
     *
     * @param filename �ļ�·��
     * @param listUD   �洢����
     */
    public abstract void iterateAnalysisXML(String filename, List<UserDocument> listUD);

}

package com.shine.yxqy.PO;

import java.util.List;

/**
 * �û�����PO��
 */
public class UserDocument {
    private String certName;   //�ͻ�����
    private String certCode;   //֤������
    private String certType;   //֤�����
    private String custId;     //�ͻ���

    private List<YXFile> fileList; //�ļ�����





    public String getCertName() {
        return certName;
    }

    public void setCertName(String certName) {
        this.certName = certName;
    }

    public String getCertCode() {
        return certCode;
    }

    public void setCertCode(String certCode) {
        this.certCode = certCode;
    }

    public String getCertType() {
        return certType;
    }

    public void setCertType(String certType) {
        this.certType = certType;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public List<YXFile> getFileList() {
        return fileList;
    }

    public void setFileList(List<YXFile> fileList) {
        this.fileList = fileList;
    }
}

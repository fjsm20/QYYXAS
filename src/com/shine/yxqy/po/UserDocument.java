package com.shine.yxqy.po;

import java.util.List;

/**
 * �û�����PO��
 */
public class UserDocument {
    private String custName; // �ͻ�����
    private String certCode; // ֤������
    private String certType; // ֤�����
    private String custId; // �ͻ���

    private String busiCode; //ҵ�����
    private String custProp; //�ͻ�����
    private String acctCode; //�˺�
    private String oprDate; //ҵ��ʱ��
    private String depCode; //ҵ���Ŵ���
    private String userCode; //����Ա����

    private List<YXFile> fileList; // �ļ�����

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
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

    public String getBusiCode() {
        return busiCode;
    }

    public void setBusiCode(String busiCode) {
        this.busiCode = busiCode;
    }

    public String getCustProp() {
        return custProp;
    }

    public void setCustProp(String custProp) {
        this.custProp = custProp;
    }

    public String getAcctCode() {
        return acctCode;
    }

    public void setAcctCode(String acctCode) {
        this.acctCode = acctCode;
    }

    public String getOprDate() {
        return oprDate;
    }

    public void setOprDate(String oprDate) {
        this.oprDate = oprDate;
    }

    public String getDepCode() {
        return depCode;
    }

    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

}

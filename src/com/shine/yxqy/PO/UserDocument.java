package com.shine.yxqy.PO;

import java.util.List;

/**
 * 用户资料PO类
 */
public class UserDocument {
    private String certName;   //客户名称
    private String certCode;   //证件号码
    private String certType;   //证件类别
    private String custId;     //客户号

    private List<YXFile> fileList; //文件集合





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

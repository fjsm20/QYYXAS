package com.shine.yxqy.po;

import java.util.List;

/**
 * 用户资料PO类
 */
public class UserDocument {
    private String custName; // 客户名称
    private String certCode; // 证件号码
    private String certType; // 证件类别
    private String custId; // 客户号

    private String busiCode; //业务代码
    private String custProp; //客户类型
    private String acctCode; //账号
    private String oprDate; //业务时间
    private String depCode; //业务部门代码
    private String userCode; //操作员代码

    private List<YXFile> fileList; // 文件集合

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

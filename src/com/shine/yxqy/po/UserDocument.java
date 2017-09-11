package com.shine.yxqy.po;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户资料PO类
 */
public class UserDocument {
    private String id;       //编号
    private String custName; // 客户名称
    private String certCode; // 证件号码
    private String certType; // 证件类别
    private String custId;   // 客户号

    private String busiCode; //业务代码
    private String custProp; //客户类型
    private String acctCode; //账号
    private String oprDate; //业务时间
    private String depCode; //业务部门代码
    private String userCode; //操作员代码


    private String afterId;     //身份证反面照ID
    private String beforeId;    //身份证正面照ID
    private String policeId;    //公安部身份证照片id
    private String groupId;     //申请人与两名见证人合影id
    private String avatarId;    //客户头像id
    private String signFileId;  //客户手持开户已签字文件照

    private List<YXFile> fileList= new ArrayList<YXFile>(); // 文件集合
    private List<YXFile> lostFileList = new ArrayList<YXFile>();//文件不存在集合


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
        this.fileList.addAll(fileList);
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

    public String getAfterId() {
        return afterId;
    }

    public void setAfterId(String afterId) {
        this.afterId = afterId;
    }

    public String getBeforeId() {
        return beforeId;
    }

    public void setBeforeId(String beforeId) {
        this.beforeId = beforeId;
    }

    public String getPoliceId() {
        return policeId;
    }

    public void setPoliceId(String policeId) {
        this.policeId = policeId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(String avatarId) {
        this.avatarId = avatarId;
    }

    public String getSignFileId() {
        return signFileId;
    }

    public void setSignFileId(String signFileId) {
        this.signFileId = signFileId;
    }

    public List<YXFile> getLostFileList() {
        return lostFileList;
    }

    public void setLostFileList(List<YXFile> lostFileList) {
        this.lostFileList.addAll(lostFileList);
    }
}

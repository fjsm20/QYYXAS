package com.shine.yxqy.po;

import java.util.ArrayList;
import java.util.List;

/**
 * �û�����PO��
 */
public class UserDocument {
    private String id;       //���
    private String custName; // �ͻ�����
    private String certCode; // ֤������
    private String certType; // ֤�����
    private String custId;   // �ͻ���

    private String busiCode; //ҵ�����
    private String custProp; //�ͻ�����
    private String acctCode; //�˺�
    private String oprDate; //ҵ��ʱ��
    private String depCode; //ҵ���Ŵ���
    private String userCode; //����Ա����


    private String afterId;     //���֤������ID
    private String beforeId;    //���֤������ID
    private String policeId;    //���������֤��Ƭid
    private String groupId;     //��������������֤�˺�Ӱid
    private String avatarId;    //�ͻ�ͷ��id
    private String signFileId;  //�ͻ��ֳֿ�����ǩ���ļ���

    private List<YXFile> fileList= new ArrayList<YXFile>(); // �ļ�����
    private List<YXFile> lostFileList = new ArrayList<YXFile>();//�ļ������ڼ���


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

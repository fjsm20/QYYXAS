package com.shine.yxqy.po;

/**
 * ���϶���
 *
 */
public class YXFile {
    private String fileName;    //�ļ�����
    private String fileSize;    //�ļ���С
    private String rootPath;    //�ļ���·��
    private String relaPath;    //�ļ����·��
    private String fileType;    //�ļ����


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getRelaPath() {
        return relaPath;
    }

    public void setRelaPath(String relaPath) {
        this.relaPath = relaPath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}

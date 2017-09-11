package com.shine.yxqy.po;

/**
 * 资料对象
 *
 */
public class YXFile {

    private String fileName;     // 文件名称
    private String sourceNo;     // 扫描项
    private String absPath;      // 全路径

    private String moduleName;   //类别名称
    private String id;           //XML文件标签中YXZL子标签中的ID
    private String rootPath;     // 文件根路径
    private String relaPath;     // 文件相对路径
    private String adddate;      // 添加时间 2017-06-25 16:11:58
    private String moduleId;     //业务ID

    private String fileId; // 文件的ID

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileID(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public String getSourceNo() {
        return sourceNo;
    }

    public void setSourceNo(String sourceNo) {
        this.sourceNo = sourceNo;
    }

    public String getAbsPath() {
        return absPath;
    }

    public void setAbsPath(String absPath) {
        this.absPath = absPath;
    }

    public String getAdddate() {
        return adddate;
    }

    public void setAdddate(String adddate) {
        this.adddate = adddate;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }


}

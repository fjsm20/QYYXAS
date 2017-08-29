package com.shine.yxqy.po;

/**
 * 资料对象
 *
 */
public class YXFile {
    private String fileName;    //文件名称
    private String fileSize;    //文件大小
    private String rootPath;    //文件根路径
    private String relaPath;    //文件相对路径
    private String fileType;    //文件类别

    private String id;//XML文件标签中YXZL子标签中的ID

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

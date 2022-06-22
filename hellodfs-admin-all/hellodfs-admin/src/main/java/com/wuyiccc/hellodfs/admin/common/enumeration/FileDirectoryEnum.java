package com.wuyiccc.hellodfs.admin.common.enumeration;

/**
 * @author wuyiccc
 * @date 2022/6/5 9:41
 */
public enum FileDirectoryEnum {

    /**
     * 用户头像目录
     */
    IMAGE_USER_FACE_DIRECTORY("image/user/face"),

    /**
     * 用户存储文件的目录
     */
    IMAGE_USER_STORAGE_DIRECTORY("file/user/storage");

    private final String fileDirectory;

    FileDirectoryEnum(String fileDirectory) {
        this.fileDirectory = fileDirectory;
    }

    public String getFileDirectory() {
        return fileDirectory;
    }
}

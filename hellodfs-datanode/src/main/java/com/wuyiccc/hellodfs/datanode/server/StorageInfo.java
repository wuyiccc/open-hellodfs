package com.wuyiccc.hellodfs.datanode.server;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wuyiccc
 * @date 2022/5/18 0:02
 */
public class StorageInfo {

    /**
     * cur datanode already stored filename list
     */
    private List<String> filenameList = new ArrayList<>();

    /**
     * cur datanode already stored file size
     */
    private Long storedDataSize = 0L;

    public List<String> getFilenameList() {
        return filenameList;
    }

    public void setFilenameList(List<String> filenameList) {
        this.filenameList = filenameList;
    }

    public Long getStoredDataSize() {
        return storedDataSize;
    }

    public void setStoredDataSize(Long storedDataSize) {
        this.storedDataSize = storedDataSize;
    }

    public void addFilename(String filename) {
        this.filenameList.add(filename);
    }

    public void addStoredDataSize(Long storedDataSize) {
        this.storedDataSize += storedDataSize;
    }

    @Override
    public String toString() {
        return "StorageInfo{" +
                "filenameList=" + filenameList +
                ", storedDataSize=" + storedDataSize +
                '}';
    }
}

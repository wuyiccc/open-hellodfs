package com.wuyiccc.hellodfs.backupnode.server;

import com.alibaba.fastjson.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The core component responsible for managing the file directory tree in memory
 *
 * @author wuyiccc
 * @date 2022/4/26 22:11
 */
public class FSDirectory {

    private INode rootDirTree;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * current fsDirTree max sync txId
     */
    private long maxTxId;


    public void writeLock() {
        lock.writeLock().lock();
    }

    public void writeUnLock() {
        lock.writeLock().unlock();
    }

    public void readLock() {
        lock.readLock().lock();
    }

    public void readUnLock() {
        lock.readLock().unlock();
    }


    public FSDirectory() {
        this.rootDirTree = new INode("/");
    }


    public FSImage getFSImage() {
        FSImage fsImage = null;
        try {
            readLock();
            String fsImageJson = JSONObject.toJSONString(this.rootDirTree);
            fsImage = new FSImage(maxTxId, fsImageJson);
        } finally {
            readUnLock();
        }
        return fsImage;
    }

    /**
     * create a directory
     *
     * @param path /usr/warehouse/hive
     */
    public void mkdir(long txId, String path) {

        try {
            writeLock();

            this.maxTxId = txId;
            String[] pathArray = path.split("/");
            INode parent = this.rootDirTree;

            for (String splitPath : pathArray) {

                if ("".equals(splitPath.trim())) {
                    continue;
                }

                INode dir = findDirectory(parent, splitPath);

                // if we find the target directory, then continue to recursive find
                if (dir != null) {
                    parent = dir;
                    continue;
                }

                // if not found the target directory, we create a directory and then add into the parentDirectory
                INode child = new INode(splitPath);
                parent.addChild(child);
                parent = child;
            }
        } finally {
            writeUnLock();
        }


        //printDirTree(this.rootDirTree, "-");
    }

    private void printDirTree(INode dirTree, String blank) {
        if (dirTree == null || dirTree.getChildrenList().size() == 0) {
            return;
        }

        for (INode curDir : dirTree.getChildrenList()) {
            System.out.println(blank + ((INode) curDir).getPath());
            printDirTree((INode) curDir, blank + "-");
        }
    }

    /**
     * find child directory
     *
     * @param dir  current directory
     * @param path target path
     * @return null (cannot find the target directory) || INodeDirectory Object (the target directory)
     */
    private INode findDirectory(INode dir, String path) {

        if (dir.getChildrenList().size() == 0) {
            return null;
        }

        for (INode child : dir.getChildrenList()) {
            if (child instanceof INode) {
                if (((INode) child).getPath().equals(path)) {
                    return (INode) child;
                }
            }
        }
        return null;
    }



    /**
     * a directory node in filesystem tree
     */
    public static class INode {
        String path;
        List<INode> childrenList;

        public INode() {
        }

        public INode(String path) {
            this.path = path;
            this.childrenList = new LinkedList<>();
        }

        public void addChild(INode node) {
            this.childrenList.add(node);
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public List<INode> getChildrenList() {
            return childrenList;
        }

        public void setChildrenList(List<INode> childrenList) {
            this.childrenList = childrenList;
        }

        @Override
        public String toString() {
            return "INodeDirectory{" +
                    "path='" + path + '\'' +
                    ", childrenList=" + childrenList +
                    '}';
        }
    }

}

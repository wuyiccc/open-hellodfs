package com.wuyiccc.hellodfs.namenode.server;

import java.util.LinkedList;
import java.util.List;

/**
 * The core component responsible for managing the file directory tree in memory
 *
 * @author wuyiccc
 * @date 2022/4/26 22:11
 */
public class FSDirectory {

    private INodeDirectory dirTree;

    public FSDirectory() {
        this.dirTree = new INodeDirectory("/");
    }

    /**
     * create a directory
     *
     * @param path /usr/warehouse/hive
     */
    public void mkdir(String path) {

        synchronized (dirTree) {
            String[] pathArray = path.split("/");
            INodeDirectory parent = dirTree;

            for (String splitPath : pathArray) {

                if ("".equals(splitPath.trim())) {
                    continue;
                }

                INodeDirectory dir = findDirectory(parent, splitPath);

                // if we find the target directory, then continue to recursive find
                if (dir != null) {
                    parent = dir;
                    continue;
                }

                // if not found the target directory, we create a directory and then add into the parentDirectory
                INodeDirectory child = new INodeDirectory(splitPath);
                parent.addChild(child);
                parent = child;
            }
        }


    }

    /**
     * find child directory
     *
     * @param dir  current directory
     * @param path target path
     * @return null (cannot find the target directory) || INodeDirectory Object (the target directory)
     */
    private INodeDirectory findDirectory(INodeDirectory dir, String path) {

        if (dir.getChildrenList().size() == 0) {
            return null;
        }

        for (INode child : dir.getChildrenList()) {
            if (child instanceof INodeDirectory) {
                if (((INodeDirectory) child).getPath().equals(path)) {
                    return (INodeDirectory) child;
                }
            }
        }
        return null;
    }


    /**
     * a node in filesystem tree
     */
    public static interface INode {

    }

    /**
     * a directory node in filesystem tree
     */
    public static class INodeDirectory implements INode {
        String path;
        List<INode> childrenList;

        public INodeDirectory(String path) {
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
    }

    /**
     * a file node in filesystem tree
     */
    public static class INodeFile implements INode {

        String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

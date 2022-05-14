package com.wuyiccc.hellodfs.namenode.server;

/**
 * @author wuyiccc
 * @date 2022/5/14 15:53
 */
public class EditLogFactory {

    public static String mkdir(String path) {
        return "{'OP':'MKDIR','PATH':'" + path + "'}";
    }
    public static String create(String path) {
        return "{'OP':'CREATE','PATH':'" + path + "'}";
    }
}

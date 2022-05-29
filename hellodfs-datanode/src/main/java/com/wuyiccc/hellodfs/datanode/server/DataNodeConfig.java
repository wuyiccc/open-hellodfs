package com.wuyiccc.hellodfs.datanode.server;

/**
 * @author wuyiccc
 * @date 2022/5/15 17:43
 */
public class DataNodeConfig {

    public  String NAMENODE_HOSTNAME = "localhost";

    public  Integer NAMENODE_PORT = 50070;

    public  String DATANODE_HOSTNAME = "datanode02";

    public  String DATANODE_IP = "127.0.0.1";

    public  Integer NIO_PORT = 9302;

    public  String DATA_DIR = "E:\\code_learn\\031-opensource\\06-hellodfs\\hellodfs\\image\\tmp2";

    private static volatile DataNodeConfig instance;

    public static DataNodeConfig getInstance() {
        if (instance == null) {
            synchronized (DataNodeConfig.class) {
                if (instance == null) {
                    instance = new DataNodeConfig();
                }
            }
        }
        return instance;
    }

}

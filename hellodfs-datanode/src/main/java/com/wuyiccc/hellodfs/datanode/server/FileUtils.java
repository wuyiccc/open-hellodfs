package com.wuyiccc.hellodfs.datanode.server;

import java.io.File;

/**
 * @author wuyiccc
 * @date 2022/5/23 23:53
 */
public class FileUtils {


    public static String getAbsoluteFilename(String relativeFilename) throws Exception {
        String[] relativeFilenameSplit = relativeFilename.split("/");

        String dirPath = DataNodeConfig.DATA_DIR;
        for(int i = 0; i < relativeFilenameSplit.length - 1; i++) {
            if(i == 0) {
                continue;
            }
            dirPath += "\\" + relativeFilenameSplit[i];
        }

        File dir = new File(dirPath);
        if(!dir.exists()) {
            dir.mkdirs();
        }

        String absoluteFilename = dirPath + "\\" + relativeFilenameSplit[relativeFilenameSplit.length - 1];
        return absoluteFilename;
    }
}

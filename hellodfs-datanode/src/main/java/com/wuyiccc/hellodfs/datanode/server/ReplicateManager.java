package com.wuyiccc.hellodfs.datanode.server;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author wuyiccc
 * @date 2022/5/23 22:33
 */
public class ReplicateManager {


    public static final Integer REPLICATE_THREAD_NUM = 3;

    private ConcurrentLinkedQueue<JSONObject> replicateTaskQueue = new ConcurrentLinkedQueue<>();

    private NIOClient nioClient = new NIOClient();

    public ReplicateManager() {
        for (int i = 0; i < REPLICATE_THREAD_NUM; i++) {
            new ReplicateWorker().start();
        }
    }


    public void addReplicateTask(JSONObject replicateTask) {
        replicateTaskQueue.offer(replicateTask);
    }

    class ReplicateWorker extends Thread {
        @Override
        public void run() {

            while(true) {
                FileOutputStream imageOut = null;
                FileChannel imageChannel = null;

                try {
                    JSONObject replicateTask = replicateTaskQueue.poll();
                    if(replicateTask == null) {
                        Thread.sleep(1000);
                        continue;
                    }

                    String filename = replicateTask.getString("filename");

                    JSONObject sourceDatanode = replicateTask.getJSONObject("sourceDataNodeInfo");
                    String hostname = sourceDatanode.getString("hostname");
                    Integer nioPort = sourceDatanode.getInteger("nioPort");

                    byte[] file = nioClient.readFile(hostname, nioPort, filename);
                    ByteBuffer fileBuffer = ByteBuffer.wrap(file);

                    String absoluteFilename = FileUtils.getAbsoluteFilename(filename);

                    imageOut = new FileOutputStream(absoluteFilename);
                    imageChannel = imageOut.getChannel();

                    imageChannel.write(fileBuffer);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if(imageChannel != null) {
                            imageChannel.close();
                        }
                        if(imageOut != null) {
                            imageOut.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }

    }
}

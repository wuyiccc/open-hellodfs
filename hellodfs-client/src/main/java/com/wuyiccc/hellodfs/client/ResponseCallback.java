package com.wuyiccc.hellodfs.client;

/**
 * @author wuyiccc
 * @date 2022/5/29 10:26
 */
public interface ResponseCallback {

    /**
     * callback result
     */
    void process(NetworkResponse response);
}

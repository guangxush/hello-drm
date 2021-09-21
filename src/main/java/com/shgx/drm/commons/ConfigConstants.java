package com.shgx.drm.commons;

/**
 * @author: guangxush
 * @create: 2021/09/20
 */
public class ConfigConstants {

    /**
     * 注册中心节点根路径
     */
    public static final String BASE_URL = "/config";

    /**
     * 生产者线程池线程数目
     */
    public static final int PROVIDER_THREAD_POOL_NUM = 256;

    /**
     * 生产者线程池工作队列长度
     */
    public static final int PROVIDER_THREAD_POOL_QUEUE_LEN = 1024;

    public static String INIT_METHOD = "init";
}

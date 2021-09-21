package com.shgx.drm.publisher;

import com.shgx.drm.commons.ConfigRegistryEnum;
import com.shgx.drm.configcenter.ConfigRegistryFactory;
import com.shgx.drm.subscriber.HelloConfigImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: guangxush
 * @create: 2021/09/21
 */
@Slf4j
public class TestPublisher {
    public static void main(String[] args) throws Exception {
        String serverAddress = "127.0.0.1:6688";
        String registryAddress = "127.0.0.1:2181";

        ConfigPublisher configPublisher = new ConfigPublisher(serverAddress, ConfigRegistryFactory.getInstance(ConfigRegistryEnum.zookeeper, registryAddress));
        HelloConfig helloConfig = new HelloConfigImpl();
        configPublisher.addResource(helloConfig, serverAddress);
        try {
            configPublisher.start();
        } catch (Exception e) {
            log.error("exception:", e);
        }
    }
}

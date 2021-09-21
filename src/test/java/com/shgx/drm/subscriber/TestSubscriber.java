package com.shgx.drm.subscriber;

import com.shgx.drm.commons.ConfigRegistryEnum;
import com.shgx.drm.configcenter.ConfigRegistryFactory;
import com.shgx.drm.publisher.HelloConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: guangxush
 * @create: 2021/09/21
 */
@Slf4j
public class TestSubscriber {

    public static void main(String[] args) throws Exception {
        String address = "127.0.0.1:2181";
        HelloConfig helloConfig = ConfigSubscriber.create(HelloConfig.class, "0.0.1",
                ConfigRegistryFactory.getInstance(ConfigRegistryEnum.zookeeper, address));
        String response = helloConfig.hello("shgx");
        log.info("response: "+ response);
    }
}

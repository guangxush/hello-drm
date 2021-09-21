package com.shgx.drm.subscriber;

import com.shgx.drm.annotation.DResource;
import com.shgx.drm.publisher.HelloConfig;

/**
 * @author: guangxush
 * @create: 2021/09/21
 */
@DResource(resourceName = HelloConfig.class, resourceVersion = "0.0.1")
public class HelloConfigImpl implements HelloConfig{

    @Override
    public String hello(String config) {
        return "hello " + config;
    }
}

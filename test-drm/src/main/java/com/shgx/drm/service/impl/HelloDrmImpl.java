package com.shgx.drm.service.impl;

import com.shgx.drm.service.HelloDrm;

/**
 * @author: guangxush
 * @create: 2021/09/21
 */
public class HelloDrmImpl implements HelloDrm {
    /**
     * hello
     *
     * @param param
     * @return
     */
    @Override
    public String hello(String param) {
        return "hello, " + param;
    }
}

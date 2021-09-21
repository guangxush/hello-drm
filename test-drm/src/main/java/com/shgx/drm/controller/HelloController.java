package com.shgx.drm.controller;

import com.shgx.drm.service.HelloDrm;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: guangxush
 * @create: 2021/09/21
 */
public class HelloController {

    private HelloDrm helloDrm;

    @GetMapping("/hello")
    public String testHello(@RequestParam String param){
        // http://localhost:8081/hello?param=rpc
        return helloDrm.hello(param);
    }
}

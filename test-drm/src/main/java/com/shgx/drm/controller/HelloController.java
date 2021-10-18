package com.shgx.drm.controller;

import com.shgx.drm.annotation.DAttribute;
import com.shgx.drm.service.HelloDrm;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: guangxush
 * @create: 2021/09/21
 */
@RestController
public class HelloController {

    @DAttribute(resourceVersion = "0.0.2", resourceName = HelloDrm.class)
    public HelloDrm helloDrm;

    @GetMapping("/hello")
    public String testHello(@RequestParam String param){
        // http://localhost:8082/hello?param=drm
        return helloDrm.hello(param);
    }
}

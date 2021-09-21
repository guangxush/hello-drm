package com.shgx.drm.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于资源注册和监听
 *
 * @author: guangxush
 * @create: 2021/09/20
 */
@Retention(RetentionPolicy.RUNTIME) //运行时解析
@Target({ElementType.TYPE}) //注解目标为类
@Component //被Spring加载
public @interface DResource {
    /**
     * 资源版本
     *
     * @return
     */
    String resourceVersion() default "0.0.1";

    /**
     * 资源名称
     *
     * @return
     */
    Class<?> resourceName() default Object.class;

    /**
     * 资源服务地址
     *
     * @return
     */
    String registryAddress() default "127.0.0.1:2181";

    /**
     * 注册中心
     *
     * @return
     */
    String registryType() default "zookeeper";

}

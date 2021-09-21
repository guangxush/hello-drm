package com.shgx.drm.subscriber;

import com.shgx.drm.commons.ConfigProperties;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: guangxush
 * @create: 2021/09/21
 */
@Configuration
@EnableConfigurationProperties(ConfigProperties.class)
public class SubscribeAutoConfiguration {

    @Bean
    public static BeanFactoryPostProcessor consumerPostProcess() {
        return new SubScribePostProcessor();
    }
}

package com.shgx.drm.publisher;

import com.shgx.drm.commons.ConfigProperties;
import com.shgx.drm.commons.ConfigRegistryEnum;
import com.shgx.drm.configcenter.ConfigRegistry;
import com.shgx.drm.configcenter.ConfigRegistryFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 配置自动注册
 *
 * @author: guangxush
 * @create: 2021/09/20
 */
@Configuration
@EnableConfigurationProperties(ConfigProperties.class)
public class ConfigAutoConfiguration {
    @Resource
    private ConfigProperties properties;

    @Bean
    public ConfigPublisher init() throws Exception {
        // 根据配置选择注册中心
        ConfigRegistryEnum type = ConfigRegistryEnum.valueOf(properties.getConfigRegistryType());
        // 单例模式
        ConfigRegistry configRegistry = ConfigRegistryFactory.getInstance(type, properties.getConfigRegistryAddress());
        return new ConfigPublisher(properties.getConfigAddress(), configRegistry);
    }
}

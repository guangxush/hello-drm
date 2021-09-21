package com.shgx.drm.commons;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置属性
 *
 * @author: guangxush
 * @create: 2021/09/20
 */
@Data
@ConfigurationProperties(prefix = "drm")
public class ConfigProperties {
    private String configAddress;
    private String configRegistryAddress;
    private String configRegistryType;
}

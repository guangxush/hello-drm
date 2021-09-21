package com.shgx.drm.configcenter;

import com.shgx.drm.commons.ConfigRegistryEnum;

/**
 * 配置注册工厂
 *
 * @author: guangxush
 * @create: 2021/09/20
 */
public class ConfigRegistryFactory {
    private static volatile ConfigRegistry configRegistry;

    /**
     * 根据服务类型和地址获取注册服务
     *
     * @param configRegistryEnum
     * @param registryAddress
     * @return
     * @throws Exception
     */
    public static ConfigRegistry getInstance(ConfigRegistryEnum configRegistryEnum, String registryAddress) throws Exception {
        if (null == configRegistry) {
            synchronized (ConfigRegistryEnum.class) {
                if (null == configRegistry) {
                    configRegistry = configRegistryEnum == ConfigRegistryEnum.zookeeper ? new ZookeeperConfigRegistry(registryAddress) :
                            configRegistryEnum == ConfigRegistryEnum.redis ? new RedisConfigRegistry(registryAddress) : null;
                }
            }
        }
        return configRegistry;
    }
}

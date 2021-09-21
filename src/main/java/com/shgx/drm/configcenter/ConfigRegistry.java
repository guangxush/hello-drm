package com.shgx.drm.configcenter;

/**
 * 配置注册
 *
 * @author: guangxush
 * @create: 2021/09/20
 */
public interface ConfigRegistry {
    /**
     * 配置注册
     * @param configModel
     * @throws Exception
     */
    void register(ConfigModel configModel) throws Exception;

    /**
     * 配置注销
     * @param configModel
     * @throws Exception
     */
    void unRegister(ConfigModel configModel) throws Exception;

    /**
     * 配置发现
     * @param configName
     * @return
     * @throws Exception
     */
    ConfigModel discovery(String configName) throws Exception;

    /**
     * 配置关闭
     * @throws Exception
     */
    void close() throws Exception;
}

package com.shgx.drm.configcenter;

/**
 * todo 使用redis实现配置注册和发现
 *
 * @author: guangxush
 * @create: 2021/09/20
 */
public class RedisConfigRegistry implements ConfigRegistry{

    public RedisConfigRegistry(String address) {
    }

    /**
     * 配置注册
     *
     * @param configModel
     * @throws Exception
     */
    @Override
    public void register(ConfigModel configModel) throws Exception {

    }

    /**
     * 配置注销
     *
     * @param configModel
     * @throws Exception
     */
    @Override
    public void unRegister(ConfigModel configModel) throws Exception {

    }

    /**
     * 配置发现
     *
     * @param configName
     * @return
     * @throws Exception
     */
    @Override
    public ConfigModel discovery(String configName) throws Exception {
        return null;
    }

    /**
     * 配置关闭
     *
     * @throws Exception
     */
    @Override
    public void close() throws Exception {

    }
}

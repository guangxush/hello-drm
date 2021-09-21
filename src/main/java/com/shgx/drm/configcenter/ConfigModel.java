package com.shgx.drm.configcenter;

import lombok.Data;

/**
 * 配置模型
 *
 * @author: guangxush
 * @create: 2021/09/20
 */
@Data
public class ConfigModel {
    /**
     * 配置名
     */
    private String configName;
    /**
     * 配置版本
     */
    private String configVersion;
    /**
     * 配置地址
     */
    private String address;
    /**
     * 配置端口
     */
    private int port;

    public static ConfigModel builder() {
        return new ConfigModel();
    }

    public ConfigModel configName(String configName) {
        this.setConfigName(configName);
        return this;
    }


    public ConfigModel configPort(int port) {
        this.port = port;
        return this;
    }


    public ConfigModel address(String address) {
        this.address = address;
        return this;
    }

    public ConfigModel configVersion(String configVersion) {
        this.configVersion = configVersion;
        return this;
    }


    @Override
    public String toString() {
        return "ConfigModel{" +
                "configName='" + configName + '\'' +
                ", configVersion='" + configVersion + '\'' +
                ", address='" + address + '\'' +
                ", port=" + port +
                '}';
    }
}

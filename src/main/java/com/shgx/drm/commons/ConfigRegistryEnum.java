package com.shgx.drm.commons;


import org.apache.commons.lang.StringUtils;

/**
 * 注册类型枚举
 *
 * @author: guangxush
 * @create: 2021/09/20
 */
public enum ConfigRegistryEnum {
    /**
     * zookeeper注册
     */
    zookeeper("zookeeper", "zookeeper注册"),
    /**
     * eureka注册
     */
    eureka("eureka", "eureka注册"),

    /**
     * redis注册
     */
    redis("redis", "redis注册"),
    ;

    /**
     * code
     */
    private String code;
    /**
     * 描述
     */
    private String desc;

    ConfigRegistryEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ConfigRegistryEnum getByCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        for (ConfigRegistryEnum configRegistryEnum : ConfigRegistryEnum.values()) {
            if (StringUtils.equals(configRegistryEnum.getCode(), code)) {
                return configRegistryEnum;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

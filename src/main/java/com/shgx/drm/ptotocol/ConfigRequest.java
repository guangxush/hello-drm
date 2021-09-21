package com.shgx.drm.ptotocol;

import lombok.Data;

import java.io.Serializable;

/**
 * 配置请求
 *
 * @author: guangxush
 * @create: 2021/09/20
 */
@Data
public class ConfigRequest implements Serializable {
    private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
    private String serviceVersion;
}

package com.shgx.drm.ptotocol;

import lombok.Data;

import java.io.Serializable;

/**
 * 配置返回值
 *
 * @author: guangxush
 * @create: 2021/09/20
 */
@Data
public class ConfigResponse implements Serializable {
    private String requestId;
    private Object result;
    private String exception;
}

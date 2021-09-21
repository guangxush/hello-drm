package com.shgx.drm.subscriber;

import com.shgx.drm.configcenter.ConfigRegistry;
import com.shgx.drm.ptotocol.ConfigRequest;
import com.shgx.drm.ptotocol.ConfigResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author: guangxush
 * @create: 2021/09/21
 */
@Slf4j
public class ConfigInvokeHandler<T> implements InvocationHandler {

    private static final String EQUALS = "equals";
    private static final String HASH_CODE = "hashCode";
    private static final String TO_STRING = "toString";

    private String configVersion;
    private ConfigRegistry configRegistry;

    public ConfigInvokeHandler() {
    }

    public ConfigInvokeHandler(String configVersion, ConfigRegistry configRegistry) {
        this.configVersion = configVersion;
        this.configRegistry = configRegistry;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            switch (name) {
                case EQUALS:
                    return proxy == args[0];
                case HASH_CODE:
                    System.identityHashCode(proxy);
                    break;
                case TO_STRING:
                    return proxy.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(proxy)) + ", with InvocationHandler" + this;
                default:
                    throw new IllegalStateException(String.valueOf(method));
            }
        }
        ConfigRequest configRequest = new ConfigRequest();
        configRequest.setRequestId(UUID.randomUUID().toString());
        configRequest.setClassName(method.getDeclaringClass().getName());
        configRequest.setServiceVersion(this.configVersion);
        configRequest.setMethodName(method.getName());
        configRequest.setParameterTypes(method.getParameterTypes());
        configRequest.setParameters(args);

        log.debug(method.getDeclaringClass().getName());
        log.debug(method.getName());
        for(int i=0;i<method.getParameterTypes().length;i++){
            log.debug(method.getParameterTypes()[i].getName());
        }
        for (Object arg : args) {
            log.debug(arg.toString());
        }

        ConfigSubscriber configSubscriber = new ConfigSubscriber(this.configRegistry);
        ConfigResponse configResponse = configSubscriber.sendRequest(configRequest);
        if(null != configResponse){
            log.debug("consumer receive provider config response:" + configResponse.toString());
            return configResponse.getResult();
        }else{
            throw new RuntimeException("consumer config fail, response is null!");
        }
    }
}

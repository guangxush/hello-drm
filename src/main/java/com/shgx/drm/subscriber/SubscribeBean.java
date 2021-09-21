package com.shgx.drm.subscriber;

import com.shgx.drm.commons.ConfigRegistryEnum;
import com.shgx.drm.configcenter.ConfigRegistryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author: guangxush
 * @create: 2021/09/21
 */
@Slf4j
public class SubscribeBean implements FactoryBean {

    private Class<?> resourceName;
    private String resourceVersion;
    private String registryType;
    private String registryAddress;
    private Object object;

    @Override
    public Object getObject() throws Exception {
        return this.object;
    }


    @Override
    public Class<?> getObjectType() {
        return resourceName;
    }


    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setResourceName(Class<?> resourceName) {
        this.resourceName = resourceName;
    }

    public void setResourceVersion(String resourceVersion) {
        this.resourceVersion = resourceVersion;
    }

    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public void init() throws Exception {
        this.object = ConfigSubscriber.create(resourceName, resourceVersion, ConfigRegistryFactory.getInstance(
                ConfigRegistryEnum.valueOf(registryType), registryAddress
        ));
        log.info("SubscribeBean {} init ...", resourceName.getName());
    }
}

package com.shgx.drm.configcenter;

import com.google.common.collect.Lists;
import com.shgx.drm.commons.ConfigConstants;
import com.shgx.drm.commons.ConfigUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.*;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;

import java.io.Closeable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * zookeeper实现配置注册
 *
 * @author: guangxush
 * @create: 2021/09/20
 */
public class ZookeeperConfigRegistry implements ConfigRegistry {

    private final CuratorFramework curatorFramework;
    private final Object lock = new Object();
    private ServiceDiscovery<ConfigModel> serviceDiscovery;

    /**
     * 提供本地缓存服务，避免过多创建请求
     */
    private Map<String, ServiceProvider<ConfigModel>> configProviderCache;
    private List<Closeable> closeableProvider = Lists.newArrayList();

    public ZookeeperConfigRegistry(String address) throws Exception {
        configProviderCache = new ConcurrentHashMap<>(256);
        this.curatorFramework = CuratorFrameworkFactory.newClient(address, new ExponentialBackoffRetry(1000, 3));
        this.curatorFramework.start();
        JsonInstanceSerializer<ConfigModel> serializer = new JsonInstanceSerializer<>(ConfigModel.class);
        serviceDiscovery = ServiceDiscoveryBuilder.
                builder(ConfigModel.class)
                .client(this.curatorFramework)
                .serializer(serializer)
                .basePath(ConfigConstants.BASE_URL)
                .build();
        serviceDiscovery.start();
    }

    /**
     * 配置注册
     *
     * @param configModel
     * @throws Exception
     */
    @Override
    public void register(ConfigModel configModel) throws Exception {
        ServiceInstance<ConfigModel> serviceInstance = ServiceInstance
                .<ConfigModel>builder()
                //使用{服务名}:{服务版本}来唯一标识一个配置服务
                .name(ConfigUtils.generateKey(configModel.getConfigName(), configModel.getConfigVersion()))
                .address(configModel.getAddress())
                .port(configModel.getPort())
                .payload(configModel)
                .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
                .build();
        serviceDiscovery.registerService(serviceInstance);
    }

    /**
     * 配置注销
     *
     * @param configModel
     * @throws Exception
     */
    @Override
    public void unRegister(ConfigModel configModel) throws Exception {
        ServiceInstance<ConfigModel> serviceInstance =
                ServiceInstance.<ConfigModel>builder()
                        .name(ConfigUtils.generateKey(configModel.getConfigName(), configModel.getConfigVersion()))
                        .address(configModel.getAddress())
                        .port(configModel.getPort())
                        .payload(configModel)
                        .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
                        .build();
        serviceDiscovery.unregisterService(serviceInstance);
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
        // 读取缓存
        ServiceProvider<ConfigModel> configProvider = configProviderCache.get(configName);
        if (null == configProvider) {
            synchronized (lock) {
                configProvider = serviceDiscovery
                        .serviceProviderBuilder()
                        .serviceName(configName)
                        //设置负载均衡策略，这里使用轮询
                        .providerStrategy(new RoundRobinStrategy<>())
                        .build();
                configProvider.start();
                closeableProvider.add(configProvider);
                configProviderCache.put(configName, configProvider);
            }
        }
        ServiceInstance<ConfigModel> serviceInstance = configProvider.getInstance();
        return null != serviceInstance ? serviceInstance.getPayload() : null;
    }

    /**
     * 关闭配置
     *
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        for (Closeable closeable : closeableProvider) {
            CloseableUtils.closeQuietly(closeable);
        }
        serviceDiscovery.close();
    }
}

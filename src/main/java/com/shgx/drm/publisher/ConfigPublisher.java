package com.shgx.drm.publisher;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.shgx.drm.annotation.DResource;
import com.shgx.drm.commons.ConfigUtils;
import com.shgx.drm.configcenter.ConfigModel;
import com.shgx.drm.configcenter.ConfigRegistry;
import com.shgx.drm.ptotocol.ConfigDecoder;
import com.shgx.drm.ptotocol.ConfigEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.shgx.drm.commons.ConfigConstants.PROVIDER_THREAD_POOL_NUM;
import static com.shgx.drm.commons.ConfigConstants.PROVIDER_THREAD_POOL_QUEUE_LEN;

/**
 * 配置发布
 *
 * @author: guangxush
 * @create: 2021/09/20
 */
@Slf4j
public class ConfigPublisher implements InitializingBean, BeanPostProcessor {

    private static ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("config-pool-%d").build();
    private static ThreadPoolExecutor threadPoolExecutor;
    private String configAddress;
    private ConfigRegistry configRegistry;
    private Map<String, Object> handlerMap = new HashMap<>(256);
    private EventLoopGroup bossGroup = null;
    private EventLoopGroup workerGroup = null;

    public ConfigPublisher(String configAddress){
        this.configAddress = configAddress;
    }

    public ConfigPublisher(String configAddress, ConfigRegistry configRegistry){
        this.configAddress = configAddress;
        this.configRegistry = configRegistry;
    }

    public static void submit(Runnable task){
        // 两段锁创建线程池
        if (threadPoolExecutor == null) {
            synchronized (ConfigPublisher.class) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = new ThreadPoolExecutor(PROVIDER_THREAD_POOL_NUM,
                            PROVIDER_THREAD_POOL_NUM,
                            600L,
                            TimeUnit.SECONDS,
                            new ArrayBlockingQueue<>(PROVIDER_THREAD_POOL_QUEUE_LEN),
                            threadFactory);
                }
            }
        }
        threadPoolExecutor.submit(task);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(() -> {
            try {
                start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ).start();
    }

    /**
     * netty监听服务, 进行服务注册
     * @throws InterruptedException
     */
    public void start() throws InterruptedException {
        if (bossGroup == null || workerGroup == null) {
            // bossGroup线程的机制是多路复用, 都是NioEventLoopGroup，一个线程但是可以监听多个新连接
            // bossGroup用来处理nio的Accept，worker处理nio的Read和Write事件
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            // ServerBootstrap是一个用来创建服务端Channel的工具类，创建出来的Channel用来接收进来的请求；只用来做面向连接的传输，像TCP/IP。
            ServerBootstrap bootstrap = new ServerBootstrap();
            //通用平台使用NioServerSocketChannel，Linux使用EpollServerSocketChannel
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0))
                                    .addLast(new ConfigDecoder())
                                    .addLast(new ConfigEncoder())
                                    .addLast(new PublisherHandler(handlerMap));
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            String[] address = configAddress.split(":");
            String host = address[0];
            int port = Integer.parseInt(address[1]);
            // 绑定端口，开启服务监听请求
            ChannelFuture future = bootstrap.bind(host, port).sync();
            log.info("Server started on port {}", port);
            // 同步等待，需要单独开启线程调用start方法
            future.channel().closeFuture().sync();
        }
    }

    /**
     * 手动注册配置资源，用于Web接口添加配置
     *
     * @param configRegisterBean 配置方注册的bean
     * @param serverAddress 配置方注册地址
     */
    public void addResource(Object configRegisterBean, String serverAddress){
        DResource dResource = configRegisterBean.getClass().getAnnotation(DResource.class);
        String configName = dResource.resourceName().getName();
        String version = dResource.resourceVersion();
        String configKey = ConfigUtils.generateKey(configName, version);
        String[] address = serverAddress.split(":");
        String host = address[0];
        int port = Integer.parseInt(address[1]);
        ConfigModel configModel = ConfigModel.builder()
                .address(host)
                .configName(configName)
                .configPort(port)
                .configVersion(version);
        try {
            configRegistry.register(configModel);
            log.debug("register config...", configModel.toString());
        } catch (Exception e) {
            log.error("register fail...", configModel.toString(), e);
        }

        if(!handlerMap.containsKey(configKey)){
            log.info("Loading config..."+ configKey);
            handlerMap.put(configKey, configRegisterBean);
        }
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 获取DResource修饰的bean
        DResource dResource = bean.getClass().getAnnotation(DResource.class);
        // 如果没有被修饰直接返回bean
        if(dResource == null){
            return bean;
        }
        // 获取注解后的服务名，版本号
        String configName = dResource.resourceName().getName();
        String version = dResource.resourceVersion();
        String configKey = ConfigUtils.generateKey(configName, version);
        // 缓存configKey bean到本地缓存中
        handlerMap.put(configKey, bean);

        // 配置注册到注册中心
        String[] address = configAddress.split(":");
        String host = address[0];
        int port = Integer.parseInt(address[1]);
        // 创建配置元数据
        ConfigModel configModel = ConfigModel.builder()
                .address(host)
                .configName(configName)
                .configPort(port)
                .configVersion(version);
        try {
            // 尝试配置注册到注册中心
            configRegistry.register(configModel);
            log.debug("register config... {}", configModel.toString());
        } catch (Exception e) {
            log.error("register fail {}", configModel.toString(), e);
        }
        // 返回bean
        return bean;
    }
}

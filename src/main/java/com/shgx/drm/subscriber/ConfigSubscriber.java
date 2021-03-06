package com.shgx.drm.subscriber;

import com.shgx.drm.commons.ConfigProperties;
import com.shgx.drm.commons.ConfigUtils;
import com.shgx.drm.configcenter.ConfigModel;
import com.shgx.drm.configcenter.ConfigRegistry;
import com.shgx.drm.ptotocol.ConfigDecoder;
import com.shgx.drm.ptotocol.ConfigEncoder;
import com.shgx.drm.ptotocol.ConfigRequest;
import com.shgx.drm.ptotocol.ConfigResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.rmi.registry.Registry;

/**
 * @author: guangxush
 * @create: 2021/09/21
 */
@Slf4j
public class ConfigSubscriber extends SimpleChannelInboundHandler<ConfigResponse> {

    private final Object obj = new Object();
    private ConfigRegistry configRegistry;
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
    private Channel channel;
    private ConfigResponse configResponse;

    public ConfigSubscriber(ConfigRegistry configRegistry) {
        this.configRegistry = configRegistry;
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> interfaceClass, String serviceVersion, ConfigRegistry configRegistry) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new ConfigInvokeHandler<>(serviceVersion, configRegistry));
    }

    public ConfigResponse sendRequest(ConfigRequest configRequest)throws Exception{
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            log.debug("init the subscribe request...");
                            channel.pipeline()
                                    .addLast(new ConfigEncoder())
                                    .addLast(new ConfigDecoder())
                                    .addLast(ConfigSubscriber.this);
                        }
                    });
            String targetService = ConfigUtils.generateKey(configRequest.getClassName(), configRequest.getServiceVersion());
            ConfigModel configModel = configRegistry.discovery(targetService);
            if(configModel == null){
                // ????????????????????????
                throw new RuntimeException("no available config publisher for" + targetService);
            }
            log.debug("discovery config for {}-{}", targetService, configModel.toString());
            final ChannelFuture future = bootstrap.connect(configModel.getAddress(), configModel.getPort()).sync();

            future.addListener((ChannelFutureListener) arg0 -> {
                if(future.isSuccess()){
                    log.debug("connect config publisher success");
                }else{
                    log.error("connect config publisher failed");
                    future.cause().printStackTrace();
                    // ???????????????
                    eventLoopGroup.shutdownGracefully();
                }
            });
            this.channel = future.channel();
            this.channel.writeAndFlush(configRequest).sync();

            synchronized (this.obj){
                this.obj.wait();
            }

            return this.configResponse;
        }finally {
            close();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ConfigResponse configResponse) throws Exception {
        this.configResponse = configResponse;

        synchronized (obj){
            // ??????????????? ????????????
            obj.notifyAll();
        }
    }

    /**
     * ???????????????
     */
    private void close(){
        // ???????????????
        if(this.channel!=null){
            this.channel.close();
        }
        // ???????????????
        if(this.eventLoopGroup!=null){
            this.eventLoopGroup.shutdownGracefully();
        }
        log.debug("shutdown subscribe....");
    }
}

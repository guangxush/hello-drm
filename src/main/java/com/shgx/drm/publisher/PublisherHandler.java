package com.shgx.drm.publisher;

import com.shgx.drm.commons.ConfigUtils;
import com.shgx.drm.ptotocol.ConfigRequest;
import com.shgx.drm.ptotocol.ConfigResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.reflect.FastClass;

import java.util.Map;

/**
 * 注册请求
 *
 * @author: guangxush
 * @create: 2021/09/20
 */
@Slf4j
public class PublisherHandler extends SimpleChannelInboundHandler<ConfigRequest> {

    private final Map<String, Object> handlerMap;

    public PublisherHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    /**
     * 注册请求处理器
     *
     * @param channelHandlerContext
     * @param configRequest
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ConfigRequest configRequest) throws Exception {
        ConfigPublisher.submit(() -> {
            log.debug("Receive request {}", configRequest.getRequestId());
            ConfigResponse configResponse = new ConfigResponse();
            configResponse.setRequestId(configRequest.getRequestId());
            try {
                Object result = handle(configRequest);
                configResponse.setResult(result);
            } catch (Throwable throwable) {
                configResponse.setException(throwable.toString());
                log.error("Config server handle request error", throwable);
            }
            channelHandlerContext.writeAndFlush(configResponse)
                    .addListener((ChannelFutureListener) channelFuture -> log.debug("send response for request:" + configRequest.getRequestId()));
        });
    }

    /**
     * 消息处理器核心实现
     *
     * @param configRequest
     * @return
     * @throws Throwable
     */
    private Object handle(ConfigRequest configRequest) throws Throwable {
        // 生成配置注册key
        String configKey = ConfigUtils.generateKey(configRequest.getClassName(), configRequest.getServiceVersion());
        // 从缓存中获取相关的bean，缓存map的注册在ConfigPublisher中实现
        Object publisherBean = handlerMap.get(configKey);
        if (null == publisherBean) {
            // 没有获取到当前bean服务
            throw new RuntimeException(String.format("Publisher not exist: %s:%s", configRequest.getClassName(), configRequest.getMethodName()));
        }

        // 使用反射完成消息处理
        Class<?> providerClass = publisherBean.getClass();
        String methodName = configRequest.getMethodName();
        Class<?>[] parameterTypes = configRequest.getParameterTypes();
        Object[] parameters = configRequest.getParameters();
        // 打印类名
        log.debug(providerClass.getName());
        // 打印方法名
        log.debug(methodName);
        // 打印参数类型
        for (Class<?> parameterType : parameterTypes) {
            log.debug(parameterType.getName());
        }
        // 打印参数
        for (Object parameter : parameters) {
            log.debug(parameter.toString());
        }
        // 使用Cglib创建服务生产者的代理对象，调用指定的方法
        FastClass publisherFastClass = FastClass.create(providerClass);
        int methodIndex = publisherFastClass.getIndex(methodName, parameterTypes);
        return publisherFastClass.invoke(methodIndex, publisherBean, parameters);
    }
}

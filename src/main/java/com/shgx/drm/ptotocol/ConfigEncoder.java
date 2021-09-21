package com.shgx.drm.ptotocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码器
 *
 * @author: guangxush
 * @create: 2021/09/20
 */
public class ConfigEncoder extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf){
        byte[] data = HessianSDK.serialize(o);
        assert  data != null;
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);
    }
}

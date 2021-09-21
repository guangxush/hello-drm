package com.shgx.drm.ptotocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 解码器
 *
 * @author: guangxush
 * @create: 2021/09/20
 */
public class ConfigDecoder extends ByteToMessageDecoder {

    public ConfigDecoder() {
    }

    @Override
    protected final void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out){
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        Object object = HessianSDK.deserialize(data);
        assert object != null;
        out.add(object);
    }
}

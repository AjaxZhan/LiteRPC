package cn.cagurzhan.core.common;

import cn.cagurzhan.core.common.constants.RpcConstant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 解码器
 * @author AjaxZhan
 */
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProtocol msg, ByteBuf out) throws Exception {
        out.writeShort(RpcConstant.MAGIC_NUMBER);
        out.writeInt(msg.getContentLength());
        out.writeBytes(msg.getContent());
    }
}

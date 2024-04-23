package cn.cagurzhan.core.client;

import cn.cagurzhan.core.common.RpcInvocation;
import cn.cagurzhan.core.common.RpcProtocol;
import cn.cagurzhan.core.common.cache.CommonClientCache;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * @author AjaxZhan
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 拿到响应，添加到MAP中
        RpcProtocol rpcProtocol = (RpcProtocol) msg;
        RpcInvocation rpcInvocation = JSON.parseObject(new String(rpcProtocol.getContent(),0,rpcProtocol.getContent().length), RpcInvocation.class);
        if(!CommonClientCache.RESP_MAP.containsKey(rpcInvocation.getUuid())){
            throw new IllegalArgumentException("server response is error!");
        }
        CommonClientCache.RESP_MAP.put(rpcInvocation.getUuid(),rpcInvocation);
        // 释放内存
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if(ctx.channel().isActive()){
            ctx.close();
        }
    }
}

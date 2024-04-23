package cn.cagurzhan.core.server;

import cn.cagurzhan.core.common.RpcInvocation;
import cn.cagurzhan.core.common.RpcProtocol;
import cn.cagurzhan.core.common.cache.CommonServerCache;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;

/**
 * 服务端处理器
 * @author AjaxZhan
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * 读数据包前
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 接收数据包
        RpcProtocol rpcProtocol = (RpcProtocol) msg;
        String invocationJson = new String(rpcProtocol.getContent(),0, rpcProtocol.getContentLength());
        RpcInvocation rpcInvocation = JSON.parseObject(invocationJson, RpcInvocation.class);
        // 调用方法
        Object serviceBean = CommonServerCache.PROVIDER_CLASS_MAP.get(rpcInvocation.getTargetServiceName());
        Method[] declaredMethods = serviceBean.getClass().getDeclaredMethods();
        Object response = null;
        for (Method declaredMethod : declaredMethods) {
            if(declaredMethod.getName().equals(rpcInvocation.getTargetMethod())){
                // 判断返回值
                if(declaredMethod.getReturnType().equals(Void.TYPE)){
                    declaredMethod.invoke(serviceBean,rpcInvocation.getArgs());
                }else{
                    response = declaredMethod.invoke(serviceBean,rpcInvocation.getArgs());
                }
            }
        }
        // 封装response
        rpcInvocation.setResponse(response);
        // 反序列化
        RpcProtocol protocol = new RpcProtocol(JSON.toJSONString(rpcInvocation).getBytes());
        // 返回
        ctx.writeAndFlush(protocol);
    }

    /**
     *异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if(ctx.channel().isActive()){
            ctx.close();
        }
    }
}

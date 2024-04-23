package cn.cagurzhan.core.proxy.jdk;

import cn.cagurzhan.core.common.RpcInvocation;
import cn.cagurzhan.core.common.cache.CommonClientCache;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * 客户端JDK动态代理处理器
 * @author AjaxZhan
 */
public class JDKClientInvocationHandler implements InvocationHandler {

    private static final Object OBJECT = new Object();

    private Class<?> clazz;

    public JDKClientInvocationHandler(Class<?> clazz){
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 封装远程调用 RpcInvocation
        RpcInvocation rpcInvocation = new RpcInvocation();
        rpcInvocation.setArgs(args);
        rpcInvocation.setTargetMethod(method.getName());
        rpcInvocation.setTargetServiceName(clazz.getName());
        rpcInvocation.setUuid(UUID.randomUUID().toString());
        // 设置到responseMap：uuid-OBJECT
        CommonClientCache.RESP_MAP.put(rpcInvocation.getUuid(),OBJECT);
        // 添加到发送队列
        CommonClientCache.SEND_QUEUE.add(rpcInvocation);
        // 3s内，向队列轮询结果，在一段时间内从responseMap中获取结果
        long beginTime = System.currentTimeMillis();
        while(System.currentTimeMillis() - beginTime < 3000){
            Object object = CommonClientCache.RESP_MAP.get(rpcInvocation.getUuid());
            if(object instanceof RpcInvocation){
                return ((RpcInvocation) object).getResponse();
            }
        }
        throw new TimeoutException("client wait server's response timeout!");
    }
}

package cn.cagurzhan.core.proxy.javassist;


import cn.cagurzhan.core.common.RpcInvocation;
import cn.cagurzhan.core.common.cache.CommonClientCache;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeoutException;


/**
 * @Author linhao
 */
public class JavassistInvocationHandler implements InvocationHandler {


    private final static Object OBJECT = new Object();

    private Class<?> clazz;

    public JavassistInvocationHandler(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcInvocation rpcInvocation = new RpcInvocation();
        rpcInvocation.setArgs(args);
        rpcInvocation.setTargetMethod(method.getName());
        rpcInvocation.setTargetServiceName(clazz.getName());
        rpcInvocation.setUuid(UUID.randomUUID().toString());
        CommonClientCache.RESP_MAP.put(rpcInvocation.getUuid(), OBJECT);
        //代理类内部将请求放入到发送队列中，等待发送队列发送请求
        CommonClientCache.SEND_QUEUE.add(rpcInvocation);
        long beginTime = System.currentTimeMillis();
        //如果请求数据在指定时间内返回则返回给客户端调用方
        while (System.currentTimeMillis() - beginTime < 3*1000) {
            Object object = CommonClientCache.RESP_MAP.get(rpcInvocation.getUuid());
            if (object instanceof RpcInvocation) {
                return ((RpcInvocation)object).getResponse();
            }
        }
        throw new TimeoutException("client wait server's response timeout!");
    }
}

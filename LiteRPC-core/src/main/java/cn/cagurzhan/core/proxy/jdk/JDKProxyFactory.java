package cn.cagurzhan.core.proxy.jdk;

import cn.cagurzhan.core.proxy.ProxyFactory;

import java.lang.reflect.Proxy;

/**
 * jdk动态代理工厂
 * @author AjaxZhan
 */
public class JDKProxyFactory implements ProxyFactory {
    @Override
    public <T> T getProxy(final Class<?> clazz) throws Throwable {
        return (T)Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},
                new JDKClientInvocationHandler(clazz));
    }
}

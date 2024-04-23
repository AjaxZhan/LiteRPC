package cn.cagurzhan.core.proxy.javassist;

import cn.cagurzhan.core.proxy.ProxyFactory;

/**
 * 使用Javassist字节码生成技术作为动态代理
 * @author AjaxZhan
 */
public class JavassistProxyFactory implements ProxyFactory {
    @Override
    public <T> T getProxy(Class<?> clazz) throws Throwable {
        return (T) ProxyGenerator.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                clazz, new JavassistInvocationHandler(clazz));
    }
}

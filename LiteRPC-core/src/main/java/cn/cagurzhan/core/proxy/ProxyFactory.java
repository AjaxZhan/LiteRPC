package cn.cagurzhan.core.proxy;

/**
 * 代理工厂接口
 * @author AjaxZhan
 */
public interface ProxyFactory {
    <T> T getProxy(final Class<?> clazz) throws Throwable;
}

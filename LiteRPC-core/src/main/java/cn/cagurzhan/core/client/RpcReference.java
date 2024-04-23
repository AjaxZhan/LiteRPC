package cn.cagurzhan.core.client;

import cn.cagurzhan.core.proxy.ProxyFactory;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 代理对象
 * @author AjaxZhan
 */
@AllArgsConstructor
public class RpcReference {
    private ProxyFactory proxyFactory;

    /**
     * 根据接口类型获取代理对象
     */
    public <T> T get(Class<T> tClass) throws Throwable{
        return proxyFactory.getProxy(tClass);
    }



}

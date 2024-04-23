package cn.cagurzhan.core.common.cache;

import cn.cagurzhan.core.common.RpcInvocation;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端缓存
 * @author AjaxZhan
 */
public class CommonClientCache {
    // 发送队列
    public static BlockingQueue<RpcInvocation> SEND_QUEUE = new ArrayBlockingQueue<>(100);
    // 存储响应的哈希表
    public static Map<String,Object> RESP_MAP = new ConcurrentHashMap<>();
}

package cn.cagurzhan.core.common;

import lombok.Data;

import java.util.Arrays;

/**
 * RPC调用数据包
 * @author AjaxZhan
 */
@Data
public class RpcInvocation {
    private String targetMethod;
    private String targetServiceName;
    private Object[] args;
    private String uuid;
    private Object response;
    @Override
    public String toString() {
        return "RpcInvocation{" +
                "targetMethod='" + targetMethod + '\'' +
                ", targetServiceName='" + targetServiceName + '\'' +
                ", args=" + Arrays.toString(args) +
                ", uuid='" + uuid + '\'' +
                ", response=" + response +
                '}';
    }
}

package cn.cagurzhan.core.common;

import cn.cagurzhan.core.common.constants.RpcConstant;
import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 自定义协议
 * @author AjaxZhan
 */
@Data
public class RpcProtocol implements Serializable {
    private static final long serialVersionUID = 5359096060555795690L;
    private short magicNumber = RpcConstant.MAGIC_NUMBER;
    private int contentLength;
    private byte[] content;

    public RpcProtocol(byte[] content){
        this.contentLength = content.length;
        this.content = content;
    }
    @Override
    public String toString() {
        return "RpcProtocol{" +
                "contentLength=" + contentLength +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}

package cn.cagurzhan.core.common;

import cn.cagurzhan.core.common.constants.RpcConstant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * RPC解码器
 * @author AjaxZhan
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private final int BASE_LENGTH = 2 + 4;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() > BASE_LENGTH){
            if(in.readableBytes() > 1000){
                // 过大的数据包，直接丢弃
                in.skipBytes(in.readableBytes());
            }
            // 自己解决粘包问题
            // 也可以通过Netty自带的handler来解决
            int beginReaderIdx;
            while(true){
                beginReaderIdx = in.readerIndex();
                in.markReaderIndex();
                if(in.readShort()== RpcConstant.MAGIC_NUMBER){
                    break;
                }else{
                    // 非法数据包
                    ctx.close(); // 只关闭当前handler
                    return;
                }
            }
            // 读取数据包
            int len = in.readInt();
            if(in.readableBytes() < len){
                // 不完整数据包，重置readIdx
                in.readerIndex(beginReaderIdx);
                return;
            }
            // 正常数据包
            byte[] data = new byte[len];
            in.readBytes(data);
            RpcProtocol rpcProtocol = new RpcProtocol(data);
            out.add(rpcProtocol);
        }
    }
}

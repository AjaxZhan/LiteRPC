package cn.cagurzhan.core.client;

import cn.cagurzhan.core.common.RpcDecoder;
import cn.cagurzhan.core.common.RpcEncoder;
import cn.cagurzhan.core.common.RpcInvocation;
import cn.cagurzhan.core.common.RpcProtocol;
import cn.cagurzhan.core.common.cache.CommonClientCache;
import cn.cagurzhan.core.common.config.ClientConfig;
import cn.cagurzhan.core.proxy.jdk.JDKProxyFactory;
import cn.cagurzhan.interfaces.TestService;
import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 服务消费者
 * @author AjaxZhan
 */
public class Client {
    private Logger logger = LoggerFactory.getLogger(Client.class);

    @Getter
    @Setter
    private ClientConfig clientConfig;

    /**
     * 启动客户端，返回代理对象
     * @return
     */
    public RpcReference startClientApplication(){
        // Netty NIO连接
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ChannelFuture future = bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new RpcEncoder());
                        ch.pipeline().addLast(new RpcDecoder());
                        ch.pipeline().addLast(new ClientHandler());
                    }
                })
                .connect(clientConfig.getServerHost(), clientConfig.getPort());
        logger.info("============ 服务启动 ============");
        // 开启发送线程
        startClient(future);
        // 注入代理工厂，使用JDK动态代理
        RpcReference rpcReference = new RpcReference(new JDKProxyFactory());
        return rpcReference;
    }

    /**
     * 开启发送线程
     */
    private void startClient(ChannelFuture channelFuture){
        Thread thread = new Thread(new AsyncSendJob(channelFuture));
        thread.start();
    }

    public static void main(String[] args) throws Throwable {
        Client client = new Client();
        // 配置文件
        ClientConfig config = new ClientConfig();
        config.setPort(8000);
        config.setServerHost("127.0.0.1");
        client.setClientConfig(config);
        // 启动应用，拿到代理对象
        RpcReference rpcReference = client.startClientApplication();
        // 通过代理对象 调用 远程方法，拿到响应
        TestService testService = rpcReference.get(TestService.class);
        for (int i=0;i<100;i++){
            String response = testService.sendData("test");
            System.out.println("拿到响应结果：" + response);
        }
    }

    /**
     * 异步线程用于发送数据，提高响应速度
     */
    class AsyncSendJob implements Runnable{

        private ChannelFuture channelFuture;

        public AsyncSendJob(ChannelFuture channelFuture){
            this.channelFuture = channelFuture;
        }

        @Override
        public void run() {
            while(true){
                // 从阻塞队列拿到数据
                try {
                    RpcInvocation data = CommonClientCache.SEND_QUEUE.take();
                    RpcProtocol rpcProtocol = new RpcProtocol(JSON.toJSONBytes(data));
                    channelFuture.channel().writeAndFlush(rpcProtocol);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
    }


}

package cn.cagurzhan.core.server;

import cn.cagurzhan.core.common.cache.CommonServerCache;
import cn.cagurzhan.core.common.config.ServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 服务提供者
 * @author AjaxZhan
 */
public class Server {

    private static EventLoopGroup bossGroup = null;
    private static EventLoopGroup workerGroup = null;
    private static Integer SERVER_PORT = 8000;

    @Getter
    @Setter
    private ServerConfig serverConfig;

    public void startApplication() throws InterruptedException {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true)
                .option(ChannelOption.SO_BACKLOG,1024)
                .option(ChannelOption.SO_SNDBUF,16*1024)
                .option(ChannelOption.SO_RCVBUF,16*1024)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        System.out.println("初始化provider过程");
                        // 添加编码器
                        // 添加解码器
                        // 添加服务handler
                    }
                })
                .bind(serverConfig.getPort()).sync();
    }

    public void registryService(Object serviceBean){
        Class<?>[] interfaces = serviceBean.getClass().getInterfaces();
        if(interfaces.length == 0){
            // 没有接口，无法使用JDK动态代理
            throw new RuntimeException("service must had interfaces!");
        }
        if(interfaces.length > 1){
            // 只能有一个接口
            throw new RuntimeException("service must only had one interfaces!");
        }
        // 将需要注册的服务放到哈希表管理
        CommonServerCache.PROVIDER_CLASS_MAP.put(interfaces[0].getName(),serviceBean);

    }

    public static void main(String[] args) throws InterruptedException {
        Server server = new Server();
        // 加载配置
        ServerConfig config = new ServerConfig();
        config.setPort(SERVER_PORT);
        server.setServerConfig(config);
        // 注册服务
        server.registryService(new TestServiceImpl());
        // 启动服务端
        server.startApplication();
    }
}

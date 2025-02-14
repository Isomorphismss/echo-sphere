package org.isomorphism.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.isomorphism.netty.utils.JedisPoolUtils;
import org.isomorphism.netty.utils.ZookeeperRegister;
import org.isomorphism.netty.websocket.WSServerInitializer;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ChatServer: Netty服务的启动类（服务器）
 */
public class ChatServer2 {

    public static final Integer nettyDefaultPort = 875;  // 885 895 905 915 ...
    public static final String initOnlineCounts = "0";

    public static Integer selectPort(Integer port) {

        String portKey = "netty_port";
        Jedis jedis = JedisPoolUtils.getJedis();

        Map<String, String> portMap = jedis.hgetAll(portKey);
        System.out.println(portMap);

        // 由于map中的key都应该是整数类型的port，所以先转换成整数后，再比对，否则string类型的端口比对会有问题
        List<Integer> portList = portMap
                .entrySet()
                .stream()
                .map(
                        entry ->
                                Integer.valueOf(entry.getKey())
                )
                .collect(Collectors.toList());

        System.out.println(portList);

        Integer nettyPort = null;
        if (portList == null || portList.isEmpty()) {
            jedis.hset(portKey, port + "", initOnlineCounts);
            nettyPort = port;
        } else {
            // 使用stream循环获得最大值，并且累加10（累加的数值不是强制为10的）
            Optional<Integer> maxInteger = portList.stream().max(Integer::compareTo);
            Integer maxPort = maxInteger.get().intValue();
            Integer currentPort = maxPort + 10;
            jedis.hset(portKey, currentPort + "", initOnlineCounts);
            nettyPort = currentPort;
        }

        return nettyPort;
    }

    public static void main(String[] args) throws Exception {
        // 定义主从线程组
        // 定义主线程池，用于接受客户端的连接，但是不做任何处理，比如老板会谈业务，拉到业务就会交给下面的员工去做了
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 定义从线程池，处理主线程池交过来的任务，公司业务员开展业务，完成老板交代的任务
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        // Netty服务启动的时候，从redis中查找有没有端口，如果没有，则使用875，如果有则端口累加10再启动
        Integer nettyPort = selectPort(nettyDefaultPort);

        // 注册当前netty服务到zookeeper中
        ZookeeperRegister.registerNettyServer("server-list",
                ZookeeperRegister.getLocalIp(),
                nettyPort);

        try {
            // 构建Netty服务器
            ServerBootstrap server = new ServerBootstrap();      // 服务的启动类
            server.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)       // 设置Nio的双向通道
                    .childHandler(new WSServerInitializer());  // 设置处理器，用于处理workerGroup

            // 启动server，并且绑定端口号875，同时启动方式为“同步”
            ChannelFuture channelFuture = server.bind(nettyPort).sync();

            // 监听关闭的channel
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}

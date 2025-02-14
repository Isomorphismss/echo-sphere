package org.isomorphism.netty.utils;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.isomorphism.pojo.netty.NettyServerNode;
import org.isomorphism.utils.JsonUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class ZookeeperRegister {

    public static void registerNettyServer(String nodeName,
                                           String ip,
                                           Integer port) throws Exception {

        CuratorFramework zkClient = CuratorConfig.getClient();
        String path = "/" + nodeName;
        Stat stat = zkClient.checkExists().forPath(path);
        if (stat == null) {
            zkClient.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(path);
        } else {
            System.out.println(stat);
        }

        // 创建对应的临时节点，值可以放在线人数，默认为初始化的0
        NettyServerNode serverNode = new NettyServerNode();
        serverNode.setIp(ip);
        serverNode.setPort(port);
        String nodeJson = JsonUtils.objectToJson(serverNode);

        zkClient.create()
                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath(path + "/im-", nodeJson.getBytes());

    }

    public static String getLocalIp() throws Exception {
        InetAddress address = InetAddress.getLocalHost();
        String ip = address.getHostAddress();
        System.out.println("本机IP地址：" + ip);
        return ip;
    }

    /**
     * 增加在线人数（累加）
     * @param serverNode
     */
    public static void incrementOnlineCounts(NettyServerNode serverNode) throws Exception {
        dealOnlineCounts(serverNode, 1);
    }

    /**
     * 减少在线人数（累减）
     * @param serverNode
     */
    public static void decrementOnlineCounts(NettyServerNode serverNode) throws Exception {
        dealOnlineCounts(serverNode, -1);
    }

    /**
     * 处理在线人数的增减
     * @param serverNode
     * @param counts
     */
    public static void dealOnlineCounts(NettyServerNode serverNode,
                                        Integer counts) throws Exception {
        CuratorFramework zkClient = CuratorConfig.getClient();
        String path = "/server-list";
        List<String> list = zkClient.getChildren().forPath(path);

        for (String node : list) {
            String nodePath = path + "/" + node;
            String nodeValue = new String(zkClient.getData().forPath(nodePath));

            NettyServerNode pendingNode = JsonUtils.jsonToPojo(nodeValue,
                                                                NettyServerNode.class);
            if ((pendingNode.getIp().equals(serverNode.getIp())) &&
                    (pendingNode.getPort().intValue() == serverNode.getPort().intValue())
            ) {
                pendingNode.setOnlineCounts(pendingNode.getOnlineCounts() + counts);
                String nodeJson = JsonUtils.objectToJson(pendingNode);
                zkClient.setData().forPath(nodePath, nodeJson.getBytes());
            }
        }

    }

}

package org.isomorphism.netty.utils;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.net.InetAddress;
import java.net.UnknownHostException;

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

    }

    public static String getLocalIp() throws Exception {
        InetAddress address = InetAddress.getLocalHost();
        String ip = address.getHostAddress();
        System.out.println("本机IP地址：" + ip);
        return ip;
    }

}

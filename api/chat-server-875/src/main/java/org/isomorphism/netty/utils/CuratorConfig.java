package org.isomorphism.netty.utils;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class CuratorConfig {

    private static final Dotenv dotenv = Dotenv.load();
    private static String zooHost = dotenv.get("ZOO_HOST");
    private static String zooPort = dotenv.get("ZOO_PORT");
    private static String zooNamespace = dotenv.get("ZOO_NAMESPACE");

    private static String host = zooHost + ":" + zooPort;  // 单机/集群ip:port
    private static Integer connectionTimeoutMs = 30 * 1000; // 连接超时时间
    private static Integer sessionTimeoutMs = 3 * 1000; // 会话连接超时时间
    private static Integer sleepMsBetweenRetry = 2 * 1000;  // 每次重试的间隔时间
    private static Integer maxRetries = 3;  // 最大重试次数
    private static String namespace = zooNamespace; // 命名空间（root根节点名称）

    // Curator的客户端
    private static CuratorFramework client;

    static {
        // 定义重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(sleepMsBetweenRetry, maxRetries);

        // 声明初始化客户端
        client = CuratorFrameworkFactory.builder()
                .connectString(host)
                .connectionTimeoutMs(connectionTimeoutMs)
                .sessionTimeoutMs(sessionTimeoutMs)
                .retryPolicy(retryPolicy)
                .namespace(namespace)
                .build();
        client.start();   // 启动curator客户端
    }

    public static CuratorFramework getClient() {
        return client;
    }

}

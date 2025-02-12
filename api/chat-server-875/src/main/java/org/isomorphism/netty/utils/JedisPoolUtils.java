package org.isomorphism.netty.utils;

import io.github.cdimascio.dotenv.Dotenv;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

/**
 * Jedis连接池工具类
 */
public class JedisPoolUtils {

    private static final JedisPool jedisPool;

    // 加载dotenv环境文件
    private static final Dotenv dotenv = Dotenv.load();

    private static String host = dotenv.get("REDIS_HOST");
    private static int port = Integer.parseInt(dotenv.get("REDIS_PORT"));
    private static int timeout = 1000;
    private static String password = dotenv.get("REDIS_PASSWORD");

    static {
        // 配置连接池
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        // 最大连接数
        poolConfig.setMaxTotal(10);
        // 最大空闲连接数
        poolConfig.setMaxIdle(10);
        // 最小空闲连接数
        poolConfig.setMinIdle(5);
        // 最长等待时间，ms
//        poolConfig.setMaxWaitMillis(1500);
        poolConfig.setMaxWait(Duration.ofMillis(1500));

        jedisPool = new JedisPool(poolConfig,
                host,
                port,
                timeout,
                password);
    }

    public static Jedis getJedis() {
        return jedisPool.getResource();
    }

//    public static void main(String[] args) {
//        String key = "testJedis";
//        Jedis jedis = JedisPoolUtils.getJedis();
//        jedis.set(key, "hello Jedis~~!!!");
//        String cacheValue = jedis.get(key);
//        System.out.println(cacheValue);
//    }

}

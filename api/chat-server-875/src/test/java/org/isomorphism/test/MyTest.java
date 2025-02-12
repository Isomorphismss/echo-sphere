package org.isomorphism.test;

import org.isomorphism.netty.utils.JedisPoolUtils;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

public class MyTest {

    @Test
    public void testJedisPool() {
        String key = "testJedis";
        Jedis jedis = JedisPoolUtils.getJedis();
        jedis.set(key, "hello Jedis~~!!!");
        String cacheValue = jedis.get(key);
        System.out.println(cacheValue);
    }

}

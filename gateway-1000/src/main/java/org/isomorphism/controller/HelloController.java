package org.isomorphism.controller;

import jakarta.annotation.Resource;
import org.isomorphism.base.BaseInfoProperties;
import org.isomorphism.utils.RedisOperator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("g")
public class HelloController extends BaseInfoProperties {

//    @Resource
//    private RedisOperator redis;

    // 127.0.0.1:1000/g/hello

    @GetMapping("hello")
    public Object hello() {
        return "Hello World";
    }

    @GetMapping("setRedis")
    public Object setRedis(String k, String v) {
        redis.set(k, v);
        return "setRedis OK~";
    }

    @GetMapping("getRedis")
    public Object getRedis(String k) {
        return redis.get(k);
    }

}

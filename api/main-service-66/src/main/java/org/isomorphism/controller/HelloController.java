package org.isomorphism.controller;

import jakarta.annotation.Resource;
import org.isomorphism.pojo.netty.ChatMsg;
import org.isomorphism.rabbitmq.RabbitMQTestConfig;
import org.isomorphism.utils.JsonUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("m")
public class HelloController {

    // 127.0.0.1:77/a/hello

    @GetMapping("hello")
    public Object hello() {
        return "Hello World";
    }

    @Resource
    private RabbitTemplate rabbitTemplate;

    @GetMapping("mq")
    public Object ok() {

        ChatMsg chatMsg = new ChatMsg();
        chatMsg.setMsg("hello world");
        String msg = JsonUtils.objectToJson(chatMsg);

        rabbitTemplate.convertAndSend(
                RabbitMQTestConfig.TEST_EXCHANGE,
                RabbitMQTestConfig.ROUTING_KEY_TEST_SEND,
                msg
        );

        return "ok";
    }

}

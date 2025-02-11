package org.isomorphism.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.isomorphism.pojo.netty.ChatMsg;
import org.isomorphism.utils.JsonUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

//@Component
@Slf4j
public class RabbitMQConsumer {

    @RabbitListener(queues = {RabbitMQTestConfig.TEST_QUEUE})
    public void watchQueue(String payload, Message message) {
//        log.info("payload = " + payload);

        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        log.info("routingKey = " + routingKey);

        if (routingKey.equals(RabbitMQTestConfig.ROUTING_KEY_TEST_SEND)) {
            String msg = payload;
            ChatMsg chatMsg = JsonUtils.jsonToPojo(msg, ChatMsg.class);
            log.info(chatMsg.toString());
        }

    }

}

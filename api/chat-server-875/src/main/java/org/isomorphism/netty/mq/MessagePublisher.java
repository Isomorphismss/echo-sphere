package org.isomorphism.netty.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.github.cdimascio.dotenv.Dotenv;
import org.isomorphism.pojo.netty.ChatMsg;
import org.isomorphism.utils.JsonUtils;

public class MessagePublisher {

    // 定义交换机的名字
    public static final String TEST_EXCHANGE = "test_exchange";

    // 定义队列的名字
    public static final String TEST_QUEUE = "test_queue";

    // 发送信息到消息队列接受并且保存到数据库的路由地址
    public static final String ROUTING_KEY_WECHAT_MSG_SEND = "echo.sphere.wechat.msg.send";

    // 加载 .env 文件
    private static final Dotenv dotenv = Dotenv.load();

    // 读取环境变量
    private static final String RABBITMQ_HOST = dotenv.get("RABBITMQ_HOST");
    private static final int RABBITMQ_PORT = Integer.parseInt(dotenv.get("RABBITMQ_PORT"));
    private static final String RABBITMQ_USERNAME = dotenv.get("RABBITMQ_USERNAME");
    private static final String RABBITMQ_PASSWORD = dotenv.get("RABBITMQ_PASSWORD");
    private static final String RABBITMQ_VIRTUAL_HOST = dotenv.get("RABBITMQ_VIRTUAL_HOST");

//    public static void saveMessageByMQ(String msg) throws Exception {
//
//        // 1. 创建连接工厂
//        ConnectionFactory factory = new ConnectionFactory();
//
//        // 1.1 设置连接参数（使用 .env 配置）
//        factory.setHost(RABBITMQ_HOST);
//        factory.setPort(RABBITMQ_PORT);
//        factory.setVirtualHost(RABBITMQ_VIRTUAL_HOST);
//        factory.setUsername(RABBITMQ_USERNAME);
//        factory.setPassword(RABBITMQ_PASSWORD);
//
//        // 1.2 建立连接
//        Connection connection = factory.newConnection();
//
//        // 2. 创建通道channel
//        Channel channel = connection.createChannel();
//
//        // 3. 定义队列
//        channel.queueDeclare(QUEUE_MSG,
//                         true,
//                         false,
//                         false,
//                         null);
//        // 4. 发送消息
//        channel.basicPublish(EXCHANGE_MSG,
//                         SAVE_MSG_ROUTING_KEY,
//                         null,
//                         msg.getBytes());
//
//        // 5. 关闭通道和连接
//        channel.close();
//        connection.close();
//    }
//
//    public static void main(String[] args) throws Exception {
//        //saveMessageByMQ("Send a chat msg by rabbitmq~~~  another  ");
//
//        RabbitMQConnectUtils connectUtils = new RabbitMQConnectUtils();
//        String msg = "Send a chat msg by rabbitmq~~~  new ";
//
//        ChatMsg chatMsg = new ChatMsg();
//        chatMsg.setMsgId("1001");
//        chatMsg.setMsg("123xyz");
//        // String pendingMsg = GsonUtils.object2String(chatMsg);
//        String pendingMsg = JsonUtils.objectToJson(chatMsg);
//
//        connectUtils.sendMsg(pendingMsg, EXCHANGE_MSG, SAVE_MSG_ROUTING_KEY);
//    }

    public static void sendMsgToSave(ChatMsg msg) throws Exception {
        RabbitMQConnectUtils connectUtils = new RabbitMQConnectUtils();
        connectUtils.sendMsg(JsonUtils.objectToJson(msg),
                TEST_EXCHANGE,
                ROUTING_KEY_WECHAT_MSG_SEND);
    }

    public static void sendMsgToNettyServers(String msg) throws Exception {
        RabbitMQConnectUtils connectUtils = new RabbitMQConnectUtils();
        String fanoutExchange = "fanout_exchange";
        connectUtils.sendMsg(msg, fanoutExchange, "");
    }

}

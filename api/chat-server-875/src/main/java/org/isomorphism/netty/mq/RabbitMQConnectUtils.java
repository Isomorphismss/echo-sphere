package org.isomorphism.netty.mq;

import com.rabbitmq.client.*;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RabbitMQConnectUtils {

    private final List<Connection> connections = new ArrayList<>();
    private final int maxConnection = 20;

    // 加载 .env 文件
    private static final Dotenv dotenv = Dotenv.load();

    // 读取环境变量
    private final String host = dotenv.get("RABBITMQ_HOST");
    private final int port = Integer.parseInt(dotenv.get("RABBITMQ_PORT"));
    private final String username = dotenv.get("RABBITMQ_USERNAME");
    private final String password = dotenv.get("RABBITMQ_PASSWORD");
    private final String virtualHost = dotenv.get("RABBITMQ_VIRTUAL_HOST");

    // 生产环境 prod
    //private final String host = "";
    //private final int port = 5672;
    //private final String username = "123";
    //private final String password = "123";
    //private final String virtualHost = "123";

    public ConnectionFactory factory;

    public ConnectionFactory getRabbitMqConnection() {
        return getFactory();
    }

    public ConnectionFactory getFactory() {
        initFactory();
        return factory;
    }

    private void initFactory() {
        try {
            if (factory == null) {
                factory = new ConnectionFactory();
                factory.setHost(host);
                factory.setPort(port);
                factory.setUsername(username);
                factory.setPassword(password);
                factory.setVirtualHost(virtualHost);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String message, String queue) throws Exception {
        Connection connection = getConnection();
        Channel channel = connection.createChannel();
        channel.basicPublish("",
                            queue,
                            MessageProperties.PERSISTENT_TEXT_PLAIN,
                            message.getBytes("utf-8"));
        channel.close();
        setConnection(connection);
    }

    public void sendMsg(String message, String exchange, String routingKey) throws Exception {
        Connection connection = getConnection();
        Channel channel = connection.createChannel();
        channel.basicPublish(exchange,
                            routingKey,
                            MessageProperties.PERSISTENT_TEXT_PLAIN,
                            message.getBytes("utf-8"));
        channel.close();
        setConnection(connection);
    }

    public GetResponse basicGet(String queue, boolean autoAck) throws Exception {
        GetResponse getResponse = null;
        Connection connection = getConnection();
        Channel channel = connection.createChannel();
        getResponse = channel.basicGet(queue, autoAck);
        channel.close();
        setConnection(connection);
        return getResponse;
    }

    public Connection getConnection() throws Exception {
        return getAndSetConnection(true, null);
    }

    public void setConnection(Connection connection) throws Exception {
        getAndSetConnection(false, connection);
    }

    public void listen(String exchangeName, String queueName) throws Exception {

        Connection connection = getConnection();
        Channel channel = connection.createChannel();

        // 定义交换机 FANOUT 发布订阅模式（广播模式）
        channel.exchangeDeclare(exchangeName,
                BuiltinExchangeType.FANOUT,
                true,
                false,
                null);

        // 定义队列
        channel.queueDeclare(queueName,
                true,
                false,
                false,
                null);

        // 把队列绑定到交换机
        channel.queueBind(queueName, exchangeName, "");

        Consumer consumer = new DefaultConsumer(channel) {
            /**
             * 重写消息配送（交付）方法
             * @param consumerTag 消息的标签（标识）
             * @param envelope 信封（一些消息，比如交换机路由等信息）
             * @param properties 配置信息和内容
             * @param body 收到的消息数据内容
             * @throws IOException
             */
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String exchange = envelope.getExchange();
                if (exchange.equalsIgnoreCase(exchangeName)) {
                    String msg = new String(body);
                    System.out.println(msg);
                }
            }
        };

        /**
         * queue: 监听的队列名称
         * autoAck：是否自动确认，true：告知mq消费者已经消费的确认通知
         * callback：回调函数，处理监听到的消息。
         */
        channel.basicConsume(queueName, true, consumer);

    }

    private synchronized Connection getAndSetConnection(boolean isGet, Connection connection) throws Exception {
        getRabbitMqConnection();

        if (isGet) {
            if (connections.isEmpty()) {
                return factory.newConnection();
            }
            Connection newConnection = connections.get(0);
            connections.remove(0);
            if (newConnection.isOpen()) {
                return newConnection;
            } else {
                return factory.newConnection();
            }
        } else {
            if (connections.size() < maxConnection) {
                connections.add(connection);
            }
            return null;
        }
    }

}

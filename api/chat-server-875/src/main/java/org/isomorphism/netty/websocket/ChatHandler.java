package org.isomorphism.netty.websocket;

import com.a3test.component.idworker.IdWorkerConfigBean;
import com.a3test.component.idworker.Snowflake;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.isomorphism.enums.MsgTypeEnum;
import org.isomorphism.grace.result.GraceJSONResult;
import org.isomorphism.netty.mq.MessagePublisher;
import org.isomorphism.netty.utils.JedisPoolUtils;
import org.isomorphism.netty.utils.ZookeeperRegister;
import org.isomorphism.pojo.netty.ChatMsg;
import org.isomorphism.pojo.netty.DataContent;
import org.isomorphism.pojo.netty.NettyServerNode;
import org.isomorphism.utils.JsonUtils;
import org.isomorphism.utils.LocalDateUtils;
import org.isomorphism.utils.OkHttpUtil;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建自定义助手类
 */
// SimpleChannelInboundHandler: 对于请求来说，相当于入站（入境）
// TextWebSocketFrame: 用于为websocket专门处理的文本数据对象，Frame是数据（消息）的载体
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    // 用于记录和管理所有客户端的channel组
    public static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                TextWebSocketFrame msg) throws Exception {
        // 获得客户端传输过来的消息
        String content = msg.text();
        System.out.println("接收到的数据：" + content);

        // 1. 获取客户端发来的消息并且解析
        DataContent dataContent = JsonUtils.jsonToPojo(content, DataContent.class);
        ChatMsg chatMsg = dataContent.getChatMsg();

        String msgText = chatMsg.getMsg();
        String receiverId = chatMsg.getReceiverId();
        String senderId = chatMsg.getSenderId();

        // 判断是否黑名单 start
        // 如果双方只要有一方是黑名单，则终止发送
        GraceJSONResult result = OkHttpUtil.get("http://127.0.0.1:1000/friendship/isBlack?friendId1st=" + receiverId
                            + "&friendId2nd=" + senderId);
        boolean isBlack = (Boolean) result.getData();
        System.out.println("当前的黑名单关系为：" + isBlack);
        if (isBlack) {
            return;
        }
        // 判断是否黑名单 end

        // 时间校准，以服务器的时间为准
        chatMsg.setChatTime(LocalDateTime.now());

        Integer msgType = chatMsg.getMsgType();

        // 获取channel
        Channel currentChannel = ctx.channel();
        String currentChannelId = currentChannel.id().asLongText();
        String currentChannelIdShort = currentChannel.id().asShortText();

//        System.out.println("客户端currentChannelId: "
//                + currentChannelId
//                + ", currentChannelIdShort: "
//                + currentChannelIdShort
//        );

        // 2. 判断消息类型，根据不同的类型来处理不同的业务
        if (msgType == MsgTypeEnum.CONNECT_INIT.type) {
            // 当websocket初次open的时候，初始化channel，把channel和用户userid关联起来
            UserChannelSession.putMultiChannels(senderId, currentChannel);
            UserChannelSession.putUserChannelIdRelation(currentChannelId, senderId);

            // 初次连接后，该节点下的在线人数累加
            NettyServerNode minNode = dataContent.getServerNode();
            ZookeeperRegister.incrementOnlineCounts(minNode);

            // 获得ip和端口，在redis中设置关系，以便在前端设备断线后减少在线人数
            Jedis jedis = JedisPoolUtils.getJedis();
            jedis.set(senderId, JsonUtils.objectToJson(minNode));

        } else if (msgType == MsgTypeEnum.WORDS.type
                || msgType == MsgTypeEnum.IMAGE.type
                || msgType == MsgTypeEnum.VIDEO.type
                || msgType == MsgTypeEnum.VOICE.type
        ) {

            // 此处为mq异步解耦，保存信息到数据库，数据库无法获得信息的主键i
            // 所以此处可以用snowflake直接生成唯一的主键id
            Snowflake snowflake = new Snowflake(new IdWorkerConfigBean());
            String sid = snowflake.nextId();
            System.out.println("sid = " + sid);

            String iid = IdWorker.getIdStr();
            System.out.println("iid = " + iid);

            chatMsg.setMsgId(sid);

            // 发送消息
//            List<Channel> receiverChannels = UserChannelSession.getMultiChannels(receiverId);
//            if (receiverChannels == null || receiverChannels.isEmpty()) {
//                // receiverChannels为空，表示用户离线/断线状态，消息不需要发送，后续可以存储到数据库
//                chatMsg.setIsReceiverOnLine(false);
//            } else {
//                chatMsg.setIsReceiverOnLine(true);

                // 当receiverChannels不为空的时候，同账户多端设备接受消息
//                for (Channel c : receiverChannels) {
//                    Channel findChannel = clients.find(c.id());
//                    if (findChannel != null) {

                        if (msgType == MsgTypeEnum.VOICE.type) {
                            chatMsg.setIsRead(false);
                        }

                        dataContent.setChatMsg(chatMsg);
                        String chatTimeFormat = LocalDateUtils
                                .format(chatMsg.getChatTime(), LocalDateUtils.DATETIME_PATTERN_2);
                        dataContent.setChatTime(chatTimeFormat);

                        // 使用扩展字段，填入当前需要被排除发送的channelId
                        dataContent.setExtend(currentChannelId);

                        // 把聊天信息作为mq消息进行广播
                        MessagePublisher.sendMsgToNettyServers(JsonUtils.objectToJson(dataContent));
                        // 发送消息给在线的用户
//                        findChannel.writeAndFlush(
//                                new TextWebSocketFrame(
//                                        JsonUtils.objectToJson(dataContent))
//                        );
//                    }
//                }
//            }

            // 把聊天信息作为mq的消息发送给消费者进行消费处理（保存到数据库）
            MessagePublisher.sendMsgToSave(chatMsg);
        }

//        List<Channel> myOtherChannels = UserChannelSession
//                .getMyOtherChannels(senderId, currentChannelId);
//        for (Channel c : myOtherChannels) {
//            Channel findChannel = clients.find(c.id());
//            if (findChannel != null) {
//                dataContent.setChatMsg(chatMsg);
//                String chatTimeFormat = LocalDateUtils
//                        .format(chatMsg.getChatTime(), LocalDateUtils.DATETIME_PATTERN_2);
//                dataContent.setChatTime(chatTimeFormat);
//                // 同步消息给在线的其他设备端
//                findChannel.writeAndFlush(
//                        new TextWebSocketFrame(
//                                JsonUtils.objectToJson(dataContent))
//                );
//            }
//        }

//        // 把聊天信息作为mq的消息发送给消费者进行消费处理（保存到数据库）
//        System.out.println("🔹 正在向 MQ 发送消息：" + JsonUtils.objectToJson(chatMsg));
//        if (chatMsg.getMsgType() != MsgTypeEnum.CONNECT_INIT.type) {
//            MessagePublisher.sendMsgToSave(chatMsg);
//        }

        UserChannelSession.outputMulti();

//        currentChannel.writeAndFlush(new TextWebSocketFrame(currentChannelId));
    }

    /**
     * 客户端连接到服务端之后（打开链接）
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel currentChannel = ctx.channel();
        String currentChannelId = currentChannel.id().asLongText();
        System.out.println("客户端建立连接，channel对应的长id为：" + currentChannelId);

        // 获得客户端的channel，并且存入到ChannelGroup中进行管理（作为一个客户端群组）
        clients.add(currentChannel);
    }

    /**
     * 关闭连接，移除channel
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel currentChannel = ctx.channel();
        String currentChannelId = currentChannel.id().asLongText();
        System.out.println("客户端关闭连接，channel对应的长id为：" + currentChannelId);

        // 移除多余的会话
        String userId = UserChannelSession.getUserIdByChannelId(currentChannelId);
        UserChannelSession.removeUselessChannels(userId, currentChannelId);

        clients.remove(currentChannel);

        // zk中在线人数累减
        Jedis jedis = JedisPoolUtils.getJedis();
        NettyServerNode minNode = JsonUtils.jsonToPojo(jedis.get(userId),
                                                        NettyServerNode.class);
        ZookeeperRegister.decrementOnlineCounts(minNode);
    }

    /**
     * 发生异常并且捕获，移除channel
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel currentChannel = ctx.channel();
        String currentChannelId = currentChannel.id().asLongText();
        System.out.println("发生异常捕获，channel对应的长id为：" + currentChannelId);

        // 发生异常之后关闭连接（关闭channel）
        ctx.channel().close();
        // 随后从ChannelGroup中移除对应的channel
        clients.remove(currentChannel);

        // 移除多余的会话
        String userId = UserChannelSession.getUserIdByChannelId(currentChannelId);
        UserChannelSession.removeUselessChannels(userId, currentChannelId);

        // zk中在线人数累减
        Jedis jedis = JedisPoolUtils.getJedis();
        NettyServerNode minNode = JsonUtils.jsonToPojo(jedis.get(userId),
                NettyServerNode.class);
        ZookeeperRegister.decrementOnlineCounts(minNode);
    }

}

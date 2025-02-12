package org.isomorphism.service;

import org.isomorphism.pojo.netty.ChatMsg;
import org.isomorphism.utils.PagedGridResult;

public interface ChatMessageService {

    /**
     * 保存聊天信息
     * @param msg
     */
   public void saveMsg(ChatMsg msg);

    /**
     * 查询聊天信息列表
     * @param senderId
     * @param receiverId
     * @param page
     * @param pageSize
     * @return
     */
   public PagedGridResult queryChatMsgList(String senderId,
                                           String receiverId,
                                           Integer page,
                                           Integer pageSize);

    /**
     * 标记语音聊天信息的签收已读
     * @param msgId
     */
   public void updateMsgSignRead(String msgId);

}

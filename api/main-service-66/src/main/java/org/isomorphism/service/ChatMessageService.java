package org.isomorphism.service;

import org.isomorphism.pojo.netty.ChatMsg;

public interface ChatMessageService {

    /**
     * 保存聊天信息
     * @param msg
     */
   public void saveMsg(ChatMsg msg);

}

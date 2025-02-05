package org.isomorphism.service;

import org.isomorphism.pojo.bo.FriendCircleBO;

public interface FriendCircleService {

    /**
     * 发布朋友圈图文数据，保存到数据库
     * @param friendCircleBO
     */
    public void publish(FriendCircleBO friendCircleBO);

}

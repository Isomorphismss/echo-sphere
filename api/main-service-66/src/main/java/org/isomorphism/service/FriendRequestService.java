package org.isomorphism.service;

import org.isomorphism.pojo.Users;
import org.isomorphism.pojo.bo.ModifyUserBO;
import org.isomorphism.pojo.bo.NewFriendRequestBO;

/**
 * <p>
 * 好友请求 服务类
 * </p>
 *
 * @author isomorphism
 * @since 2025-01-29
 */
public interface FriendRequestService {

    /**
     * 新增添加好友的请求
     * @param friendRequestBO
     */
    public void addNewRequest(NewFriendRequestBO friendRequestBO);

}

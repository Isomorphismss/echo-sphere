package org.isomorphism.service;

import org.isomorphism.pojo.Users;
import org.isomorphism.pojo.bo.ModifyUserBO;
import org.isomorphism.pojo.bo.NewFriendRequestBO;
import org.isomorphism.utils.PagedGridResult;

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

    /**
     * 查询新朋友的请求列表
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryNewFriendList(String userId,
                                              Integer page,
                                              Integer pageSize);

}

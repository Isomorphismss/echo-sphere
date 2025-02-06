package org.isomorphism.service;

import org.isomorphism.pojo.bo.FriendCircleBO;
import org.isomorphism.utils.PagedGridResult;
import org.springframework.web.bind.annotation.RequestParam;

public interface FriendCircleService {

    /**
     * 发布朋友圈图文数据，保存到数据库
     * @param friendCircleBO
     */
    public void publish(FriendCircleBO friendCircleBO);

    /**
     * 分页查询朋友圈图文列表
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryList(String userId,
                                     Integer page,
                                     Integer pageSize);

    /**
     * 点赞朋友圈
     * @param friendCircleId
     * @param userId
     */
    public void like(String friendCircleId, String userId);

    /**
     * 取消（删除）点赞朋友圈
     * @param friendCircleId
     * @param userId
     */
    public void unlike(String friendCircleId, String userId);

}

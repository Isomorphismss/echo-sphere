package org.isomorphism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.isomorphism.base.BaseInfoProperties;
import org.isomorphism.enums.FriendRequestVerifyStatus;
import org.isomorphism.mapper.FriendRequestMapper;
import org.isomorphism.pojo.FriendRequest;
import org.isomorphism.pojo.bo.NewFriendRequestBO;
import org.isomorphism.service.FriendRequestService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author isomorphism
 * @since 2025-01-29
 */
@Service
public class FriendRequestServiceImpl extends BaseInfoProperties implements FriendRequestService {

    @Resource
    private FriendRequestMapper friendRequestMapper;

    @Transactional
    @Override
    public void addNewRequest(NewFriendRequestBO friendRequestBO) {
        // 先删除以前的记录
        QueryWrapper<FriendRequest> deleteWrapper = new QueryWrapper<FriendRequest>()
                .eq("my_id", friendRequestBO.getMyId())
                .eq("friend_id", friendRequestBO.getFriendId());
        friendRequestMapper.delete(deleteWrapper);

        // 再新增记录
        FriendRequest pendingFriendRequest = new FriendRequest();
        BeanUtils.copyProperties(friendRequestBO, pendingFriendRequest);
        pendingFriendRequest.setVerifyStatus(FriendRequestVerifyStatus.WAIT.type);
        pendingFriendRequest.setRequestTime(LocalDateTime.now());

        friendRequestMapper.insert(pendingFriendRequest);
    }

}

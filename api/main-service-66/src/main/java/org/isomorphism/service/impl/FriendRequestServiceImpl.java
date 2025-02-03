package org.isomorphism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.isomorphism.base.BaseInfoProperties;
import org.isomorphism.enums.FriendRequestVerifyStatus;
import org.isomorphism.mapper.FriendRequestMapper;
import org.isomorphism.mapper.FriendRequestMapperCustom;
import org.isomorphism.pojo.FriendRequest;
import org.isomorphism.pojo.bo.NewFriendRequestBO;
import org.isomorphism.pojo.vo.NewFriendsVO;
import org.isomorphism.service.FriendRequestService;
import org.isomorphism.utils.PagedGridResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

    @Resource
    private FriendRequestMapperCustom friendRequestMapperCustom;

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

    @Override
    public PagedGridResult queryNewFriendList(String userId,
                                              Integer page,
                                              Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("mySelfId", userId);

        Page<NewFriendsVO> pageInfo = new Page<>(page, pageSize);
        friendRequestMapperCustom.queryNewFriendList(pageInfo, map);

        return setterPagedGridPlus(pageInfo);
    }

}

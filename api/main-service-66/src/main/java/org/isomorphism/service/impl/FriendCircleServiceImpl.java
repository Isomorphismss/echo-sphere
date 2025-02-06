package org.isomorphism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.isomorphism.base.BaseInfoProperties;
import org.isomorphism.mapper.FriendCircleLikedMapper;
import org.isomorphism.mapper.FriendCircleMapper;
import org.isomorphism.mapper.FriendCircleMapperCustom;
import org.isomorphism.pojo.FriendCircle;
import org.isomorphism.pojo.FriendCircleLiked;
import org.isomorphism.pojo.Users;
import org.isomorphism.pojo.bo.FriendCircleBO;
import org.isomorphism.pojo.vo.FriendCircleVO;
import org.isomorphism.service.FriendCircleService;
import org.isomorphism.service.UsersService;
import org.isomorphism.utils.PagedGridResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class FriendCircleServiceImpl extends BaseInfoProperties implements FriendCircleService {

    @Resource
    private FriendCircleMapper friendCircleMapper;

    @Resource
    private FriendCircleMapperCustom friendCircleMapperCustom;

    @Resource
    private UsersService usersService;

    @Resource
    private FriendCircleLikedMapper circleLikedMapper;

    @Transactional
    @Override
    public void publish(FriendCircleBO friendCircleBO) {
        FriendCircle pendingFriendCircle = new FriendCircle();

        BeanUtils.copyProperties(friendCircleBO, pendingFriendCircle);

        friendCircleMapper.insert(pendingFriendCircle);
    }

    @Override
    public PagedGridResult queryList(String userId,
                                     Integer page,
                                     Integer pageSize) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", userId);

        // 设置分页参数
        Page<FriendCircleVO> pageInfo = new Page<>(page, pageSize);
        friendCircleMapperCustom.queryFriendCircleList(pageInfo, map);

        return setterPagedGridPlus(pageInfo);
    }

    @Transactional
    @Override
    public void like(String friendCircleId, String userId) {
        // 根据朋友圈的主键ID查询归属人（发布人）
        FriendCircle friendCircle = this.selectFriendCircle(friendCircleId);

        // 根据用户主键ID查询点赞人
        Users users = usersService.getById(userId);

        FriendCircleLiked circleLiked = new FriendCircleLiked();
        circleLiked.setFriendCircleId(friendCircleId);
        circleLiked.setBelongUserId(friendCircle.getUserId());
        circleLiked.setLikedUserId(userId);
        circleLiked.setLikedUserName(users.getNickname());
        circleLiked.setCreatedTime(LocalDateTime.now());

        circleLikedMapper.insert(circleLiked);

        // 点赞过后，朋友圈的对应点赞数累加1
        redis.increment(REDIS_FRIEND_CIRCLE_LIKED_COUNTS + ":" + friendCircleId, 1);

        // 标记哪个用户点赞过该朋友圈
        redis.setnx(REDIS_DOES_USER_LIKE_FRIEND_CIRCLE + ":" + friendCircleId + ":" + userId, userId);
    }

    @Transactional
    @Override
    public void unlike(String friendCircleId, String userId) {
        // 从数据库中删除点赞关系
        QueryWrapper<FriendCircleLiked> deleteWrapper = new QueryWrapper<FriendCircleLiked>()
                .eq("friend_circle_id", friendCircleId)
                .eq("liked_user_id", userId);
        circleLikedMapper.delete(deleteWrapper);

        // 取消点赞过后，朋友圈的对应点赞数累减1
        redis.decrement(REDIS_FRIEND_CIRCLE_LIKED_COUNTS + ":" + friendCircleId, 1);

        // 删除标记的那个用户点赞过的朋友圈
        redis.del(REDIS_DOES_USER_LIKE_FRIEND_CIRCLE + ":" + friendCircleId + ":" + userId);
    }

    private FriendCircle selectFriendCircle(String friendCircleId) {
        return friendCircleMapper.selectById(friendCircleId);
    }

}

package org.isomorphism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.isomorphism.base.BaseInfoProperties;
import org.isomorphism.enums.YesOrNo;
import org.isomorphism.mapper.FriendshipMapper;
import org.isomorphism.mapper.FriendshipMapperCustom;
import org.isomorphism.pojo.Friendship;
import org.isomorphism.pojo.vo.ContactsVO;
import org.isomorphism.service.FriendshipService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FriendshipServiceImpl extends BaseInfoProperties implements FriendshipService {

    @Resource
    private FriendshipMapper friendshipMapper;

    @Resource
    private FriendshipMapperCustom friendshipMapperCustom;

    @Override
    public Friendship getFriendship(String myId, String friendId) {
        QueryWrapper<Friendship> queryWrapper = new QueryWrapper<Friendship>()
                                                    .eq("my_id", myId)
                                                    .eq("friend_id", friendId);
        return friendshipMapper.selectOne(queryWrapper);
    }

    @Override
    public List<ContactsVO> queryMyFriends(String myId) {
        Map<String, Object> map = new HashMap<>();
        map.put("myId", myId);
        return friendshipMapperCustom.queryMyFriends(map);
    }

    @Transactional
    @Override
    public void updateFriendRemark(String myId,
                                   String friendId,
                                   String friendRemark) {
        QueryWrapper<Friendship> updateWrapper = new QueryWrapper<>();
        updateWrapper.eq("my_id", myId);
        updateWrapper.eq("friend_id", friendId);

        Friendship friendship = new Friendship();
        friendship.setFriendRemark(friendRemark);
        friendship.setUpdatedTime(LocalDateTime.now());

        friendshipMapper.update(friendship, updateWrapper);
    }

    @Transactional
    @Override
    public void updateBlackList(String myId,
                                String friendId,
                                YesOrNo yesOrNo) {
        QueryWrapper<Friendship> updateWrapper = new QueryWrapper<>();
        updateWrapper.eq("my_id", myId);
        updateWrapper.eq("friend_id", friendId);

        Friendship friendship = new Friendship();
        friendship.setIsBlack(yesOrNo.type);
        friendship.setUpdatedTime(LocalDateTime.now());

        friendshipMapper.update(friendship, updateWrapper);
    }

}

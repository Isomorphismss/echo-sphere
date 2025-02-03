package org.isomorphism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.isomorphism.base.BaseInfoProperties;
import org.isomorphism.mapper.FriendshipMapper;
import org.isomorphism.mapper.FriendshipMapperCustom;
import org.isomorphism.pojo.Friendship;
import org.isomorphism.pojo.vo.ContactsVO;
import org.isomorphism.service.FriendshipService;
import org.springframework.stereotype.Service;

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

}

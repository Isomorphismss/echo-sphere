package org.isomorphism.service.impl;

import jakarta.annotation.Resource;
import org.isomorphism.base.BaseInfoProperties;
import org.isomorphism.mapper.FriendCircleMapper;
import org.isomorphism.pojo.FriendCircle;
import org.isomorphism.pojo.bo.FriendCircleBO;
import org.isomorphism.service.FriendCircleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FriendCircleServiceImpl extends BaseInfoProperties implements FriendCircleService {

    @Resource
    private FriendCircleMapper friendCircleMapper;

    @Transactional
    @Override
    public void publish(FriendCircleBO friendCircleBO) {
        FriendCircle pendingFriendCircle = new FriendCircle();

        BeanUtils.copyProperties(friendCircleBO, pendingFriendCircle);

        friendCircleMapper.insert(pendingFriendCircle);
    }

}

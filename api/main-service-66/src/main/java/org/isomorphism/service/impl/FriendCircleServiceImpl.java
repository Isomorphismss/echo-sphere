package org.isomorphism.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.isomorphism.base.BaseInfoProperties;
import org.isomorphism.mapper.FriendCircleMapper;
import org.isomorphism.mapper.FriendCircleMapperCustom;
import org.isomorphism.pojo.FriendCircle;
import org.isomorphism.pojo.bo.FriendCircleBO;
import org.isomorphism.pojo.vo.FriendCircleVO;
import org.isomorphism.service.FriendCircleService;
import org.isomorphism.utils.PagedGridResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class FriendCircleServiceImpl extends BaseInfoProperties implements FriendCircleService {

    @Resource
    private FriendCircleMapper friendCircleMapper;

    @Resource
    private FriendCircleMapperCustom friendCircleMapperCustom;

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
}

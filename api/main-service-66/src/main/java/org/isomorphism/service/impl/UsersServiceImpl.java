package org.isomorphism.service.impl;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.isomorphism.base.BaseInfoProperties;
import org.isomorphism.exceptions.GraceException;
import org.isomorphism.grace.result.ResponseStatusEnum;
import org.isomorphism.mapper.UsersMapper;
import org.isomorphism.pojo.Users;
import org.isomorphism.pojo.bo.ModifyUserBO;
import org.isomorphism.service.UsersService;
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
public class UsersServiceImpl extends BaseInfoProperties implements UsersService {

    @Resource
    private UsersMapper usersMapper;

    @Transactional
    @Override
    public void modifyUserInfo(ModifyUserBO userBO) {

        Users pendingUser = new Users();

        String userId = userBO.getUserId();
        if (StringUtils.isBlank(userId))
            GraceException.display(ResponseStatusEnum.USER_INFO_UPDATED_ERROR);

        pendingUser.setId(userId);
        pendingUser.setUpdatedTime(LocalDateTime.now());

        BeanUtils.copyProperties(userBO, pendingUser);

        usersMapper.updateById(pendingUser);

    }

}

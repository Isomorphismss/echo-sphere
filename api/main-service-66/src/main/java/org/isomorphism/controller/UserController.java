package org.isomorphism.controller;

import jakarta.annotation.Resource;
import org.isomorphism.base.BaseInfoProperties;
import org.isomorphism.grace.result.GraceJSONResult;
import org.isomorphism.pojo.Users;
import org.isomorphism.pojo.bo.ModifyUserBO;
import org.isomorphism.pojo.vo.UsersVO;
import org.isomorphism.service.UsersService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("userinfo")
public class UserController extends BaseInfoProperties {

    @Resource
    private UsersService userService;

    @PostMapping("modify")
    public GraceJSONResult modify(@RequestBody ModifyUserBO userBO) {

        // 修改用户信息
        userService.modifyUserInfo(userBO);

        // 返回最新用户信息
        UsersVO usersVO = getUserInfo(userBO.getUserId(), true);

        return GraceJSONResult.ok(usersVO);
    }

    private UsersVO getUserInfo(String userId, boolean needToken) {

        // 查询获得用户的最新信息
        Users latestUser = userService.getById(userId);

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(latestUser, usersVO);

        if (needToken) {
            String uToken = TOKEN_USER_PREFIX + SYMBOL_DOT + UUID.randomUUID();
            redis.set(REDIS_USER_TOKEN + ":" + userId, uToken);
            usersVO.setUserToken(uToken);
        }

        return usersVO;
    }

    @PostMapping("get")
    public GraceJSONResult get(@RequestParam String userId) {
        return GraceJSONResult.ok(getUserInfo(userId, false));
    }

}

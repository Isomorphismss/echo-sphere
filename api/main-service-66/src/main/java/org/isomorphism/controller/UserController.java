package org.isomorphism.controller;

import jakarta.annotation.Resource;
import org.isomorphism.grace.result.GraceJSONResult;
import org.isomorphism.pojo.bo.ModifyUserBO;
import org.isomorphism.service.UsersService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("userinfo")
public class UserController {

    @Resource
    private UsersService userService;

    @PostMapping("modify")
    public GraceJSONResult modify(@RequestBody ModifyUserBO userBO) {
        userService.modifyUserInfo(userBO);
        return GraceJSONResult.ok();
    }

}

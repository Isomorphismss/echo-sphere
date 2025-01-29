package org.isomorphism.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.isomorphism.base.BaseInfoProperties;
import org.isomorphism.grace.result.GraceJSONResult;
import org.isomorphism.grace.result.ResponseStatusEnum;
import org.isomorphism.pojo.Users;
import org.isomorphism.pojo.bo.RegisterLoginBO;
import org.isomorphism.service.UsersService;
import org.isomorphism.tasks.SMSTask;
import org.isomorphism.utils.IPUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("passport")
public class PassportController extends BaseInfoProperties {

    // 127.0.0.1:88/passport

    @Resource
    private SMSTask smsTask;

    @Resource
    private UsersService usersService;

    @PostMapping("getSMSCode")
    public GraceJSONResult getSMSCode(String mobile,
                                      HttpServletRequest request) throws Exception {
        if (StringUtils.isBlank(mobile)) {
            return GraceJSONResult.error();
        }

        // 获得用户的手机号/
        String userIp = IPUtil.getRequestIp(request);
        // 限制该用户的手机号/ip在60秒内只能获得一次验证码
        redis.setnx60s(MOBILE_SMSCODE + ":" + userIp, mobile);

        String code = (int) ((Math.random() * 9 + 1) * 100000) + "";
        System.out.println("验证码为：" + code);

        // 把验证码存入到redis中，用于后续注册/登录的校验
        redis.set(MOBILE_SMSCODE + ":" + mobile, code, 30 * 60);

        return GraceJSONResult.ok();
    }

    @PostMapping("register")
    public GraceJSONResult register(@RequestBody RegisterLoginBO registerLoginBO,
                                    HttpServletRequest request) throws Exception {
        String mobile = registerLoginBO.getMobile();
        String code = registerLoginBO.getSmsCode();

        // 1. 从redis中获得验证码进行校验判断是否匹配
        String redisCode = redis.get(MOBILE_SMSCODE + ":" + mobile);
        System.out.println("电话号码：" + mobile);
        System.out.println("数据库里的：" + redisCode);
        System.out.println("前端的：" + code);
        if (StringUtils.isBlank(redisCode) || !redisCode.equalsIgnoreCase(code)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }

        // 2. 根据mobile查询数据库，如果用户存在，则提示不能重复注册
        Users user = usersService.queryMobileIfExist(mobile);
        if (user == null) {
            // 2.1 如果查询数据库中用户为空，则表示用户没有注册过，则需要进行用户信息数据的入库
            user = usersService.createUsers(mobile);
        } else {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ALREADY_EXIST_ERROR);
        }

        // 3. 用户注册成功后，删除redis中的短信验证码使其失效
        redis.del(MOBILE_SMSCODE + ":" + mobile);

        // 4. 返回用户数据给前端
        return GraceJSONResult.ok(user);
    }

}

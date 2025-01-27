package org.isomorphism.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.isomorphism.base.BaseInfoProperties;
import org.isomorphism.grace.result.GraceJSONResult;
import org.isomorphism.tasks.SMSTask;
import org.isomorphism.utils.IPUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("passport")
public class PassportController extends BaseInfoProperties {

    // 127.0.0.1:88/passport

    @Resource
    private SMSTask smsTask;


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

}

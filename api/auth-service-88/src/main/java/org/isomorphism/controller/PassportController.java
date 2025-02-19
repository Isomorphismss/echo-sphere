package org.isomorphism.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.isomorphism.base.BaseInfoProperties;
import org.isomorphism.grace.result.GraceJSONResult;
import org.isomorphism.grace.result.ResponseStatusEnum;
import org.isomorphism.pojo.Users;
import org.isomorphism.pojo.bo.RegisterLoginBO;
import org.isomorphism.pojo.vo.UsersVO;
import org.isomorphism.service.UsersService;
import org.isomorphism.tasks.SMSTask;
import org.isomorphism.utils.IPUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
        redis.setnx(MOBILE_SMSCODE + ":" + userIp, mobile, 10);

        String code = (int) ((Math.random() * 9 + 1) * 100000) + "";
        System.out.println("验证码为：" + code);

        // 把验证码存入到redis中，用于后续注册/登录的校验
        redis.set(MOBILE_SMSCODE + ":" + mobile, code, 30 * 60);

        return GraceJSONResult.ok();
    }

    @PostMapping("register")
    public GraceJSONResult register(@RequestBody @Valid RegisterLoginBO registerLoginBO,
                                    HttpServletRequest request) throws Exception {
        String mobile = registerLoginBO.getMobile();
        String code = registerLoginBO.getSmsCode();
        String nickname = registerLoginBO.getNickname();

        // 1. 从redis中获得验证码进行校验判断是否匹配
        String redisCode = redis.get(MOBILE_SMSCODE + ":" + mobile);
        if (StringUtils.isBlank(redisCode) || !redisCode.equalsIgnoreCase(code)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }

        // 2. 根据mobile查询数据库，如果用户存在，则提示不能重复注册
        Users user = usersService.queryMobileIfExist(mobile);
        if (user == null) {
            // 2.1 如果查询数据库中用户为空，则表示用户没有注册过，则需要进行用户信息数据的入库
            user = usersService.createUsers(mobile, nickname);
        } else {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ALREADY_EXIST_ERROR);
        }

        // 3. 用户注册成功后，删除redis中的短信验证码使其失效
        redis.del(MOBILE_SMSCODE + ":" + mobile);

        // 4. 设置用户分布式会话，保存用户的token令牌，存储到redis中
        String uToken = TOKEN_USER_PREFIX + SYMBOL_DOT + UUID.randomUUID();
        // 本方式只能限制用户在一台设备进行登录
        // redis.set(REDIS_USER_TOKEN + ":" + user.getId(), uToken);  // 设置分布式会话
        // 本方式允许用户在多端多设备进行登录
        redis.set(REDIS_USER_TOKEN + ":" + uToken, user.getId());  // 设置分布式会话

        // 5. 返回用户数据给前端
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user, usersVO);
        usersVO.setUserToken(uToken);

        return GraceJSONResult.ok(usersVO);
    }

    @PostMapping("login")
    public GraceJSONResult login(@RequestBody @Valid RegisterLoginBO registerLoginBO,
                                    HttpServletRequest request) throws Exception {
        String mobile = registerLoginBO.getMobile();
        String code = registerLoginBO.getSmsCode();

        // 1. 从redis中获得验证码进行校验判断是否匹配
        String redisCode = redis.get(MOBILE_SMSCODE + ":" + mobile);
        if (StringUtils.isBlank(redisCode) || !redisCode.equalsIgnoreCase(code)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }

        // 2. 根据mobile查询数据库
        Users user = usersService.queryMobileIfExist(mobile);
        if (user == null) {
            // 2.1 如果查询数据库中用户为空，则表示用户没有注册过，则返回错误信息
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        // 3. 用户注册成功后，删除redis中的短信验证码使其失效
        redis.del(MOBILE_SMSCODE + ":" + mobile);

        // 4. 设置用户分布式会话，保存用户的token令牌，存储到redis中
        String uToken = TOKEN_USER_PREFIX + SYMBOL_DOT + UUID.randomUUID();
        // 本方式只能限制用户在一台设备进行登录
        // redis.set(REDIS_USER_TOKEN + ":" + user.getId(), uToken);  // 设置分布式会话
        // 本方式允许用户在多端多设备进行登录
        redis.set(REDIS_USER_TOKEN + ":" + uToken, user.getId());  // 设置分布式会话

        // 5. 返回用户数据给前端
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user, usersVO);
        usersVO.setUserToken(uToken);

        return GraceJSONResult.ok(usersVO);
    }

    /**
     * 一键注册登录接口，可以同时提供给用户做登录和注册使用调用
     * @param registerLoginBO
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("registerOrLogin")
    public GraceJSONResult registerOrLogin(@RequestBody @Valid RegisterLoginBO registerLoginBO,
                                    HttpServletRequest request) throws Exception {
        String mobile = registerLoginBO.getMobile();
        String code = registerLoginBO.getSmsCode();
        String nickname = registerLoginBO.getNickname();

        // 1. 从redis中获得验证码进行校验判断是否匹配
        String redisCode = redis.get(MOBILE_SMSCODE + ":" + mobile);
        if (StringUtils.isBlank(redisCode) || !redisCode.equalsIgnoreCase(code)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }

        // 2. 根据mobile查询数据库，如果用户存在，则直接登录
        Users user = usersService.queryMobileIfExist(mobile);
        if (user == null) {
            // 2.1 如果查询数据库中用户为空，则表示用户没有注册过，则需要进行用户信息数据的入库
            user = usersService.createUsers(mobile, nickname);
        }

        // 3. 用户注册成功后，删除redis中的短信验证码使其失效
        redis.del(MOBILE_SMSCODE + ":" + mobile);

        // 4. 设置用户分布式会话，保存用户的token令牌，存储到redis中
        String uToken = TOKEN_USER_PREFIX + SYMBOL_DOT + UUID.randomUUID();
        // 本方式只能限制用户在一台设备进行登录
        // redis.set(REDIS_USER_TOKEN + ":" + user.getId(), uToken);  // 设置分布式会话
        // 本方式允许用户在多端多设备进行登录
        redis.set(REDIS_USER_TOKEN + ":" + uToken, user.getId());  // 设置分布式会话

        // 5. 返回用户数据给前端
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user, usersVO);
        usersVO.setUserToken(uToken);

        return GraceJSONResult.ok(usersVO);
    }

    @PostMapping("logout")
    public GraceJSONResult logout(@RequestParam String userId,
                                  HttpServletRequest request) throws Exception {
        // 清理用户的分布式会话
        redis.del(REDIS_USER_TOKEN + ":" + userId);
        return GraceJSONResult.ok();
    }

}

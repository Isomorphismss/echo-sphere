package org.isomorphism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.isomorphism.api.feign.FileMicroServiceFeign;
import org.isomorphism.base.BaseInfoProperties;
import org.isomorphism.enums.Sex;
import org.isomorphism.mapper.UsersMapper;
import org.isomorphism.pojo.Users;
import org.isomorphism.service.UsersService;
import org.isomorphism.utils.DesensitizationUtil;
import org.isomorphism.utils.LocalDateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

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

    private static final String USER_FACE1 = "https://uploadfiles.nowcoder.com/images/20240404/143824957_1712206246764/FA5D174C17AF333ACFAE67586E6D7651";

    @Override
    public Users queryMobileIfExist(String mobile) {
        return usersMapper.selectOne(
                new QueryWrapper<Users>()
                        .eq("mobile", mobile)
        );
    }

    @Transactional
    @Override
    public Users createUsers(String mobile, String nickname) {
        Users user = new Users();
        user.setMobile(mobile);

        String uuid = UUID.randomUUID().toString();
        String uuidStr[] = uuid.split("-");
        String wechatNum = "wx" + uuidStr[0] + uuidStr[1];
        user.setWechatNum(wechatNum);
        String wechatNumUrl = getQrCodeUrl(wechatNum, TEMP_STRING);
        user.setWechatNumImg(wechatNumUrl);

        // 用户138****1234
        if (nickname != null && StringUtils.isNotBlank(nickname)) {
            user.setNickname(nickname);
        } else {
            nickname = DesensitizationUtil.commonDisplay(mobile);
            user.setNickname(nickname);
        }

        user.setRealName("");

        user.setSex(Sex.secret.type);
        user.setFace(USER_FACE1);
        user.setFriendCircleBg(USER_FACE1);
        user.setEmail("");

        user.setBirthday(LocalDateUtils.
                parseLocalDate("1980-01-01",
                        LocalDateUtils.DATE_PATTERN));

        user.setCountry("中国");
        user.setProvince("");
        user.setCity("");
        user.setDistrict("");

        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());

        usersMapper.insert(user);

        return user;
    }

    @Resource
    private FileMicroServiceFeign fileMicroServiceFeign;

    private String getQrCodeUrl(String wechatNumber, String userId) {
        try {
            return fileMicroServiceFeign.generatorQrCode(wechatNumber, userId);
        } catch (Exception e) {
            return null;
        }
    }

}

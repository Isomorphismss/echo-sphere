package org.isomorphism.service;

import org.isomorphism.pojo.Users;
import org.isomorphism.pojo.bo.ModifyUserBO;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author isomorphism
 * @since 2025-01-29
 */
public interface UsersService {

    /**
     * 修改用户基本信息
     * @param userBO
     */
    public void modifyUserInfo(ModifyUserBO userBO);

    /**
     * 获得用户信息
     * @param userId
     * @return
     */
    public Users getById(String userId);

    /**
     * 根据微信号（账号）或者手机号精准匹配
     * @param queryString
     * @return
     */
    public Users getByWechatNumberOrMobile(String queryString);

}

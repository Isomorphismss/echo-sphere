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

}

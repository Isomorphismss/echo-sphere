package org.isomorphism.service;

import org.isomorphism.pojo.Users;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author isomorphism
 * @since 2025-01-29
 */
public interface UsersService {

    public Users queryMobileIfExist(String mobile);

    public Users createUsers(String mobile, String nickname);

}

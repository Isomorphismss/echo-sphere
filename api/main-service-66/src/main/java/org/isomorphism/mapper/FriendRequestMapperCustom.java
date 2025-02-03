package org.isomorphism.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.isomorphism.pojo.FriendRequest;
import org.isomorphism.pojo.vo.NewFriendsVO;

import java.util.Map;

/**
 * <p>
 * 好友请求记录表 Mapper 接口
 * </p>
 *
 * @author isomorphism
 * @since 2025-01-29
 */
public interface FriendRequestMapperCustom {

    public Page<NewFriendsVO> queryNewFriendList(@Param("page") Page<NewFriendsVO> page,
                                                 @Param("paramMap") Map<String, Object> map);

}

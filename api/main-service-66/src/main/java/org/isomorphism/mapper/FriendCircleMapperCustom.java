package org.isomorphism.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.isomorphism.pojo.vo.FriendCircleVO;
import org.isomorphism.pojo.vo.NewFriendsVO;

import java.util.Map;

/**
 * <p>
 * 朋友圈表 Mapper 接口
 * </p>
 *
 * @author isomorphism
 * @since 2025-01-29
 */
public interface FriendCircleMapperCustom {

    public Page<FriendCircleVO> queryFriendCircleList(@Param("page") Page<FriendCircleVO> page,
                                                      @Param("paramMap") Map<String, Object> map);

}

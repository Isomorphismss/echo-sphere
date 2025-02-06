package org.isomorphism.mapper;

import org.apache.ibatis.annotations.Param;
import org.isomorphism.pojo.vo.CommentVO;

import java.util.List;
import java.util.Map;

public interface CommentMapperCustom {

    public List<CommentVO> queryFriendCircleComments(@Param("paramMap") Map<String, Object> map);

}

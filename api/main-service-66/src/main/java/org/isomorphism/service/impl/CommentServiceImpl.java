package org.isomorphism.service.impl;

import jakarta.annotation.Resource;
import org.isomorphism.base.BaseInfoProperties;
import org.isomorphism.mapper.CommentMapper;
import org.isomorphism.pojo.Comment;
import org.isomorphism.pojo.Users;
import org.isomorphism.pojo.bo.CommentBO;
import org.isomorphism.pojo.vo.CommentVO;
import org.isomorphism.service.CommentService;
import org.isomorphism.service.UsersService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CommentServiceImpl extends BaseInfoProperties implements CommentService {

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private UsersService usersService;

    @Transactional
    @Override
    public CommentVO createComment(CommentBO commentBO) {

        // 新增留言
        Comment pendingComment = new Comment();

        BeanUtils.copyProperties(commentBO, pendingComment);
        pendingComment.setCreatedTime(LocalDateTime.now());

        commentMapper.insert(pendingComment);

        // 留言后的最新评论数据需要返回给前端（提供前端做的扩展数据）
        CommentVO commentVO = new CommentVO();
        BeanUtils.copyProperties(pendingComment, commentVO);

        Users commentUser = usersService.getById(commentBO.getCommentUserId());
        commentVO.setCommentUserNickname(commentUser.getNickname());
        commentVO.setCommentUserFace(commentUser.getFace());
        commentVO.setCommentId(pendingComment.getId());

        return commentVO;
    }
}

package org.isomorphism.service;

import org.isomorphism.pojo.bo.CommentBO;
import org.isomorphism.pojo.vo.CommentVO;

public interface CommentService {

    /**
     * 创建发表评论
     * @param commentBO
     */
    public CommentVO createComment(CommentBO commentBO);

}

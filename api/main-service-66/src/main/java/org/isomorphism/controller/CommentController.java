package org.isomorphism.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.isomorphism.base.BaseInfoProperties;
import org.isomorphism.grace.result.GraceJSONResult;
import org.isomorphism.pojo.bo.CommentBO;
import org.isomorphism.pojo.vo.CommentVO;
import org.isomorphism.service.CommentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("comment")
public class CommentController extends BaseInfoProperties {

    @Resource
    private CommentService commentService;

    @PostMapping("create")
    public GraceJSONResult create(@RequestBody CommentBO commentBO,
                                  HttpServletRequest request) {
        CommentVO commentVO = commentService.createComment(commentBO);
        return GraceJSONResult.ok(commentVO);
    }

}

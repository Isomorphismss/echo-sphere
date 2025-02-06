package org.isomorphism.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.isomorphism.base.BaseInfoProperties;
import org.isomorphism.grace.result.GraceJSONResult;
import org.isomorphism.pojo.FriendCircleLiked;
import org.isomorphism.pojo.bo.FriendCircleBO;
import org.isomorphism.pojo.vo.CommentVO;
import org.isomorphism.pojo.vo.FriendCircleVO;
import org.isomorphism.service.CommentService;
import org.isomorphism.service.FriendCircleService;
import org.isomorphism.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("friendCircle")
public class FriendCircleController extends BaseInfoProperties {

    @Resource
    private FriendCircleService friendCircleService;

    @Resource
    private CommentService commentService;

    @PostMapping("publish")
    public GraceJSONResult hello(@RequestBody  FriendCircleBO friendCircleBO,
                                 HttpServletRequest request) {

        String userId = request.getHeader(HEADER_USER_ID);

        friendCircleBO.setUserId(userId);
        friendCircleBO.setPublishTime(LocalDateTime.now());

        friendCircleService.publish(friendCircleBO);

        return GraceJSONResult.ok();
    }

    @PostMapping("queryList")
    public GraceJSONResult queryList(String userId,
                                     @RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "15") Integer pageSize) {
        if (StringUtils.isBlank(userId)) return GraceJSONResult.error();

        PagedGridResult gridResult = friendCircleService.queryList(userId, page, pageSize);

        List<FriendCircleVO> list = (List<FriendCircleVO>) gridResult.getRows();
        for (FriendCircleVO f : list) {
            String friendCircleId = f.getFriendCircleId();
            List<FriendCircleLiked> likedList = friendCircleService.queryLikedFriends(friendCircleId);
            f.setLikedFriends(likedList);

            boolean res = friendCircleService.doILike(friendCircleId, userId);
            f.setDoILike(res);

            List<CommentVO> commentList = commentService.queryAll(friendCircleId);
            f.setCommentList(commentList);
        }

        return GraceJSONResult.ok(gridResult);
    }

    @PostMapping("like")
    public GraceJSONResult like(String friendCircleId,
                                HttpServletRequest request) {
        String userId = request.getHeader(HEADER_USER_ID);
        friendCircleService.like(friendCircleId, userId);
        return GraceJSONResult.ok();
    }

    @PostMapping("unlike")
    public GraceJSONResult unlike(String friendCircleId,
                                  HttpServletRequest request) {
        String userId = request.getHeader(HEADER_USER_ID);
        friendCircleService.unlike(friendCircleId, userId);
        return GraceJSONResult.ok();
    }

    @PostMapping("likedFriends")
    public GraceJSONResult likedFriends(String friendCircleId,
                                  HttpServletRequest request) {
        List<FriendCircleLiked> likedList = friendCircleService.queryLikedFriends(friendCircleId);

        return GraceJSONResult.ok(likedList);
    }

}

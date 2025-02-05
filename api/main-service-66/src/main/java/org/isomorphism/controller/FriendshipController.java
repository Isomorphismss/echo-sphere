package org.isomorphism.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.isomorphism.base.BaseInfoProperties;
import org.isomorphism.enums.YesOrNo;
import org.isomorphism.grace.result.GraceJSONResult;
import org.isomorphism.pojo.Friendship;
import org.isomorphism.service.FriendshipService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("friendship")
@Slf4j
public class FriendshipController extends BaseInfoProperties {

    @Resource
    private FriendshipService friendshipService;

    @PostMapping("getFriendship")
    public GraceJSONResult getFriendship(String friendId, HttpServletRequest request) {
        String myId = request.getHeader(HEADER_USER_ID);
        Friendship friendship = friendshipService.getFriendship(myId, friendId);
        return GraceJSONResult.ok(friendship);
    }

    @PostMapping("queryMyFriends")
    public GraceJSONResult queryMyFriends(HttpServletRequest request) {
        String myId = request.getHeader(HEADER_USER_ID);
        return GraceJSONResult.ok(friendshipService.queryMyFriends(myId, false));
    }

    @PostMapping("queryMyBlackList")
    public GraceJSONResult queryMyBlackList(HttpServletRequest request) {
        String myId = request.getHeader(HEADER_USER_ID);
        return GraceJSONResult.ok(friendshipService.queryMyFriends(myId, true));
    }

    @PostMapping("updateFriendRemark")
    public GraceJSONResult updateFriendRemark(HttpServletRequest request,
                                              String friendId,
                                              String friendRemark) {
        if (StringUtils.isBlank(friendId) || StringUtils.isBlank(friendRemark)) {
            return GraceJSONResult.error();
        }

        String myId = request.getHeader(HEADER_USER_ID);
        friendshipService.updateFriendRemark(myId, friendId, friendRemark);
        return GraceJSONResult.ok();
    }

    @PostMapping("tobeBlack")
    public GraceJSONResult tobeBlack(HttpServletRequest request,
                                              String friendId) {
        if (StringUtils.isBlank(friendId)) {
            return GraceJSONResult.error();
        }

        String myId = request.getHeader(HEADER_USER_ID);
        friendshipService.updateBlackList(myId, friendId, YesOrNo.YES);

        return GraceJSONResult.ok();
    }

    @PostMapping("moveOutBlack")
    public GraceJSONResult moveOutBlack(HttpServletRequest request,
                                     String friendId) {
        if (StringUtils.isBlank(friendId)) {
            return GraceJSONResult.error();
        }

        String myId = request.getHeader(HEADER_USER_ID);
        friendshipService.updateBlackList(myId, friendId, YesOrNo.NO);

        return GraceJSONResult.ok();
    }

    @PostMapping("delete")
    public GraceJSONResult delete(HttpServletRequest request,
                                        String friendId) {
        if (StringUtils.isBlank(friendId)) {
            return GraceJSONResult.error();
        }

        String myId = request.getHeader(HEADER_USER_ID);
        friendshipService.delete(myId, friendId);

        return GraceJSONResult.ok();
    }

}

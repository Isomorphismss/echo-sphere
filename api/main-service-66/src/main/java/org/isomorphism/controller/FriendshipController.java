package org.isomorphism.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.isomorphism.base.BaseInfoProperties;
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
        Friendship friendship = friendshipService.getFriendship(friendId, myId);
        return GraceJSONResult.ok(friendship);
    }

    @PostMapping("queryMyFriends")
    public GraceJSONResult queryMyFriends(HttpServletRequest request) {
        String myId = request.getHeader(HEADER_USER_ID);
        return GraceJSONResult.ok(friendshipService.queryMyFriends(myId));
    }

}

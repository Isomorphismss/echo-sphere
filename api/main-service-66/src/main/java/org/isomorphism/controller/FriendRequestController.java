package org.isomorphism.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.isomorphism.base.BaseInfoProperties;
import org.isomorphism.grace.result.GraceJSONResult;
import org.isomorphism.pojo.bo.NewFriendRequestBO;
import org.isomorphism.service.FriendRequestService;
import org.isomorphism.utils.PagedGridResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("friendRequest")
@Slf4j
public class FriendRequestController extends BaseInfoProperties {

    @Resource
    private FriendRequestService friendRequestService;

    @PostMapping("add")
    public GraceJSONResult add(@RequestBody @Valid NewFriendRequestBO friendRequestBO) {
        friendRequestService.addNewRequest(friendRequestBO);
        return GraceJSONResult.ok();
    }

    @PostMapping("queryNew")
    public GraceJSONResult queryNew(HttpServletRequest request,
                                    @RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "15") Integer pageSize) {
        String userId = request.getHeader(HEADER_USER_ID);

        PagedGridResult result = friendRequestService.queryNewFriendList(userId, page, pageSize);
        return GraceJSONResult.ok(result);
    }

    @PostMapping("pass")
    public GraceJSONResult pass(String friendRequestId, String friendRemark) {
        friendRequestService.passNewFriend(friendRequestId, friendRemark);
        return GraceJSONResult.ok();
    }

}

package org.isomorphism.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.isomorphism.grace.result.GraceJSONResult;
import org.isomorphism.pojo.bo.NewFriendRequestBO;
import org.isomorphism.service.FriendRequestService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("friendRequest")
@Slf4j
public class FriendRequestController {

    @Resource
    private FriendRequestService friendRequestService;

    @PostMapping("add")
    public GraceJSONResult add(@RequestBody @Valid NewFriendRequestBO friendRequestBO) {
        friendRequestService.addNewRequest(friendRequestBO);
        return GraceJSONResult.ok();
    }

}

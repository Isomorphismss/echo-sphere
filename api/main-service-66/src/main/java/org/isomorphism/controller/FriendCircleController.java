package org.isomorphism.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.isomorphism.base.BaseInfoProperties;
import org.isomorphism.grace.result.GraceJSONResult;
import org.isomorphism.pojo.bo.FriendCircleBO;
import org.isomorphism.service.FriendCircleService;
import org.isomorphism.utils.PagedGridResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("friendCircle")
public class FriendCircleController extends BaseInfoProperties {

    @Resource
    private FriendCircleService friendCircleService;

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
        return GraceJSONResult.ok(gridResult);
    }

}

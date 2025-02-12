package org.isomorphism.controller;

import org.isomorphism.base.BaseInfoProperties;
import org.isomorphism.grace.result.GraceJSONResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("chat")
public class ChatController extends BaseInfoProperties {

    // 127.0.0.1:77/a/hello

    @PostMapping("getMyUnreadCounts")
    public GraceJSONResult ok(String myId) {

        Map map = redis.hgetall(CHAT_MSG_LIST + ":" + myId);

        return GraceJSONResult.ok(map);
    }

}

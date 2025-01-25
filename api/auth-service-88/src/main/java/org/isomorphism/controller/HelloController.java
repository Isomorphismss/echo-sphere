package org.isomorphism.controller;

import jakarta.annotation.Resource;
import org.isomorphism.tasks.SMSTask;
import org.isomorphism.utils.SMSUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("a")
public class HelloController {

    // 127.0.0.1:88/a/hello

    @Resource
    private SMSUtils smsUtils;

    @Resource
    private SMSTask smsTask;

    @GetMapping("hello")
    public Object hello() {
        return "Hello World";
    }

    @GetMapping("sms")
    public Object sms() throws Exception {
        smsUtils.sendSMS("12345678901", "9875");

        return "Send SMS OK~";
    }

    @GetMapping("smsTask")
    public Object smsTask() throws Exception {
        smsTask.sendSMSInTask("12312312312", "8111");

        return "Send SMS In Task OK~";
    }

}

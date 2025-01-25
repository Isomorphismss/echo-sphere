package org.isomorphism;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.isomorphism.utils.SMSUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SMSTask {

    @Resource
    private SMSUtils smsUtils;

    @Async
    public void sendSMSInTask(String mobile, String code) throws Exception {
        smsUtils.sendSMS("12312312312", code);
        log.info("异步任务中所发送的验证码为：{}", code);
    }

}

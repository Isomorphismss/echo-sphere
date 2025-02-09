package org.isomorphism.utils;

import org.springframework.beans.factory.annotation.Value;

public class BaiduAISDK {

    @Value("${baidu.speech-to-text.app-id}")
    public static String APP_ID;

    @Value("${baidu.speech-to-text.api-key}")
    public static String API_KEY;

    @Value("${baidu.speech-to-text.secret-key}")
    public static String SECRET_KEY;

}

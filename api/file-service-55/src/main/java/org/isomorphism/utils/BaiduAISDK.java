package org.isomorphism.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

public class BaiduAISDK {

    @Value("${baidu.speech-to-text.app-id}")
    private String appId;

    @Value("${baidu.speech-to-text.api-key}")
    private String apiKey;

    @Value("${baidu.speech-to-text.secret-key}")
    private String secretKey;

    public static String APP_ID;
    public static String API_KEY;
    public static String SECRET_KEY;

    @PostConstruct
    public void init() {
        APP_ID = appId;
        API_KEY = apiKey;
        SECRET_KEY = secretKey;
    }

}

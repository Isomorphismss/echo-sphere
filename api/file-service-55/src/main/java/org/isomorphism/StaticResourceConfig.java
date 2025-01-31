package org.isomorphism;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class StaticResourceConfig extends WebMvcConfigurationSupport {

    // 配置本地路径
    @Value("${image.storage.path}")
    private String imageStoragePath;

    /**
     *
     * @param registry
     */
    // 添加静态资源映射路径，图片、视频、音频等都放在classpath下的static中
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/static/**")
                .addResourceLocations("file:" + imageStoragePath);

        // http://127.0.0.1:55/static/face/图片名

        super.addResourceHandlers(registry);
    }

}

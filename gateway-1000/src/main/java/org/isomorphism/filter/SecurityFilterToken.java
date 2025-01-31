package org.isomorphism.filter;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.isomorphism.base.BaseInfoProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class SecurityFilterToken extends BaseInfoProperties implements GlobalFilter, Ordered {

    @Resource
    private ExcludeUrlProperties excludeUrlProperties;

    // 路径匹配规则器
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 1. 获得当前用户请求的路径url
        String url = exchange.getRequest().getURI().getPath();
        log.info("SecurityFilterToken url = {}", url);

        // 2. 获得所有的需要排除校验的url list
        List<String> excludeList = excludeUrlProperties.getUrls();

        // 3. 校验并且排除excludeList
        if (excludeList != null && !excludeList.isEmpty()) {
            for (String excludeUrl : excludeList) {
                if (antPathMatcher.matchStart(excludeUrl, url)) {
                    // 如果匹配到，则直接放行，表示当前的url是不需要被拦截校验的
                    return chain.filter(exchange);
                }
            }
        }

        // 4. 代码到达此处，表示请求被拦截，需要进行校验
        log.info("当前请求的路径[{}]被拦截...", url);

        // 默认放行请求到后续的路由（服务）
        return chain.filter(exchange);
    }

    // 过滤器的顺序，数字越小则优先级越大
    @Override
    public int getOrder() {
        return 0;
    }

}

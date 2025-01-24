package org.isomorphism.filter;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.isomorphism.base.BaseInfoProperties;
import org.isomorphism.grace.result.GraceJSONResult;
import org.isomorphism.grace.result.ResponseStatusEnum;
import org.isomorphism.utils.IPUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RefreshScope
public class IPLimitFilter extends BaseInfoProperties implements GlobalFilter, Ordered {

    /**
     * 需求：
     * 判断某个请求的ip在20秒内的请求次数是否超过3次
     * 如果超过3次，则限制访问30秒
     * 等待30秒静默后，才能够继续恢复访问
     */

    @Value("${blackIp.continueCounts}")
    private Integer continueCounts;

    @Value("${blackIp.timeInterval}")
    private Integer timeInterval;

    @Value("${blackIp.limitTimes}")
    private Integer limitTimes;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return doLimit(exchange, chain);
    }

    // 限制ip请求次数的判断
    public Mono<Void> doLimit(ServerWebExchange exchange,
                              GatewayFilterChain chain) {
        // 根据request获得请求ip
        ServerHttpRequest request = exchange.getRequest();
        String ip = IPUtil.getIP(request);

        // 正常的ip定义
        final String ipRedisKey = "gateway-ip:" + ip;
        // 被拦截的黑名单ip，如果在redis中存在，则表示目前被关小黑屋
        final String ipRedisLimitKey = "gateway-ip:limit:" + ip;

        // 获得当前的ip并且查询还剩下多少时间，如果事件存在（大于0），则表示当前处在黑名单中
        long limitLeftTimes = redis.ttl(ipRedisLimitKey);
        if (limitLeftTimes > 0) {
            // 终止请求，返回错误
            return renderErrorMsg(exchange, ResponseStatusEnum.SYSTEM_ERROR_BLACK_IP);
        }

        // 在redis中获得ip的累加次数
        long requestCounts = redis.increment(ipRedisKey, 1);
        if (requestCounts == 1) {
            redis.expire(ipRedisKey, timeInterval);
        }
        if (requestCounts > continueCounts) {
            // 限制ip访问的时间
            redis.set(ipRedisLimitKey, ipRedisLimitKey, limitTimes);
            return renderErrorMsg(exchange, ResponseStatusEnum.SYSTEM_ERROR_BLACK_IP);
        }

        return chain.filter(exchange);
    }

    // 重新包装并且返回错误信息
    public Mono<Void> renderErrorMsg(ServerWebExchange exchange,
                                    ResponseStatusEnum statusEnum) {
        // 1. 获得响应response
        ServerHttpResponse response = exchange.getResponse();

        // 2. 构建jsonResult
        GraceJSONResult jsonResult = GraceJSONResult.exception(statusEnum);

        // 3. 设置header类型
        if (!response.getHeaders().containsKey("Content-Type")) {
            response.getHeaders().add("Content-Type",
                    MimeTypeUtils.APPLICATION_JSON_VALUE);
        }

        // 4. 修改当前response的状态码code为500
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);

        // 5. 转换json并且向response中写入数据
        String resultJson = new Gson().toJson(jsonResult);

        DataBuffer buffer = response
                .bufferFactory()
                .wrap(resultJson.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    // 过滤器的顺序，数字越小则优先级越大
    @Override
    public int getOrder() {
        return 1;
    }

}

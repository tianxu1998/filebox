package top.xufilebox.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.xufilebox.common.redis.RedisTemplateProxy;
import top.xufilebox.common.result.Result;
import top.xufilebox.common.result.ResultCode;
import top.xufilebox.gateway.component.AllowPathHolder;
import top.xufilebox.gateway.util.JwtTokenUtil;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @description:
 * @author: alextian
 * @create: 2020-12-23 15:50
 **/
@Component
@Slf4j
@RefreshScope
public class TokenFilter implements GlobalFilter, Ordered {
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    RedisTemplateProxy redisTemplateProxy;
    @Autowired
    AllowPathHolder allowPathHolder;
    @Autowired
    PathMatcher pathMatcher;
    @Value("${jwt.tokenHead}")
    String tokenHead;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        String uri = request.getURI().getPath();
        long count = allowPathHolder.getAllowPaths()
                .stream()
                .filter(path -> pathMatcher.match(path, uri))
                .count();
        if (count > 0) {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("gateway log[ time:{}, path:{}, request:{}, response:{}]",
                        LocalDateTime.now(), exchange.getRequest().getURI().getPath(),
                        exchange.getRequest(), exchange.getResponse());
            }));
        }
        String token = request.getHeaders().getFirst("Authorization");
        // 检查是否为空
        if (StringUtils.isEmpty(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return getVoidMono(response, ResultCode.USER_EMPTY_TOKEN);
        }
        token = token.substring(tokenHead.length()).trim();
        // 检查token是否过期
        if (redisTemplateProxy.isExpired(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return getVoidMono(response, ResultCode.USER_EMPTY_TOKEN);
        }
        Claims userInfo = null;
        try {
            userInfo = jwtTokenUtil.getClaimsFromToken(token);
            if (userInfo.getExpiration().before(new Date())) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return getVoidMono(response, ResultCode.USER_TOKEN_EXPIRED);
            }
        } catch (Exception e) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return getVoidMono(response, ResultCode.USER_TOKEN_ERROR);
        }

        String userName = String.valueOf(userInfo.get("userName"));
        String userId = String.valueOf(userInfo.get("userId"));
        String role = String.valueOf(userInfo.get("role"));
        String nickName = String.valueOf(userInfo.get("nickName"));
        // 把经过网关解析的用户基本信息填到header中，除了网关其他微服务无需进行不必要的身份认证
        ServerHttpRequest newRequest = request.mutate().headers(headers -> {
            headers.add("userName", userName);
            headers.remove("userId");
            headers.add("userId", userId);
            headers.add("role", role);
            headers.add("nickName", nickName);
        }).build();
        ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
        return chain.filter(newExchange).then(Mono.fromRunnable(() -> {
            log.info("gateway log[ time:{}, path:{}, request:{}, response:{}]",
                    LocalDateTime.now(), exchange.getRequest().getURI().getPath(),
                    exchange.getRequest(), exchange.getResponse());
        }));
    }

    private Mono<Void> getVoidMono(ServerHttpResponse serverHttpResponse, ResultCode responseCodeEnum) {
        serverHttpResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        Result responseResult = Result.failed(responseCodeEnum);
        ObjectMapper mapper = new ObjectMapper();
        DataBuffer dataBuffer = null;
        try {
            dataBuffer = serverHttpResponse.bufferFactory().wrap(mapper.writeValueAsString(responseResult).getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return serverHttpResponse.writeWith(Flux.just(dataBuffer));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

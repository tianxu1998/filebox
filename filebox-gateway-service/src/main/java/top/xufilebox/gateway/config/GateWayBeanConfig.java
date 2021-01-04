package top.xufilebox.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.WebFilter;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * @description:
 * @author: alextian
 * @create: 2020-12-23 16:14
 **/
@Configuration
public class GateWayBeanConfig {
    @Bean
    public PathMatcher antPathMatcher() {
        return new AntPathMatcher();
    }



    @Bean
    public WebFilter corsFilter() {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (!CorsUtils.isCorsRequest(request)) {
                return chain.filter(exchange);
            }
            ServerHttpResponse response = exchange.getResponse();
            HttpHeaders headers = response.getHeaders();
            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST,GET,OPTIONS,DELETE,PUT");
            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "content-type");
            headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3628800");
            return chain.filter(exchange);
        };
    }
}

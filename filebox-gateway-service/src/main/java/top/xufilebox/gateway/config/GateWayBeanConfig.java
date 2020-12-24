package top.xufilebox.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

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
}

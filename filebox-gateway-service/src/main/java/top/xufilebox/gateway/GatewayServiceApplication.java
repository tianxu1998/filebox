package top.xufilebox.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

/**
 * @description:
 * @author: alextian
 * @create: 2020-12-22 15:06
 **/
@SpringBootApplication(scanBasePackages = "top.xufilebox.**")
@EnableEurekaClient
public class GatewayServiceApplication {
    @Autowired
    RouteLocator locator;
    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }
}

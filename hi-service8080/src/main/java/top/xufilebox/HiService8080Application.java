package top.xufilebox;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableEurekaClient
@EnableDiscoveryClient
@RestController
@SpringBootApplication
public class HiService8080Application {
    @Value("${server.port}")
    int port;

    public static void main(String[] args) {
        SpringApplication.run(HiService8080Application.class, args);
    }

    @RequestMapping("/hi")
    public String hello() {
        return "hi i am server at" + port;
    }

}

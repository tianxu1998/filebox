package top.xufilebox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class FileboxEurekaService8002Application {

    public static void main(String[] args) {
        SpringApplication.run(FileboxEurekaService8002Application.class, args);
    }

}

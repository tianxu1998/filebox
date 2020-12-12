package top.xufilebox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import java.util.Arrays;
import java.util.Scanner;

@SpringBootApplication
@EnableEurekaServer
public class FileboxEurekaService8001Application {

    public static void main(String[] args) {
        SpringApplication.run(FileboxEurekaService8001Application.class, args);
    }
}

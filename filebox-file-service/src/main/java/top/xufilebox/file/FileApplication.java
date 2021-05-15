package top.xufilebox.file;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @description:
 * @author: alextian
 * @create: 2020-12-31 10:29
 **/
@SpringBootApplication(scanBasePackages = "top.xufilebox.**", exclude = DruidDataSourceAutoConfigure.class)
@MapperScan("top.xufilebox.common.mybatis.mapper")
@EnableFeignClients(basePackages = {"top.xufilebox.**"})
public class FileApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class, args);
    }
}

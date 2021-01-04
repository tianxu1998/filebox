package top.xufilebox.user;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "top.xufilebox.**", exclude = DruidDataSourceAutoConfigure.class)
@MapperScan("top.xufilebox.common.mybatis.mapper")
public class FileboxUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileboxUserServiceApplication.class, args);
    }

}

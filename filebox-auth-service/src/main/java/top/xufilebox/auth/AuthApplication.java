package top.xufilebox.auth;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @description:
 * @author: alextian
 * @create: 2020-12-19 10:50
 **/

@SpringBootApplication(scanBasePackages = "top.xufilebox.**", exclude = DruidDataSourceAutoConfigure.class)
@MapperScan("top.xufilebox.common.mybatis.mapper")
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}

package top.xufilebox.file;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @description:
 * @author: alextian
 * @create: 2020-12-31 10:29
 **/
@SpringBootApplication(scanBasePackages = "top.xufilebox.**", exclude = DruidDataSourceAutoConfigure.class)
@MapperScan("top.xufilebox.common.mybatis.mapper")
public class FileApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class, args);
//        com.github.tobato.fastdfs.service.FastFileStorageClient storageClient;
////        storageClient.uploadFile()
//        com.github.tobato.fastdfs.service.TrackerClient trackerClient;
//        trackerClient.
    }
}

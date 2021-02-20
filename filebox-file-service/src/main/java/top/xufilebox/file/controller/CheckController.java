package top.xufilebox.file.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.xufilebox.common.result.Result;
import top.xufilebox.file.service.impl.FileServiceImpl;

/**
 * @description:
 * @author: alextian
 * @create: 2021-02-05 20:47
 **/
@RestController
@RequestMapping("/file/check")
public class CheckController {
    @Autowired
    FileServiceImpl fileService;

//    @RequestMapping("/fileExist/{md5}")
//    public Result checkFileExist(@PathVariable("md5") String md5) {
//        return fileService.checkFileExist(md5);
//    }

}

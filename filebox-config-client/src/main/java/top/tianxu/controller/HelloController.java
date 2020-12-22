package top.tianxu.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description:
 * @author: alextian
 * @create: 2020-07-20 10:39
 **/
@Controller
@RefreshScope
public class HelloController {
    @Value("${foo}")
    private String foo;

    @ResponseBody
    @RequestMapping("/hello")
    public String hello() {
        return foo;
    }
}

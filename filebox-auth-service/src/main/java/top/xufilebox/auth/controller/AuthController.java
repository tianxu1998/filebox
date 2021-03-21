package top.xufilebox.auth.controller;


import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.xufilebox.auth.service.impl.AuthUserServiceImpl;
import top.xufilebox.auth.util.VerifyUtil;
import top.xufilebox.common.dto.CreateDTO;
import top.xufilebox.common.dto.LoginDTO;
import top.xufilebox.common.redis.RedisTemplateProxy;
import top.xufilebox.common.result.Result;
import top.xufilebox.common.result.ResultCode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @description:
 * @author: alextian
 * @create: 2020-12-19 10:46
 **/
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthUserServiceImpl userService;
    @Autowired
    RedisTemplateProxy redisTemplateProxy;

    /**
     * 检查用户名是否存在
     * @param userName
     * @return
     */
    @RequestMapping("/userExist/{userName}")
    public Result userExist(@PathVariable("userName") String userName) {
        return userService.userExist(userName);
    }

    /**
     * 生成验证码， 返回base64编码的图片
     * @return
     * @throws Exception
     */
    @RequestMapping("/getVerifyCode")
    public Result getVerifyCode() throws Exception {
        Map map = new HashMap();
        //第一个参数是生成的验证码，第二个参数是生成的图片
        Object[] objs = VerifyUtil.createImage();
        //将图片输出给浏览器
        BufferedImage image = (BufferedImage) objs[1];
        String value = objs[0].toString().toLowerCase();
        String key = UUID.randomUUID().toString();
        redisTemplateProxy.setEX(key, value, 1000 * 60 * 2);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // 使用ServletOutputStream流可直接输出图片
//        ServletOutputStream outputStream = response.getOutputStream();
        ImageIO.write(image, "png", outputStream);
        Base64 base64 = new Base64();
        String pic = new String(base64.encode(outputStream.toByteArray()));
        map.put("codeKey", key);
        map.put("codePic", pic);
        return Result.success(ResultCode.SUCCESS, map);
    }

    /**
     * 登陆
     * @param loginDTO
     * @return
     */
    @RequestMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

    /**
     * 注册用户
     * @param createDTO
     * @return
     */
    @RequestMapping("/create")
    public Result create(@RequestBody CreateDTO createDTO) {
        return userService.create(createDTO);
    }

    /**
     * 登出
     * @param token
     * @return
     */
    @RequestMapping("/logout")
    public Result logout(@RequestParam(name = "token") String token) {
        return userService.logout(token);
    }
}

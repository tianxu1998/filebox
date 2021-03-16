package top.xufilebox.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.xufilebox.common.dto.UpdatePasswordDTO;
import top.xufilebox.common.dto.UserInfoDTO;
import top.xufilebox.common.result.Result;
import top.xufilebox.user.service.impl.UserServiceImpl;

import javax.servlet.http.HttpServletRequest;

/**
 * @description:
 * @author: alextian
 * @create: 2020-12-15 19:52
 **/
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserServiceImpl userService;

    @RequestMapping("/findUserInfo")
    public Result<UserInfoDTO> findUserInfo(@RequestHeader("userName") String userName) {
        return userService.findUserInfo(userName);
    }

    @RequestMapping("/updateUserInfo")
    public Result updateUserInfo(HttpServletRequest request, @RequestHeader("userId") String userId, @RequestBody UserInfoDTO userInfo) {
        return userService.update(userId, userInfo);
    }

    @RequestMapping("/updatePassword")
    public Result updatePassword(@RequestHeader("userId") String userId, @RequestBody UpdatePasswordDTO updatePasswordDTO) {
        return userService.updatePassword(userId, updatePasswordDTO);
    }

    @RequestMapping("/findTimeline")
    public Result findTimeline(@RequestHeader("userId") String userId) {
        return userService.findTimeline(userId);
    }

    @RequestMapping("/rootDirId")
    public Result<Integer> getRootDirId(@RequestHeader("userId") String userId) {
        return userService.getRootDirId(userId);
    }
}

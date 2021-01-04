package top.xufilebox.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.xufilebox.common.annotation.ReadOnly;
import top.xufilebox.common.dto.CreateDTO;
import top.xufilebox.common.dto.LoginDTO;
import top.xufilebox.common.dto.UpdatePasswordDTO;
import top.xufilebox.common.dto.UserInfoDTO;
import top.xufilebox.common.mybatis.entity.Directory;
import top.xufilebox.common.mybatis.entity.User;
import top.xufilebox.common.mybatis.entity.UserRole;
import top.xufilebox.common.mybatis.mapper.DirectoryMapper;
import top.xufilebox.common.mybatis.mapper.UserMapper;
import top.xufilebox.common.mybatis.mapper.UserRoleMapper;
import top.xufilebox.common.mybatis.service.IUserService;
import top.xufilebox.common.redis.RedisTemplateProxy;
import top.xufilebox.common.result.Result;
import top.xufilebox.common.result.ResultCode;
import top.xufilebox.common.util.Constant;
import top.xufilebox.common.util.LogicalValue;

import javax.naming.ServiceUnavailableException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tianxu
 * @since 2020-12-18
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RedisTemplateProxy redisTemplateProxy;
    @Autowired
    UserRoleMapper userRoleMapper;
    @Autowired
    DirectoryMapper directoryMapper;

    @Override
    @ReadOnly
    public Result<Map<String, String>> login(LoginDTO loginDTO) {
        return Result.failed(ResultCode.FAILED);
    }

    @Override
    @Transactional
    public Result<Map<String, String>> create(CreateDTO createDTO) {
        return Result.failed(ResultCode.FAILED);
    }

    @Override
    public Result update(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserId, user.getUserId());
        userMapper.update(user, queryWrapper);
        return Result.success(ResultCode.SUCCESS, "更新用户成功");
    }

    public Result update(String userId, UserInfoDTO userInfo) {
        User user = new User();
        user.setName(userInfo.getName());
        user.setNickName(userInfo.getNickName());
        user.setPhone(userInfo.getPhone());
        user.setEmail(userInfo.getEmail());
        user.setUpdateTime(LocalDateTime.now());
        user.setUpdateBy(Integer.valueOf(userId));
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserId, Integer.valueOf(userId));
        userMapper.update(user, queryWrapper);
        return Result.success(ResultCode.SUCCESS, "更新用户成功");
    }

    @Override
    public Result<String> logout(String token) {
        return Result.failed();
    }

    @Override
    public Result userExist(String userName) {
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getUserName, userName);
        User user = userMapper.selectOne(query);
        if (user == null) {
            return Result.failed(ResultCode.USER_NOT_FOUND_ERROR, "此用户不存在");
        }
        return Result.success(ResultCode.SUCCESS, "此用户已经存在");
    }

    @ReadOnly
    public Result<UserInfoDTO> findUserInfo(String userName) {
        UserInfoDTO userInfo = userMapper.findUserInfo(userName);
        return Result.success(ResultCode.SUCCESS, userInfo);
    }

    public Result updatePassword(String userId, UpdatePasswordDTO updatePasswordDTO) {
        String oldPasswordEncoded = userMapper.findPasswordById(userId);
        if (!passwordEncoder.matches(updatePasswordDTO.getOldPassword(), oldPasswordEncoded)) {
            return Result.failed(ResultCode.USER_OLD_PASSWORD_ERROR, "旧密码错误");
        }
        User user = new User();
        user.setUserId(Integer.valueOf(userId));
        user.setPassword(passwordEncoder.encode(updatePasswordDTO.getNewPassword()));
        userMapper.updateById(user);
        return Result.success(ResultCode.SUCCESS, "更新密码成功");
    }
}

package top.xufilebox.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.xufilebox.auth.call.MailServiceFeign;
import top.xufilebox.auth.util.JwtTokenUtil;
import top.xufilebox.common.annotation.ReadOnly;
import top.xufilebox.common.dto.CreateDTO;
import top.xufilebox.common.dto.LoginDTO;
import top.xufilebox.common.dto.SendMailDTO;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static top.xufilebox.common.util.Constant.MAIL_CODE_EXPIRATION;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tianxu
 * @since 2020-12-18
 */
@Service
public class AuthUserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RedisTemplateProxy redisTemplateProxy;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    UserRoleMapper userRoleMapper;
    @Autowired
    DirectoryMapper directoryMapper;
    @Autowired
    MailServiceFeign mailClient;

    // 邮件模板
    public static final String EMAIL_TEMPLATE = "您好，您正在使用filebox文件存储平台，您的验证码是：【$】。\n如果不是本人操作，请忽略此邮件。";
    // 随机生成验证码的字符串
    public static final String BASE_CHAR = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";

    @Override
    @ReadOnly
    public Result<Map<String, String>> login(LoginDTO loginDTO) {
        String verifyCode = redisTemplateProxy.getValue(loginDTO.getVerifyCodeKey());
        if (StringUtils.isBlank(verifyCode) || verifyCode.equals("-1") || !verifyCode.equals(loginDTO.getVerifyCode())) {
            return Result.failed(ResultCode.USER_VERIFYCODE_ERROR);
        }
        redisTemplateProxy.delete(loginDTO.getVerifyCodeKey());

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserName, loginDTO.getUserName());
        User user = userMapper.selectOne(wrapper);
        if (user == null || user.getDelete() == LogicalValue.TRUE) {
            return Result.failed(ResultCode.USER_NOT_FOUND_ERROR);
        } else if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return Result.failed(ResultCode.USER_PASSWORD_ERROR);
        } else if (user.getDisable() == LogicalValue.TRUE) {
            return Result.failed(ResultCode.USER_DISABLED_ERROR);
        }
        Map<String, String> data = new HashMap<>();
        data.put("completed_information", String.valueOf(user.getCompletedInformation()));

        // 生成token
        Map<String, Object> claims = new HashMap<>();
        String userRoleName = userMapper.findUserRoleName(user.getUserId());
        claims.put("role", userRoleName);
        claims.put("userName", user.getUserName());
        claims.put("userId", user.getUserId());
        claims.put("nickName", user.getNickName());
        String token = jwtTokenUtil.generateToken(claims);
        data.put("token", token);
        redisTemplateProxy.setEX(token, user.getUserName(), jwtTokenUtil.getDefaultExpiration());
        return Result.success(ResultCode.SUCCESS, data);
    }

    @Override
    @Transactional
    public Result<Map<String, String>> create(CreateDTO createDTO) {
        // 校验验证码
        String verifyCode = redisTemplateProxy.getValue(createDTO.getVerifyCodeKey());
        if (verifyCode == null || verifyCode.equals("")
                || verifyCode.equals("-1") || !verifyCode.equals(createDTO.getVerifyCode())) {
            return Result.failed(ResultCode.USER_VERIFYCODE_ERROR);
        }
        // 删除redis中的验证码
        redisTemplateProxy.delete(createDTO.getVerifyCodeKey());

        // 1. 创建用户
        User user = this.createDefaultUser(createDTO);
        // 2. 创建用户主目录
        Directory directory = this.createDefaultDir(user.getUserId());
        // 3. 关联用户表与（角色和主目录）的id
        user.setRootDirId(directory.getDirId());
        update(user);
        createUserRole(user.getUserId());

        Map<String, String> data = new HashMap<>();
        data.put("completed_information", "0");
        // 生成token
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "user");
        claims.put("userName", user.getUserName());
        claims.put("userId", user.getUserId());
        claims.put("nickName", user.getNickName());
        String token = jwtTokenUtil.generateToken(claims);
        data.put("token", token);
        redisTemplateProxy.setEX(token, user.getUserName(), jwtTokenUtil.getDefaultExpiration());
        return Result.success(ResultCode.SUCCESS, data);
    }

    @Override
    public Result update(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserId, user.getUserId());
        userMapper.update(user, queryWrapper);
        return Result.success(ResultCode.SUCCESS, "更新用户成功");
    }

    @Override
    public Result<String> logout(String token) {
        redisTemplateProxy.delete(token);
        return Result.success();
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

    private User createDefaultUser(CreateDTO createDTO) {
        User user = new User();
        user.setCreateBy(-1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateBy(-1);
        user.setUpdateTime(LocalDateTime.now());
        user.setUserName(createDTO.getUserName());
        user.setPassword(passwordEncoder.encode(createDTO.getPassword()));
        user.setName(createDTO.getName());
        user.setNickName(createDTO.getNickName());
        user.setCapacity(Constant.DEFAULT_CAPACITY);
        userMapper.insert(user);
        return user;
    }

    private Directory createDefaultDir(int ownerId) {
        Directory directory = new Directory();
        directory.setCreateBy(-1);
        directory.setCreateTime(LocalDateTime.now());
        directory.setUpdateBy(-1);
        directory.setUpdateTime(LocalDateTime.now());
        directory.setParentDirId(Constant.DEFAULT_PARENT_DIR_ID);
        directory.setOwner(ownerId);
        directory.setDirName("home");
        directory.setPath("/");
        directoryMapper.insert(directory);
        return directory;
    }

    private UserRole createUserRole(int userId) {
        UserRole userRole = new UserRole();
        userRole.setCreateBy(-1);
        userRole.setCreateTime(LocalDateTime.now());
        userRole.setUpdateBy(-1);
        userRole.setUpdateTime(LocalDateTime.now());
        userRole.setUserId(userId);
        userRoleMapper.createDefaultUserRole(userRole);
        return userRole;
    }

    public Result sendMailVerifyCode(String userId) {
        String email = userMapper.findEmailById(Integer.valueOf(userId));
        if (StringUtils.isBlank(email) || email.indexOf("@") == -1) {
            return Result.failed(ResultCode.EMAIL_FORMAT_ERROR);
        }
        String verifyCode = randomVerifyCode();
        // 发送验证码邮件
        mailClient.sendTextMail(SendMailDTO.builder()
                .title("【xufilebox文件存储系统】【验证码】")
                .to(email)
                .template(EMAIL_TEMPLATE)
                .keyWords(Arrays.asList(verifyCode))
                .build());
        // redis存储邮箱验证码key的格式"userId_mail_mailAddress"
        redisTemplateProxy.setEX(userId + "_mail_" + email, verifyCode, MAIL_CODE_EXPIRATION);
        return Result.success();
    }

    /**
     * 生成随机六位的验证码，范围是大写小写字母和0-9数字
     * @return
     */
    private String randomVerifyCode() {
        Random rand = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 6; i++) {
            int idx = rand.nextInt(BASE_CHAR.length());
            sb.append(BASE_CHAR.charAt(idx));
        }
        return sb.toString();
    }
}

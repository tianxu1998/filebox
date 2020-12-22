package top.xufilebox.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.xufilebox.common.dto.CreateDTO;
import top.xufilebox.common.dto.LoginDTO;
import top.xufilebox.common.mybatis.entity.User;
import top.xufilebox.common.result.Result;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tianxu
 * @since 2020-12-18
 */
public interface IUserService extends IService<User> {
    Result<Map<String, String>> login(LoginDTO loginDTO);

    Result<Map<String, String>> create(CreateDTO createDTO);

    Result update(User user);
}

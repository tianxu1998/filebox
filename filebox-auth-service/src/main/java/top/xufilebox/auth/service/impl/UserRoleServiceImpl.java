package top.xufilebox.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.xufilebox.common.mybatis.service.IUserRoleService;
import top.xufilebox.common.mybatis.entity.UserRole;
import top.xufilebox.common.mybatis.mapper.UserRoleMapper;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tianxu
 * @since 2020-12-18
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

}

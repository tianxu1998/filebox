package top.xufilebox.common.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.xufilebox.common.mybatis.entity.User;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author tianxu
 * @since 2020-12-18
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    int count();
    String findUserRoleName(@Param("userId") int userId);

}

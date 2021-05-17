package top.xufilebox.common.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.xufilebox.common.dto.UserInfoDTO;
import top.xufilebox.common.mybatis.entity.User;

import java.util.Map;

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

    UserInfoDTO findUserInfo(@Param("userName") String userName);

    String findPasswordById(@Param("userId") String userId);

    Map<String, String> findTimeline(@Param("userId") String userId);

    Integer getRootDirId(@Param("userId") String userId);

    void updateUsedCapatity(@Param("fileSize") Long fileSize, @Param("userId") String userId);

    @Select("select email from u_user where user_id = #{userId}")
    String findEmailById(@Param("userId") Integer userId);
}

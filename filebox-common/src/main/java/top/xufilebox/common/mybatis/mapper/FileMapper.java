package top.xufilebox.common.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import top.xufilebox.common.mybatis.entity.File;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author tianxu
 * @since 2021-01-28
 */
public interface FileMapper extends BaseMapper<File> {

    Integer findUserIdByFileId(@Param("fileId") Integer fileId);
}

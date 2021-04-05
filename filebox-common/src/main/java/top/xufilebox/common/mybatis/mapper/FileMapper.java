package top.xufilebox.common.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import top.xufilebox.common.dto.FileInfoDTO;
import top.xufilebox.common.mybatis.entity.File;

import java.util.List;

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

    List<FileInfoDTO> listRootDirFile(@Param("userId") String userId);

    List<FileInfoDTO> listDirFile(@Param("dirId") Integer dirId);

    List<FileInfoDTO> listDir(@Param("dirId") Integer dirId);
}

package top.xufilebox.common.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.xufilebox.common.dto.FileInfoDTO;
import top.xufilebox.common.dto.ShareInfoDTO;
import top.xufilebox.common.dto.TransferSaveBaseDTO;
import top.xufilebox.common.dto.TransferSaveDTO;
import top.xufilebox.common.mybatis.entity.Share;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author tianxu
 * @since 2020-12-18
 */
@Mapper
public interface ShareMapper extends BaseMapper<Share> {

    void insertList(List<Share> shareList);

    List<FileInfoDTO> selectShareFileList(List<Integer> fileIds);

    void transferSave(List<TransferSaveBaseDTO> fileIds);

    List<ShareInfoDTO.ShareInfoItem> getSharInfo(@Param("userId") Integer userId);

    void disableShareUrl(@Param("shareUrl") String shareUrl);

    void enableShareUrl(@Param("shareUrl") String shareUrl);

    @Select("select max(create_time) as create_time from f_share where share_url = #{shareUrl}")
    LocalDateTime test(@Param("shareUrl") String shareUrl);
}

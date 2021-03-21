package top.xufilebox.common.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.xufilebox.common.dto.FileInfoDTO;
import top.xufilebox.common.dto.TransferSaveBaseDTO;
import top.xufilebox.common.dto.TransferSaveDTO;
import top.xufilebox.common.mybatis.entity.Share;

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
}

package top.xufilebox.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.xufilebox.common.annotation.ReadOnly;
import top.xufilebox.common.dto.FileInfoDTO;
import top.xufilebox.common.mybatis.entity.File;
import top.xufilebox.common.mybatis.mapper.FileMapper;
import top.xufilebox.common.result.Result;
import top.xufilebox.common.result.ResultCode;
import top.xufilebox.common.util.Constant;
import top.xufilebox.common.util.LogicalValue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileSearchServiceImpl  extends ServiceImpl<FileMapper, File> {
    @Autowired
    FileMapper fileMapper;

    /**
     * 根据文件名模糊搜索自己的文件
     * @param fileName
     * @param userId
     * @return
     */
    @ReadOnly
    public Result<List<FileInfoDTO>> searchByFileName(String fileName, String userId) {
        LambdaQueryWrapper<File> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(File::getOwner, Integer.valueOf(userId))
                .eq(File::getStatus, Constant.UPLOAD_COMPLETED)
                .like(File::getFileName, fileName);
        List<File> files = fileMapper.selectList(wrapper);
        return Result.success(ResultCode.SUCCESS, files.stream().map(file -> {
            FileInfoDTO dto = new FileInfoDTO();
            dto.setFrom(file.getFrom());
            dto.setFileId(file.getFileId());
            dto.setIsDir(LogicalValue.FALSE);
            dto.setSuffix(file.getFileName().substring(file.getFileName().lastIndexOf(".") + 1));
            dto.setFileName(file.getFileName());
            dto.setBlockNumber(file.getBlockNumber());
            dto.setSize(file.getSize());
            dto.setHash(file.getHash());
            dto.setUpdateTime(file.getUpdateTime());
            dto.setCreateTime(file.getCreateTime());
            return dto;
        }).collect(Collectors.toList()));
    }
}

package top.xufilebox.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.github.tobato.fastdfs.service.TrackerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.xufilebox.common.annotation.ReadOnly;
import top.xufilebox.common.dto.FileHashInfoDTO;
import top.xufilebox.common.dto.UploadFileDTO;
import top.xufilebox.common.mybatis.entity.Block;
import top.xufilebox.common.mybatis.entity.Directory;
import top.xufilebox.common.mybatis.entity.File;
import top.xufilebox.common.mybatis.mapper.BlockMapper;
import top.xufilebox.common.mybatis.mapper.DirectoryMapper;
import top.xufilebox.common.mybatis.mapper.FileMapper;
import top.xufilebox.common.mybatis.service.IFileService;
import top.xufilebox.common.result.Result;
import top.xufilebox.common.result.ResultCode;
import top.xufilebox.common.util.Constant;
import top.xufilebox.file.entity.Chunk;
import top.xufilebox.file.util.FastDFSUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

/**
 * @description:
 * @author: alextian
 * @create: 2021-02-05 20:55
 **/
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements IFileService {
    @Autowired
    FileMapper fileMapper;
    @Autowired
    DirectoryMapper directoryMapper;

    @Autowired
    TrackerClient trackerClient;
    @Autowired
    FastFileStorageClient storageClient;
    @Autowired
    FastDFSUtil fastDFSUtil;
    @Autowired
    BlockMapper blockMapper;


    @Transactional
    public Result<File> createFile(UploadFileDTO uploadFileDTO, String userId) {
        // 如果前端没有传来父文件夹的id 则默认上传到家目录
        if (uploadFileDTO.getParentDirId() == null || uploadFileDTO.getParentDirId() == 0) {
            LambdaQueryWrapper<Directory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper
                    .eq(Directory::getOwner, userId)
                    .eq(Directory::getParentDirId, Constant.DEFAULT_PARENT_DIR_ID);
            Directory directory = directoryMapper.selectOne(queryWrapper);
            uploadFileDTO.setParentDirId(directory.getDirId());
        }
        // 根据dto创建file实体
        File newFile = createFileFromUploadFileDTO(uploadFileDTO, userId);
        // 查询文件是否已经被上传过 判断条件是 存在相同的hash，并且上传状态为完成
        LambdaQueryWrapper<File> fileQueryWrapper = new LambdaQueryWrapper<>();
        fileQueryWrapper
                .eq(File::getHash, uploadFileDTO.getHash())
                .eq(File::getStatus, Constant.UPLOAD_COMPLETED);
        Integer file = fileMapper.selectCount(fileQueryWrapper);
        if (file <= 0) {
            // 此文件为未有用户上传到系统的情况
            newFile.setStatus(Constant.UPLOAD_UNCOMPLETED);
        } else {
            // 此文件之前已经有用户上传的情况
            newFile.setStatus(Constant.UPLOAD_COMPLETED);
        }
        fileMapper.insert(newFile);
        return Result.success(ResultCode.SUCCESS, newFile);
    }

    private File createFileFromUploadFileDTO(UploadFileDTO uploadFileDTO, String userId) {
        File newFile = new File();
        newFile.setFileName(uploadFileDTO.getFileName());
        newFile.setFrom(uploadFileDTO.getFrom());
        newFile.setParentDirId(uploadFileDTO.getParentDirId());
        newFile.setSize(uploadFileDTO.getSize());
        newFile.setHash(uploadFileDTO.getHash());
        newFile.setBlockNumber(uploadFileDTO.getBlockNumber());
        newFile.setUpdateTime(LocalDateTime.now());
        newFile.setCreateTime(LocalDateTime.now());
        newFile.setUpdateBy(Integer.valueOf(userId));
        newFile.setCreateBy(Integer.valueOf(userId));
        newFile.setOwner(Integer.valueOf(userId));
        return newFile;
    }

    @Transactional
    public Result uploadFile(Chunk chunk, String userId) throws IOException {
        // 如果此块分片已经上传过了  无需再次上传
        String fileName = chunk.getIdentifier() + "_" + chunk.getChunkNumber();
        LambdaQueryWrapper<Block> chunkHashWrapper = new LambdaQueryWrapper<>();
        chunkHashWrapper.eq(Block::getHash, fileName);
        Integer integer = blockMapper.selectCount(chunkHashWrapper);
        if (integer > 0) {
            return Result.success();
        }

        // 上传到fastdfs
        MultipartFile file = chunk.getFile();
        Integer chunkNumber = chunk.getChunkNumber();
        String ext = chunk.getFilename().substring(chunk.getFilename().lastIndexOf(".") + 1);
        if (chunkNumber == null) {
            chunkNumber = 0;
        }
        InputStream inputStream = file.getInputStream();
        String groupName = fastDFSUtil.randomGroupName();
        StorePath storePath = storageClient.uploadFile(groupName, inputStream, chunk.getCurrentChunkSize(), ext);

        // 持久化chunk存储信息到mysql
        Block newBlock = new Block();
        newBlock.setBlockSize(chunk.getCurrentChunkSize());
        newBlock.setFileHash(chunk.getIdentifier());
        newBlock.setGroup(storePath.getGroup());
        newBlock.setDfsPath(storePath.getFullPath());
        newBlock.setHash(fileName);
        newBlock.setBlockNumber(chunkNumber);
        newBlock.setUpdateTime(LocalDateTime.now());
        newBlock.setCreateTime(LocalDateTime.now());
        newBlock.setUpdateBy(Integer.valueOf(userId));
        newBlock.setCreateBy(Integer.valueOf(userId));

        blockMapper.insert(newBlock);
        return Result.success();
    }

    public Result<String> uploadFileSuccess(String md5, String userId) {
        File file = new File();
        file.setStatus(1);
        file.setUpdateBy(Integer.valueOf(userId));
        file.setUpdateTime(LocalDateTime.now());
        LambdaQueryWrapper<File> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(File::getHash, md5)
                .eq(File::getStatus, Constant.UPLOAD_UNCOMPLETED);
        fileMapper.update(file, queryWrapper);
        return Result.success();
    }

    @ReadOnly
    public Result uploadFileTest(Chunk chunk, String userId) {
        LambdaQueryWrapper<File> fileQueryWrapper = new LambdaQueryWrapper<>();
        fileQueryWrapper
                .eq(File::getHash, chunk.getIdentifier())
                .eq(File::getStatus, Constant.UPLOAD_COMPLETED);
        Integer file = fileMapper.selectCount(fileQueryWrapper);
        if (file <= 0) {
            // 此文件为未有用户上传到系统的情况
           return Result.success(ResultCode.FILE_UPLOAD_NOT_COMPLETED);
        } else {
            // 此文件之前已经有用户上传的情况
            return Result.success(ResultCode.FILE_UPLOAD_COMPLETED);
        }
    }

    public Result<FileHashInfoDTO> getFileBlocks(Integer fileId, String userId) {
        LambdaQueryWrapper<File> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(File::getFileId, fileId);
        File file = fileMapper.selectOne(queryWrapper);
        // 如果不是拥有者, 返回失败
        if (file.getOwner() != Integer.valueOf(userId)) {
            return Result.failed(ResultCode.NOT_FILE_OWNER);
        }
        // 文件上传未完成
        if (file.getStatus() != Constant.UPLOAD_COMPLETED) {
            return Result.failed(ResultCode.FILE_DONOT_EXIST);
        }
        FileHashInfoDTO res = new FileHashInfoDTO();
        res.setFileId(fileId);
        res.setFileHash(file.getHash());
        res.setBlockNumber(file.getBlockNumber());
        res.setFileName(file.getFileName());
        res.setFileSize(file.getSize());
        return Result.success(ResultCode.SUCCESS, res);
    }

    public Result download(HttpServletResponse response, String blockName) throws IOException {
        LambdaQueryWrapper<Block> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Block::getHash, blockName);
        Block blockInfo = blockMapper.selectOne(queryWrapper);

        response.addHeader("Content-Disposition", "attachment;filename=" + blockName);
        response.addHeader("Content-Length", "" + blockInfo.getBlockSize());
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        ServletOutputStream fos = response.getOutputStream();
        String res = storageClient.downloadFile(blockInfo.getGroup(),
                blockInfo.getDfsPath().substring(blockInfo.getDfsPath().indexOf("/") + 1),
                input -> {
                    byte[] buf = new byte[2024];
                    int len = -1;
                    while ((len = input.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    input.close();
                    fos.flush();
                    return "下载分块完成";
                });
        return Result.success(ResultCode.SUCCESS, res);
    }
}

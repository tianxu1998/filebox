package top.xufilebox.file.service.impl;

import com.alibaba.druid.support.json.JSONUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.github.tobato.fastdfs.service.TrackerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.xufilebox.common.annotation.ReadOnly;
import top.xufilebox.common.dto.*;
import top.xufilebox.common.mybatis.entity.Block;
import top.xufilebox.common.mybatis.entity.Directory;
import top.xufilebox.common.mybatis.entity.File;
import top.xufilebox.common.mybatis.entity.Share;
import top.xufilebox.common.mybatis.mapper.*;
import top.xufilebox.common.mybatis.service.IFileService;
import top.xufilebox.common.result.Result;
import top.xufilebox.common.result.ResultCode;
import top.xufilebox.common.util.Constant;
import top.xufilebox.common.util.LogicalValue;
import top.xufilebox.file.entity.Chunk;
import top.xufilebox.file.util.AESUtil;
import top.xufilebox.file.util.FastDFSUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    UserMapper userMapper;

    @Autowired
    TrackerClient trackerClient;
    @Autowired
    FastFileStorageClient storageClient;
    @Autowired
    FastDFSUtil fastDFSUtil;
    @Autowired
    BlockMapper blockMapper;
    @Autowired
    AESUtil aesUtil;
    @Autowired
    ShareMapper shareMapper;




    @Transactional
    public Result<File> createFile(UploadFileDTO uploadFileDTO, String userId) {
        // 如果前端没有传来父文件夹的id 则默认上传到家目录
        if (uploadFileDTO.getParentDirId() == null || uploadFileDTO.getParentDirId() == -1) {
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

    public Result<List<FileInfoDTO>> listRootDirFile(String userId) {
        List<FileInfoDTO> fileInfoDTOS = fileMapper.listRootDirFile(userId);
        fileInfoDTOS.forEach(it -> {
            if (it.getIsDir() == LogicalValue.FALSE) {
                it.setSuffix(it.getFileName().substring(it.getFileName().lastIndexOf(".") + 1));
            }
        });
        return Result.success(ResultCode.SUCCESS, fileInfoDTOS);
    }

    @ReadOnly
    public Result<Integer> getRootDirId(String userId) {
        return Result.success(ResultCode.SUCCESS, userMapper.getRootDirId(userId));
    }

    public Result<FileInfoDTO> createNewDir(String userId, CreateNewDirDTO createNewDirDTO) {
        Directory newDir = this.createNewDirFromDTO(userId, createNewDirDTO);
        directoryMapper.insert(newDir);
        FileInfoDTO fileInfoDTO = new FileInfoDTO();
        fileInfoDTO.setFileId(newDir.getDirId());
        fileInfoDTO.setFileName(newDir.getDirName());
        fileInfoDTO.setCreateTime(newDir.getCreateTime());
        fileInfoDTO.setUpdateTime(newDir.getUpdateTime());
        fileInfoDTO.setBlockNumber(0);
        fileInfoDTO.setHash("--");
        fileInfoDTO.setFrom("本地创建");
        fileInfoDTO.setIsDir(1);
        fileInfoDTO.setSize(0L);
        return Result.success(ResultCode.SUCCESS, fileInfoDTO);
    }

    private Directory createNewDirFromDTO(String userId, CreateNewDirDTO createNewDirDTO) {
        Directory dir = new Directory();
        dir.setDirName(Constant.DEFAULT_DIR_NAME);
        dir.setPath(createNewDirDTO.getCurPath() + "/" + Constant.DEFAULT_DIR_NAME);
        dir.setParentDirId(createNewDirDTO.getCurDirId());
        dir.setCreateTime(LocalDateTime.now());
        dir.setUpdateTime(LocalDateTime.now());
        dir.setOwner(Integer.valueOf(userId));
        dir.setUpdateBy(Integer.valueOf(userId));
        dir.setCreateBy(Integer.valueOf(userId));
        return dir;
    }

    @ReadOnly
    public Result<List<FileInfoDTO>> listDirFile(String userId, Integer dirId) {
        Directory directory = directoryMapper.selectById(dirId);
        if (directory.getOwner() != Integer.valueOf(userId)) {
            return Result.failed(ResultCode.NOT_FILE_OWNER);
        }
        List<FileInfoDTO> files = fileMapper.listDirFile(dirId);
        return Result.success(ResultCode.SUCCESS, files);
    }

    public Result<String> generateUrl(String userId, GenerateUrlDTO generateUrlDTO) throws JsonProcessingException {
        List<Share> shareList = createShareFromDTO(userId, generateUrlDTO);
        shareMapper.insertList(shareList);
        String shareUrl = shareList.get(0).getShareUrl();
        return Result.success(ResultCode.SUCCESS, shareUrl);
    }

    @ReadOnly
    public Result<List<FileInfoDTO>> decodeUrl(String url) {
        Map<String, Object> dtoInfoMap = parseGenerateUrlDTO(url);
        // 检查分享是否过期
        if (expireCheck(dtoInfoMap.get("shareTime").toString(),
                Integer.valueOf(dtoInfoMap.get("term").toString()))) {
            return Result.failed(ResultCode.SHARE_EXPIRED);
        }
        List<FileInfoDTO> res = shareMapper.selectShareFileList((List<Integer>) dtoInfoMap.get("fileIds"));
        res.forEach(item -> item.setFrom(dtoInfoMap.get("from").toString()));
        return Result.success(ResultCode.SUCCESS, res);
    }


    /**
     * 保存用户分享到我的文件
     * @param userId
     * @param request
     * @return
     */
    public Result<String> transferSave(String userId, TransferSaveDTO request) {
        Map<String, Object> dtoInfoMap = parseGenerateUrlDTO(request.getUrl());
        // 检查分享是否过期
        if (expireCheck(dtoInfoMap.get("shareTime").toString(),
                Integer.valueOf(dtoInfoMap.get("term").toString()))) {
            return Result.failed(ResultCode.SHARE_EXPIRED);
        }
        List<Integer> fileIds = (List<Integer>) dtoInfoMap.get("fileIds");
        shareMapper.transferSave(fileIds.stream().map(fileId -> {
            TransferSaveBaseDTO dto = new TransferSaveBaseDTO();
            dto.setFileId(fileId);
            dto.setFrom(dtoInfoMap.get("from").toString());
            dto.setParentDirId(request.getToDirId());
            dto.setUserId(Integer.valueOf(userId));
            return dto;
        }).collect(Collectors.toList()));
        return Result.success();
    }

    /**
     * 检查分享文件是否过期
     * @param shareTime 分享时间
     * @param term 有效期 以天为单位
     * @return
     */
    private boolean expireCheck(String shareTime, Integer term) {
        return LocalDateTime.parse(shareTime, GenerateUrlDTO.DTF)
                .plusDays(term)
                .isBefore(LocalDateTime.now());
    }

    /**
     * 根据generateUrlDTO生成share_url
     * @param generateUrlDTO
     * @return 加密后的share_url
     */
    private String generateUrl(GenerateUrlDTO generateUrlDTO) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonStr = objectMapper.writeValueAsString(generateUrlDTO);
        String ret = aesUtil.ecodes(jsonStr);
        return ret;
    }

    /**
     * 根据加密后的url解析
     * @param url
     * @return
     */
    private Map<String, Object> parseGenerateUrlDTO(String url) {
        String dtoJsonStr = aesUtil.dcodes(url);
        return (Map<String, Object>) JSONUtils.parse(dtoJsonStr);
    }

    private List<Share> createShareFromDTO(String userId, GenerateUrlDTO generateUrlDTO) throws JsonProcessingException {
        String url = generateUrl(generateUrlDTO);
        List<Integer> fileIds = generateUrlDTO.getFileIds();
        List<Share> shareList = fileIds.stream().map(fileId -> {
            Share share = new Share();
            share.setCreateBy(Integer.valueOf(userId));
            share.setCreateTime(LocalDateTime.now());
            share.setShareUrl(url);
            share.setFileId(fileId);
            share.setFromUserName(generateUrlDTO.getFrom());
            share.setEffectiveTime(generateUrlDTO.getTerm());
            share.setDisable(LogicalValue.FALSE);
            share.setUpdateBy(Integer.valueOf(userId));
            share.setUpdateTime(LocalDateTime.now());
            return share;
        }).collect(Collectors.toList());
        return shareList;
    }


}

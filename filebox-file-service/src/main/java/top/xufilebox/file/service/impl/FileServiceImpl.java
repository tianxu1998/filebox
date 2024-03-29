package top.xufilebox.file.service.impl;

import com.alibaba.druid.support.json.JSONUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.github.tobato.fastdfs.service.TrackerClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;
import top.xufilebox.common.annotation.ReadOnly;
import top.xufilebox.common.dto.*;
import top.xufilebox.common.mybatis.entity.*;
import top.xufilebox.common.mybatis.mapper.*;
import top.xufilebox.common.mybatis.service.IFileService;
import top.xufilebox.common.result.Result;
import top.xufilebox.common.result.ResultCode;
import top.xufilebox.common.util.Constant;
import top.xufilebox.common.util.LogicalValue;
import top.xufilebox.file.entity.Chunk;
import top.xufilebox.file.util.AESUtil;
import top.xufilebox.file.util.FastDFSUtil;
import top.xufilebox.common.dto.ShareInfoDTO.*;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: alextian
 * @create: 2021-02-05 20:55
 **/
@Service
@Slf4j
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
//    加synchronized避免分片重试时存在并发问题，导致重复上传 TODO 应该加分布式锁
    public synchronized Result uploadFile(Chunk chunk, String userId) throws IOException {
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
        // 随机获得文件分组名称
        String groupName = fastDFSUtil.randomGroupName();
        // 存到fastdfs
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
        int row = blockMapper.insert(newBlock);
        return row == 1 ? Result.success() : Result.failed();
    }

    /**
     * 该md5的文件上传完成， 更新该md5对应的文件上传状态为完成
     *
     * @param md5
     * @param userId
     * @return
     */
    @Transactional
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
        // 更新用户已使用的存储空间， TODO 存在并发问题，应该使用分布式锁优化
        Long fileSize = fileMapper.findFileSizeByHash(md5);
        userMapper.updateUsedCapatity(fileSize, userId);
        return Result.success();
    }

    /**
     * 校验该md5的文件是否已经存在系统
     * @param chunk
     * @param userId
     * @return
     */
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

    /**
     *     根据fileId获取文件的hash和分块数量等
     */
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

    /**
     * 从文件存储集群下载
     * @param response
     * @param blockName
     * @return
     * @throws IOException
     */
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

    @ReadOnly
    public Result<List<FileInfoDTO>> listDir(String userId, Integer dirId) {
        Directory directory = directoryMapper.selectById(dirId);
        if (directory.getOwner() != Integer.valueOf(userId)) {
            return Result.failed(ResultCode.NOT_FILE_OWNER);
        }
        List<FileInfoDTO> dirs = fileMapper.listDir(dirId);
        return Result.success(ResultCode.SUCCESS, dirs);
    }

    /**
     * 列出家目录的文件夹
     * @param userId
     * @return
     */
    @ReadOnly
    public Result<List<FileInfoDTO>> listRootDir(String userId) {
        List<FileInfoDTO> dirs = fileMapper.listRootDir(userId);
        return Result.success(ResultCode.SUCCESS, dirs);
    }

    /**
     * 插入分享信息到数据库
     */
    @Transactional
    public Result<String> generateUrl(String userId, GenerateUrlDTO generateUrlDTO) throws JsonProcessingException {
        List<Share> shareList = createShareFromDTO(userId, generateUrlDTO);
        shareMapper.insertList(shareList);
        String shareUrl = shareList.get(0).getShareUrl();
        return Result.success(ResultCode.SUCCESS, shareUrl);
    }

    @ReadOnly
    public Result<List<FileInfoDTO>> decodeUrl(String url) {
        GenerateUrlDTO dto = parseGenerateUrlDTO(url);
        // 检查分享是否过期
        if (expireCheck(dto.getShareTime(), dto.getTerm())) {
            return Result.failed(ResultCode.SHARE_EXPIRED);
        }
        // 检查分享链接是否禁用
        if (disableCheck(url)) {
            return Result.failed(ResultCode.SHARE_DISABLED);
        }
        List<FileInfoDTO> res = shareMapper.selectShareFileList(dto.getFileIds());
        res.forEach(item -> {
            item.setFrom(dto.getFrom());
            item.setSuffix(item.getFileName().substring(item.getFileName().lastIndexOf(".") + 1));
        });
        return Result.success(ResultCode.SUCCESS, res);
    }

    /**
     *  检查分享链接是否禁用
     * @param shareUrl
     * @return 返回true禁用， false未禁用
     */
    private boolean disableCheck(String shareUrl) {
        LambdaQueryWrapper<Share> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(Share::getShareUrl, shareUrl)
                .eq(Share::getDisable, 1);
        return !shareMapper.selectList(queryWrapper).isEmpty();
    }

    /**
     * 重命名文件
     * @param userId 用户Id
     * @param request 新文件名字 和 文件Id
     * @return
     */
    public Result<String> rename(String userId, FileRenameDTO request) {
        File file = fileMapper.selectById(request.getFileId());
        if (!userId.equals(file.getOwner().toString())) {
            return Result.failed(ResultCode.NOT_FILE_OWNER, "不是文件拥有者");
        }
        file.setFileName(request.getNewName());
        fileMapper.updateById(file);
        return Result.success();
    }

    /**
     * 重命名文件夹
     * @param userId 用户Id
     * @param request 新文件夹名字 和 文件夹Id
     * @return
     */
    public Result<String> renameDir(String userId, FileRenameDTO request) {
        Directory file = directoryMapper.selectById(request.getFileId());
        if (!userId.equals(file.getOwner().toString())) {
            return Result.failed(ResultCode.NOT_FILE_OWNER, "不是文件拥有者");
        }
        file.setDirName(request.getNewName());
        directoryMapper.updateById(file);
        return Result.success();
    }

    /**
     * 保存用户分享到我的文件
     * @param userId
     * @param request
     * @return
     */
    public Result<String> transferSave(String userId, TransferSaveDTO request) {
        GenerateUrlDTO dto = parseGenerateUrlDTO(request.getUrl());
        // 检查分享是否过期
        if (expireCheck(dto.getShareTime(), dto.getTerm())) {
            return Result.failed(ResultCode.SHARE_EXPIRED);
        }
        // 检查是否禁用
        if (disableCheck(request.getUrl())) {
            return Result.failed(ResultCode.SHARE_DISABLED);
        }
        List<Integer> fileIds = (dto.getFileIds());
        TransferSaveBaseDTO baseDTO = new TransferSaveBaseDTO();
        baseDTO.setFrom(baseDTO.getFrom());
        baseDTO.setParentDirId(request.getToDirId());
        baseDTO.setUserId(Integer.valueOf(userId));
        shareMapper.transferSave(baseDTO, fileIds);
        return Result.success();
    }

    /**
     * 检查分享文件是否过期
     * @param shareTime 分享时间
     * @param term 有效期 以天为单位
     * @return
     */
    private boolean expireCheck(String shareTime, Integer term) {
        return expireCheck(LocalDateTime.parse(shareTime, GenerateUrlDTO.DTF), term);
    }

    private boolean expireCheck(LocalDateTime time, Integer term) {
        return time.plusDays(term)
                   .isBefore(LocalDateTime.now());
    }

    /**
     * 检查是否文件拥有者
     * @param userName
     * @param shareUrl
     * @return true代表是文件拥有者 false不是
     */
    public boolean shareUrlOwnerCheck(String userName, String shareUrl) {
        String jsonStr = aesUtil.dcodes(shareUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            GenerateUrlDTO dto = objectMapper.readValue(jsonStr, GenerateUrlDTO.class);
            if (!userName.equals(dto.getFrom())) {
                return false;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 检查分享文件是否过期
     * @param shareUrl
     * @return true过期， false未过期
     */
    public boolean shareUrlExpiredCheck(String shareUrl) {
        String jsonStr = aesUtil.dcodes(shareUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            GenerateUrlDTO dto = objectMapper.readValue(jsonStr, GenerateUrlDTO.class);
            return expireCheck(dto.getShareTime(), dto.getTerm());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return true;
        }
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
    private GenerateUrlDTO parseGenerateUrlDTO(String url) {
        String dtoJsonStr = aesUtil.dcodes(url);
        ObjectMapper objectMapper = new ObjectMapper();
        GenerateUrlDTO ret = null;
        try {
            ret = objectMapper.readValue(dtoJsonStr, GenerateUrlDTO.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ret;
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


//    @ReadOnly
    public Result<ShareInfoDTO> getShareInfo(String userId) {
        ShareInfoDTO data = new ShareInfoDTO();
        List<ShareInfoItem> items = shareMapper.getSharInfo(Integer.valueOf(userId));
        items.forEach(item -> {
            Integer expired = expireCheck(item.getShareDate(), item.getTerm()) ? LogicalValue.TRUE : LogicalValue.FALSE;
            item.setExpired(expired);
        });
        Collections.sort(items,
                        Comparator.comparingInt(ShareInfoItem::getExpired)
                                .thenComparing(ShareInfoItem::getShareDate, Comparator.reverseOrder()));
        data.setItems(items);
        return Result.success(ResultCode.SUCCESS, data);
    }

    /**
     * 禁用url
     * @param shareUrl
     * @return
     */
    public Result<String> disableShareUrl(String shareUrl) {
        shareMapper.disableShareUrl(shareUrl);
        return Result.success(ResultCode.SUCCESS, "禁用成功");
    }

    /**
     * 启用url
     * @param shareUrl
     * @return
     */
    public Result<String> enableShareUrl(String shareUrl) {
        shareMapper.enableShareUrl(shareUrl);
        return Result.success(ResultCode.SUCCESS, "启用成功");
    }

    /**
     * 删除指定fileId的文件
     * @param userId
     * @param fileId
     * @return
     */
    public Result<String> deleteFile(String userId, String fileId) {
        // 检查是否对应文件拥有者
        Integer userIdByFileId = fileMapper.findUserIdByFileId(Integer.valueOf(fileId));
        if (!userId.equals(String.valueOf(userIdByFileId))) {
            return Result.failed(ResultCode.NOT_FILE_OWNER);
        }
        fileMapper.deleteById(Integer.valueOf(fileId));
        return Result.success();
    }

    /**
     * 删除指定dirId的文件
     * @param userId
     * @param dirId
     * @return
     */
    @Transactional
    public Result<String> deleteDir(String userId, String dirId) {
        // 检查是否对应文件夹拥有者
        Integer userIdByDirId = directoryMapper.findUserIdByDirId(dirId);
        if (!userId.equals(String.valueOf(userIdByDirId))) {
            return Result.failed(ResultCode.NOT_FILE_OWNER);
        }
        // 删除其下的所有文件和文件夹 TODO 需要改成递归删除
        LambdaQueryWrapper<File> deleteFileWrapper = Wrappers.lambdaQuery();
        deleteFileWrapper.eq(File::getParentDirId, Integer.valueOf(dirId));
        fileMapper.delete(deleteFileWrapper);
        // 删除当前文件夹
        int row = directoryMapper.deleteById(Integer.valueOf(dirId));
        return row == 1 ? Result.success() : Result.failed();
    }

    public Result<String> moveFiles(String userId, MoveFileDTO moveFileDTO) {
        // 检查是否对应文件的拥有者
        LambdaQueryWrapper<File> checkOwnerWrapper = Wrappers.lambdaQuery();
        if (moveFileDTO.getFileIds() == null || moveFileDTO.getFileIds().isEmpty()) {
            return Result.failed(ResultCode.FAILED);
        }
        checkOwnerWrapper.in(File::getFileId, moveFileDTO.getFileIds())
                        .eq(File::getOwner, Integer.valueOf(userId));
        Integer count = fileMapper.selectCount(checkOwnerWrapper);
        if (count != moveFileDTO.getFileIds().size()) {
            return Result.failed(ResultCode.FAILED);
        }
        File file = new File();
        file.setParentDirId(moveFileDTO.getToDir());
        fileMapper.update(file, checkOwnerWrapper);
        return Result.success(ResultCode.SUCCESS);
    }
}

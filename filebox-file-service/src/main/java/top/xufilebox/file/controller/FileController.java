package top.xufilebox.file.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.xufilebox.common.dto.*;
import top.xufilebox.common.mybatis.entity.File;
import top.xufilebox.common.result.Result;
import top.xufilebox.file.entity.Chunk;
import top.xufilebox.file.service.impl.FileServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @description:
 * @author: alextian
 * @create: 2021-02-05 21:02
 **/
@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    FileServiceImpl fileService;

    /**
     * 创建逻辑文件
     * @param uploadFileDTO
     * @param userId
     * @return
     */
    @RequestMapping("/upload")
    public Result<File> createFile(@RequestBody UploadFileDTO uploadFileDTO, @RequestHeader("userId") String userId) {
        return fileService.createFile(uploadFileDTO, userId);
    }

    /**
     * 对应md5的文件上传完成
     * @param md5
     * @param userId
     * @return
     */
    @RequestMapping("/uploadFileSuccess/{md5}")
    public Result<String> uploadFileSuccess(@PathVariable("md5") String md5, @RequestHeader("userId") String userId) {
        return fileService.uploadFileSuccess(md5, userId);
    }

    /**
     * 上传文件接口
     * @param request
     * @param chunk
     * @param userId
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/uploadFile")
    public Result upload(HttpServletRequest request,Chunk chunk, @RequestHeader("userId") String userId) throws IOException {
        return fileService.uploadFile(chunk, userId);
    }

    /**
     * 上传文件check， 秒传的实现得益于此
     * @param request
     * @param chunk
     * @param userId
     * @return
     * @throws IOException
     */
    @GetMapping(value = "/uploadFile")
    public Result uploadTest(HttpServletRequest request,Chunk chunk, @RequestHeader("userId") String userId) throws IOException {
        return fileService.uploadFileTest(chunk, userId);
    }

    /**
     *     根据fileId获取文件的hash和分块数量等
     */
    @RequestMapping("/fileBlocks/{fileId}")
    public Result<FileHashInfoDTO> getFileBlocks(@PathVariable("fileId") Integer fileId, @RequestHeader("userId") String userId) {
        return fileService.getFileBlocks(fileId, userId);
    }

    /**
     * 下载文件
     * @param response
     * @param blockName 文件的分块hash
     * @return
     * @throws IOException
     */
    @RequestMapping("/download/{blockName}")
    public Result download(HttpServletResponse response, @PathVariable("blockName") String blockName) throws IOException {
        return fileService.download(response, blockName);
    }

    /**
     * 查询根目录下的文件（夹）列表
     * @param userId
     * @return
     */
    @RequestMapping("/list/home")
    public Result<List<FileInfoDTO>> listRootDirFile(@RequestHeader("userId") String userId) {
        return fileService.listRootDirFile(userId);
    }

    /**
     * 获得用户根文件夹的id
     * @param userId
     * @return
     */
    @RequestMapping("/rootDirId")
    public Result<Integer> getRootDirId(@RequestHeader("userId") String userId) {
        return fileService.getRootDirId(userId);
    }

    /**
     * 新建一个文件夹
     * @param createNewDirDTO
     * @param userId
     * @return
     */
    @RequestMapping("/createNewDir")
    public Result<FileInfoDTO> createNewDir(@RequestBody CreateNewDirDTO createNewDirDTO, @RequestHeader("userId") String userId) {
        return fileService.createNewDir(userId, createNewDirDTO);
    }

    /**
     * 查询某个文件夹下的文件（夹）列表
     * @param userId
     * @param dirId
     * @return
     */
    @RequestMapping("/list/{dirId}")
    public Result<List<FileInfoDTO>> listDirFile(@RequestHeader("userId") String userId, @PathVariable("dirId") Integer dirId) {
        return fileService.listDirFile(userId, dirId);
    }

    /**
     * 查询某个文件夹下的文件夹列表
     * @param userId
     * @param dirId
     * @return
     */
    @RequestMapping("/listDir/{dirId}")
    public Result<List<FileInfoDTO>> listDir(@RequestHeader("userId") String userId, @PathVariable("dirId") Integer dirId) {
        return fileService.listDir(userId, dirId);
    }

    /**
     * 查询根目录下的文件夹列表
     * @param userId
     * @param dirId
     * @return
     */
    @RequestMapping("/listDir/home")
    public Result<List<FileInfoDTO>> listDir(@RequestHeader("userId") String userId) {
        return fileService.listRootDir(userId);
    }

    /**
     * 重命名文件
     * @param userId
     * @param request
     * @return
     */
    @PostMapping("/rename")
    public Result<String> rename(@RequestHeader("userId") String userId,
                                 @RequestBody FileRenameDTO request) {
        return fileService.rename(userId, request);
    }

    /**
     * 重命名文件夹
     * @param userId
     * @param request
     * @return
     */
    @PostMapping("/renameDir")
    public Result<String> renameDir(@RequestHeader("userId") String userId,
                                 @RequestBody FileRenameDTO request) {
        return fileService.renameDir(userId, request);
    }

    /**
     * 删除指定fileId的文件
     * @param userId
     * @param fileId
     * @return
     */
    @GetMapping("/deleteFile/{fileId}")
    public Result<String> deleteFile(@RequestHeader("userId") String userId,
                                    @PathVariable("fileId") String fileId) {
        return fileService.deleteFile(userId, fileId);
    }

    /**
     * 删除指定dirId的文件夹以及其下文件
     * @param userId
     * @param dirId
     * @return
     */
    @GetMapping("/deleteDir/{dirId}")
    public Result<String> deleteDir(@RequestHeader("userId") String userId,
                                     @PathVariable("dirId") String dirId) {
        return fileService.deleteDir(userId, dirId);
    }




//    @Autowired
//    TrackerClient trackerClient;
//    @Autowired
//    FastFileStorageClient storageClient;
//    @Autowired
//    BlockMapper blockMapper;
//    @RequestMapping("/deleteAll")
//    public Result deleteAllFile() {
//        LambdaQueryWrapper<Block> queryWrapper = new LambdaQueryWrapper<>();
//        List<Block> blocks = blockMapper.selectList(queryWrapper);
//        System.out.println(trackerClient.listGroups().get(0));
//        for (Block block : blocks) {
//            storageClient.deleteFile(block.getDfsPath());
//        }
////        storageClient.queryFileInfo("group1", "");
//        System.out.println(trackerClient.listGroups().get(0));
//        return Result.success();
//    }
//    @RequestMapping("/get")
//    public Result getFile() {
//        FileInfo group1 = storageClient.queryFileInfo("group1", "M00/00/03/rBgP92AgDPOANnQeAnW7xmFxDMU416.pdf");
//        return Result.success(ResultCode.SUCCESS, group1.toString());
//    }

}

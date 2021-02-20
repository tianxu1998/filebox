package top.xufilebox.file.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.github.tobato.fastdfs.domain.fdfs.FileInfo;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.github.tobato.fastdfs.service.TrackerClient;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.xufilebox.common.dto.FileHashInfoDTO;
import top.xufilebox.common.dto.UploadFileDTO;
import top.xufilebox.common.mybatis.entity.Block;
import top.xufilebox.common.mybatis.entity.File;
import top.xufilebox.common.mybatis.mapper.BlockMapper;
import top.xufilebox.common.result.Result;
import top.xufilebox.common.result.ResultCode;
import top.xufilebox.file.entity.Chunk;
import top.xufilebox.file.service.impl.FileServiceImpl;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @description:
 * @author: alextian
 * @create: 2021-02-05 21:02
 **/
@CrossOrigin
@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    FileServiceImpl fileService;

    @RequestMapping("/upload")
    public Result<File> createFile(@RequestBody UploadFileDTO uploadFileDTO, @RequestHeader("userId") String userId) {
        return fileService.createFile(uploadFileDTO, userId);
    }

    @RequestMapping("/uploadFileSuccess/{md5}")
    public Result<String> uploadFileSuccess(@PathVariable("md5") String md5, @RequestHeader("userId") String userId) {
        return fileService.uploadFileSuccess(md5, userId);
    }

//    @RequestMapping(value = "/uploadFile")
//    public Result upload(HttpServletRequest request,Chunk chunk, @RequestHeader("userId") String userId) throws IOException {
//        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
//        if (!isMultipart) return Result.failed(ResultCode.NOT_MULTIPARTCONTENT);
//        return fileService.uploadFile(chunk, userId);
//    }
    @PostMapping(value = "/uploadFile")
    public Result upload(HttpServletRequest request,Chunk chunk, @RequestHeader("userId") String userId) throws IOException {
//        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
//        if (!isMultipart) return Result.failed(ResultCode.NOT_MULTIPARTCONTENT);
        return fileService.uploadFile(chunk, userId);
    }

    @GetMapping(value = "/uploadFile")
    public Result uploadTest(HttpServletRequest request,Chunk chunk, @RequestHeader("userId") String userId) throws IOException {
//        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
//        if (!isMultipart) return Result.failed(ResultCode.NOT_MULTIPARTCONTENT);
        return fileService.uploadFileTest(chunk, userId);
    }

    // 根据fileId获取文件的hash和分块数量等
    @RequestMapping("/fileBlocks/{fileId}")
    public Result<FileHashInfoDTO> getFileBlocks(@PathVariable("fileId") Integer fileId, @RequestHeader("userId") String userId) {
        return fileService.getFileBlocks(fileId, userId);
    }

    @RequestMapping("/download/{blockName}")
    public Result download(HttpServletResponse response, @PathVariable("blockName") String blockName) throws IOException {
        return fileService.download(response, blockName);
    }

    @Autowired
    TrackerClient trackerClient;
    @Autowired
    FastFileStorageClient storageClient;
    @Autowired
    BlockMapper blockMapper;
    @RequestMapping("/deleteAll")
    public Result deleteAllFile() {
        LambdaQueryWrapper<Block> queryWrapper = new LambdaQueryWrapper<>();
        List<Block> blocks = blockMapper.selectList(queryWrapper);
        System.out.println(trackerClient.listGroups().get(0));
        for (Block block : blocks) {
            storageClient.deleteFile(block.getDfsPath());
        }
//        storageClient.queryFileInfo("group1", "");
        System.out.println(trackerClient.listGroups().get(0));
        return Result.success();
    }
    @RequestMapping("/get")
    public Result getFile() {
        FileInfo group1 = storageClient.queryFileInfo("group1", "M00/00/03/rBgP92AgDPOANnQeAnW7xmFxDMU416.pdf");
        return Result.success(ResultCode.SUCCESS, group1.toString());
    }

}

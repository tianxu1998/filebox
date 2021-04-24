package top.xufilebox.file.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.xufilebox.common.dto.FileInfoDTO;
import top.xufilebox.common.dto.GenerateUrlDTO;
import top.xufilebox.common.dto.ShareInfoDTO;
import top.xufilebox.common.dto.TransferSaveDTO;
import top.xufilebox.common.mybatis.mapper.ShareMapper;
import top.xufilebox.common.result.Result;
import top.xufilebox.common.result.ResultCode;
import top.xufilebox.file.service.impl.FileServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/file/share")
public class FileShareController {

    @Autowired
    FileServiceImpl fileService;

    /**
     * 用户分享文件给他人生成分享链接
     * @param userId
     * @param generateUrlDTO
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping("/generateUrl")
    public Result<String> generateUrl(@RequestBody GenerateUrlDTO generateUrlDTO,
                                      @RequestHeader("userId") String userId,
                                      @RequestHeader("userName") String userName) throws JsonProcessingException {
        generateUrlDTO.setFrom(userName);
        return fileService.generateUrl(userId, generateUrlDTO);
    }

    /**
     * 根据分享的链接获得文件列表
     * @param userId
     * @param url
     * @return
     */
    @GetMapping("/decodeUrl/{url}")
    public Result<List<FileInfoDTO>> decodeUrl(@RequestHeader("userId") String userId,
                                               @PathVariable("url") String url) {

        return fileService.decodeUrl(url);
    }

    /**
     * 其他用户点击分享链接，转存到自己的空间
     * @param userId
     * @param request
     * @return
     */
    @PostMapping("/transferSave")
    public Result<String> transferSave(@RequestHeader("userId") String userId,
                                       @RequestBody TransferSaveDTO request) {
        return fileService.transferSave(userId, request);
    }

    /**
     * 获取分享的所有url信息
     * @param userId
     * @return
     */
    @PostMapping("/getAllShareInfo")
    public Result<ShareInfoDTO> getAllShareInfo(@RequestHeader("userId") String userId) {
        return fileService.getShareInfo(userId);
    }

    /**
     * 禁用分享链接
     * @param userName
     * @param shareUrl
     * @return
     */
    @GetMapping("/disableShareUrl/{shareUrl}")
    public Result<String> disableShareUrl(@RequestHeader("userName") String userName,
                                          @PathVariable("shareUrl") String shareUrl) {
        // 检查是否 文件拥有者
        if (!fileService.shareUrlOwnerCheck(userName, shareUrl)) {
            return Result.failed(ResultCode.NOT_FILE_OWNER);
        }
        // 检查分享是否过期
        if (fileService.shareUrlExpiredCheck(shareUrl)) {
            return Result.failed(ResultCode.SHARE_EXPIRED);
        }
        return fileService.disableShareUrl(shareUrl);
    }

    /**
     * 启用分享链接
     * @param userName
     * @param shareUrl
     * @return
     */
    @GetMapping("/enableShareUrl/{shareUrl}")
    public Result<String> enableShareUrl(@RequestHeader("userName") String userName,
                                          @PathVariable("shareUrl") String shareUrl) {
        // 检查是否 文件拥有者
        if (!fileService.shareUrlOwnerCheck(userName, shareUrl)) {
            return Result.failed(ResultCode.NOT_FILE_OWNER);
        }
        // 检查分享是否过期
        if (fileService.shareUrlExpiredCheck(shareUrl)) {
            return Result.failed(ResultCode.SHARE_EXPIRED);
        }
        return fileService.enableShareUrl(shareUrl);
    }

}

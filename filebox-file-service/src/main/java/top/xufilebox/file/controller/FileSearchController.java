package top.xufilebox.file.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.stylesheets.LinkStyle;
import top.xufilebox.common.dto.FileInfoDTO;
import top.xufilebox.common.result.Result;
import top.xufilebox.file.service.impl.FileSearchServiceImpl;

import java.util.List;

/**
 * @description:
 * @author: alextian
 * @create: 2021-02-05 21:02
 **/
@RestController
@RequestMapping("/file/search")
public class FileSearchController {
    @Autowired
    FileSearchServiceImpl fileSearchService;

    @GetMapping("/name/{fileName}")
    public Result<List<FileInfoDTO>> searchByFileName(@PathVariable("fileName") String fileName,
                                                      @RequestHeader("userId") String userId) {
        return fileSearchService.searchByFileName(fileName, userId);
    }
}

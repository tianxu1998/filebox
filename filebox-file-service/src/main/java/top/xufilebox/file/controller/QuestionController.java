package top.xufilebox.file.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.xufilebox.common.dto.QuestionDTO;
import top.xufilebox.common.dto.UploadFileDTO;
import top.xufilebox.common.mybatis.entity.File;
import top.xufilebox.common.result.Result;
import top.xufilebox.file.service.impl.QuestionService;

import java.util.List;

@RestController
@RequestMapping("/file/question")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    /**
     * 列出所有问题
     * @return
     */
    @RequestMapping("/listQuestion")
    public Result<List<QuestionDTO>> listQuestion() {
        return questionService.listQuestion();
    }

    /**
     * 搜索问题
     * @param keyword
     * @return
     */
    @RequestMapping("/searchQuestion/{keyword}")
    public Result<List<QuestionDTO>> searchQuestion(@PathVariable("keyword") String keyword) {
        return questionService.searchQuestion(keyword);
    }

    /**
     * 人工解决问题
     * @param question
     * @return
     */
    @RequestMapping("/sendMailQuestion/{question}")
    public Result sendMailQuestion(@PathVariable("question") String question) {
        return questionService.sendMailQuestion(question);
    }
}

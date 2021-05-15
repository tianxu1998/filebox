package top.xufilebox.file.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.xufilebox.common.dto.QuestionDTO;
import top.xufilebox.common.dto.SendMailDTO;
import top.xufilebox.common.result.Result;
import top.xufilebox.common.result.ResultCode;
import top.xufilebox.common.util.LogicalValue;
import top.xufilebox.file.call.MailServiceFeign;

import java.util.*;

/**
 * @description:
 * @author: alextian
 * @create: 2021-02-05 20:55
 **/
@Service
@Slf4j
public class QuestionService {
    @Value("#{${filebox.question.questionsMap}}")
    private Map<String, String> questionsMap;
    @Value("${filebox.question.questionMail}")
    private String questionMail;
    @Value("${filebox.question.questionTitle}")
    private String questionTitle;

    @Autowired
    private MailServiceFeign mailClient;

    public Result<List<QuestionDTO>> listQuestion() {
        List<QuestionDTO> res = new ArrayList<>();
        questionsMap.forEach((key, value) -> {
            res.add(QuestionDTO.builder()
                        .question(key)
                        .result(value)
                        .code(LogicalValue.TRUE)
                        .build());
        });
        return Result.success(ResultCode.SUCCESS, res);
    }

    public Result<List<QuestionDTO>> searchQuestion(String keyword) {
        List<QuestionDTO> res = new ArrayList<>();
        Set<String> keySet = questionsMap.keySet();
        for (String question : keySet) {
            if (question.contains(keyword)) {
                res.add(QuestionDTO.builder()
                        .question(question)
                        .result(questionsMap.get(question))
                        .code(LogicalValue.TRUE)
                        .build());
            }
        }
        return Result.success(ResultCode.SUCCESS, res);
    }

    public Result sendMailQuestion(String question) {
        return mailClient.sendTextMail(SendMailDTO.builder()
                        .to(questionMail)
                        .template(question)
                        .title(questionTitle)
                        .keyWords(Collections.emptyList())
                        .build());
    }
}

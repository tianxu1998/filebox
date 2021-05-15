package top.xufilebox.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionDTO {
    // 问题
    private String question;
    // 答案
    private String result;
    // 目前有无统计此问题
    private Integer code;
}

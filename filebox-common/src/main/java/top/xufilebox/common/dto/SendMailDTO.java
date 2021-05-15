package top.xufilebox.common.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SendMailDTO {
    String to;
    String title;
    String template;
    List<String> keyWords;
}

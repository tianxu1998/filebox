package top.xufilebox.common.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @description: 生成分享链接的dto
 * @author: alextian
 * @create: 2021-02-25 14:34
 **/
public class GenerateUrlDTO implements Serializable {
    public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private Integer term; // 有效期, 以天为单位
    private List<Integer> fileIds; // 分享的文件id
    private String from; // 分享者用户名
    private String shareTime = LocalDateTime.now().format(DTF); // 分享时间

    public String getShareTime() {
        return shareTime;
    }

    public void setShareTime(String shareTime) {
        this.shareTime = shareTime;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public List<Integer> getFileIds() {
        return fileIds;
    }

    public void setFileIds(List<Integer> fileIds) {
        this.fileIds = fileIds;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}





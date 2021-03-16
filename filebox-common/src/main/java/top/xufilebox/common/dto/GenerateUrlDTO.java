package top.xufilebox.common.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 生成分享链接的dto
 * @author: alextian
 * @create: 2021-02-25 14:34
 **/
public class GenerateUrlDTO implements Serializable {
    private Integer term; // 有效期, 以天为单位
    private List<Integer> fileIds; // 分享的文件id
    private String from; // 分享者用户名

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

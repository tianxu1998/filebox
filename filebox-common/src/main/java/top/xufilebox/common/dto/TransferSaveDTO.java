package top.xufilebox.common.dto;

/**
 *  请求文件转存到"我的"文件DTO
 */
public class TransferSaveDTO {
    private String url;
    private Integer toDirId;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getToDirId() {
        return toDirId;
    }

    public void setToDirId(Integer toDirId) {
        this.toDirId = toDirId;
    }
}

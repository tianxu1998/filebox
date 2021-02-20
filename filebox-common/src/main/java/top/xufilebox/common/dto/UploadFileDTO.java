package top.xufilebox.common.dto;

/**
 * @description:
 * @author: alextian
 * @create: 2021-02-05 21:04
 **/
public class UploadFileDTO {
    private String fileName;
    private String from;
    private Integer parentDirId;
    private Long size;
    private String hash;
    private Integer blockNumber;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Integer getParentDirId() {
        return parentDirId;
    }

    public void setParentDirId(Integer parentDirId) {
        this.parentDirId = parentDirId;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Integer getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Integer blockNumber) {
        this.blockNumber = blockNumber;
    }
}

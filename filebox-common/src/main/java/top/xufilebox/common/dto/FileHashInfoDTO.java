package top.xufilebox.common.dto;

/**
 * @description:
 * @author: alextian
 * @create: 2021-02-09 17:09
 **/
public class FileHashInfoDTO {
    private Integer fileId; // 文件Id
    private String fileHash; // 文件hash
    private Integer blockNumber; // 分块数量
    private Long fileSize; // 文件大小
    private String fileName; // 文件名称

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }


    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public Integer getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Integer blockNumber) {
        this.blockNumber = blockNumber;
    }
}

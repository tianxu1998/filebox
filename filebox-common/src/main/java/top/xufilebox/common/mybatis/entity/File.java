package top.xufilebox.common.mybatis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author tianxu
 * @since 2021-02-07
 */
@TableName("f_file")
@ApiModel(value="File对象", description="")
public class File implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "文件id")
    @TableId(value = "file_id", type = IdType.AUTO)
    private Integer fileId;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "文件来源:例如：来自张三的分享，本地上传等")
    @TableField("`from`")
    private String from;

    @ApiModelProperty(value = "所属文件夹的id")
    private Integer parentDirId;

    @ApiModelProperty(value = "文件大小")
    private Long size;

    @ApiModelProperty(value = "文件hash值")
    @TableField("`hash`")
    private String hash;

    @ApiModelProperty(value = "分块数量")
    private Integer blockNumber;

    @ApiModelProperty(value = "是否逻辑删除：1表示是  0表示否")
    @TableField("`delete`")
    private Integer delete;

    @ApiModelProperty(value = "文件状态 ：0、未上传完成  1、上传完成 ")
    @TableField("`status`")
    private Integer status;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "新建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "创建者user_id")
    private Integer createBy;

    @ApiModelProperty(value = "修改者user_id")
    private Integer updateBy;

    @ApiModelProperty(value = "拥有者user_id")
    @TableField("`owner`")
    private Integer owner;

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
    }

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

    public Integer getDelete() {
        return delete;
    }

    public void setDelete(Integer delete) {
        this.delete = delete;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public Integer getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    @Override
    public String toString() {
        return "File{" +
        "fileId=" + fileId +
        ", fileName=" + fileName +
        ", from=" + from +
        ", parentDirId=" + parentDirId +
        ", size=" + size +
        ", hash=" + hash +
        ", blockNumber=" + blockNumber +
        ", delete=" + delete +
        ", status=" + status +
        ", updateTime=" + updateTime +
        ", createTime=" + createTime +
        ", createBy=" + createBy +
        ", updateBy=" + updateBy +
        "}";
    }
}

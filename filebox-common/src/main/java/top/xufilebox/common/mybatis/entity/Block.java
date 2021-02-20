package top.xufilebox.common.mybatis.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("f_block")
@ApiModel(value="Block对象", description="")
public class Block implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "文件id")
    private Integer blockId;

    @ApiModelProperty(value = "文件大小")
    private Long blockSize;

    @ApiModelProperty(value = "文件hash值")
    @TableField("`hash`")
    private String hash;

    @ApiModelProperty(value = "分块顺序")
    private Integer blockNumber;

    @ApiModelProperty(value = "属于哪个文件")
    private String fileHash;

    @ApiModelProperty(value = "属于哪个组")
    @TableField("`group`")
    private String group;

    @ApiModelProperty(value = "在文件集群的路径")
    private String dfsPath;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "新建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "创建者user_id")
    private Integer createBy;

    @ApiModelProperty(value = "修改者user_id")
    private Integer updateBy;


    public Integer getBlockId() {
        return blockId;
    }

    public void setBlockId(Integer blockId) {
        this.blockId = blockId;
    }

    public Long getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(Long blockSize) {
        this.blockSize = blockSize;
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

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDfsPath() {
        return dfsPath;
    }

    public void setDfsPath(String dfsPath) {
        this.dfsPath = dfsPath;
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
        return "Block{" +
        "blockId=" + blockId +
        ", blockSize=" + blockSize +
        ", hash=" + hash +
        ", blockNumber=" + blockNumber +
        ", fileHash=" + fileHash +
        ", group=" + group +
        ", dfsPath=" + dfsPath +
        ", updateTime=" + updateTime +
        ", createTime=" + createTime +
        ", createBy=" + createBy +
        ", updateBy=" + updateBy +
        "}";
    }
}

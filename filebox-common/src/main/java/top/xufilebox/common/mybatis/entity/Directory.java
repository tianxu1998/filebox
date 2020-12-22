package top.xufilebox.common.mybatis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * @since 2020-12-18
 */
@TableName("f_directory")
@ApiModel(value="Directory对象", description="")
public class Directory implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "文件夹id")
    @TableId(value = "dir_id", type = IdType.AUTO)
    private Integer dirId;

    @ApiModelProperty(value = "文件夹名称")
    private String dirName;

    @ApiModelProperty(value = "父文件夹id （父文件夹id如何为-1则表示此文件夹为用户根目录）")
    private Integer parentDirId;

    @ApiModelProperty(value = "拥有者user_id")
    private Integer owner;

    @ApiModelProperty(value = " 文件夹路径")
    private String path;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "新建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "创建者user_id")
    private Integer createBy;

    @ApiModelProperty(value = "修改者user_id")
    private Integer updateBy;


    public Integer getDirId() {
        return dirId;
    }

    public void setDirId(Integer dirId) {
        this.dirId = dirId;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public Integer getParentDirId() {
        return parentDirId;
    }

    public void setParentDirId(Integer parentDirId) {
        this.parentDirId = parentDirId;
    }

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
        return "Directory{" +
        "dirId=" + dirId +
        ", dirName=" + dirName +
        ", parentDirId=" + parentDirId +
        ", owner=" + owner +
        ", path=" + path +
        ", updateTime=" + updateTime +
        ", createTime=" + createTime +
        ", createBy=" + createBy +
        ", updateBy=" + updateBy +
        "}";
    }
}

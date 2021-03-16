package top.xufilebox.common.dto;

/**
 * @description:
 * @author: alextian
 * @create: 2021-02-24 00:46
 **/
public class CreateNewDirDTO {
    private String curPath;
    private Integer curDirId;

    public String getCurPath() {
        return curPath;
    }

    public void setCurPath(String curPath) {
        this.curPath = curPath;
    }

    public Integer getCurDirId() {
        return curDirId;
    }

    public void setCurDirId(Integer curDirId) {
        this.curDirId = curDirId;
    }
}

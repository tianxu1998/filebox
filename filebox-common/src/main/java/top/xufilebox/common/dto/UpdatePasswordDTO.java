package top.xufilebox.common.dto;

/**
 * @description:
 * @author: alextian
 * @create: 2021-01-04 15:46
 **/
public class UpdatePasswordDTO {
    private String oldPassword;
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}

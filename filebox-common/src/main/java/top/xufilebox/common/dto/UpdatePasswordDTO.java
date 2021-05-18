package top.xufilebox.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: alextian
 * @create: 2021-01-04 15:46
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordDTO {
    // 旧密码
    private String oldPassword;
    // 新密码
    private String newPassword;
    // 邮箱验证码
    private String verifyCode;
}

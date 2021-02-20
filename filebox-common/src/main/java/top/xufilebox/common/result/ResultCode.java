package top.xufilebox.common.result;

/**
 * @description:
 * @author: alextian
 * @create: 2020-12-15 18:35
 **/
public enum ResultCode {
    SUCCESS("0000", "请求成功"),
    FAILED("0001", "未知错误"),

    USER_PASSWORD_ERROR("U0001", "用户密码错误"),
    USER_NOT_FOUND_ERROR("U0002", "不存在此用户"),
    USER_DISABLED_ERROR("U0003", "用户已经被禁用"),
    USER_VERIFYCODE_ERROR("U0004", "验证码过期或验证码错误"),
    USER_EMPTY_TOKEN("U0005", "用户token为空"),
    USER_TOKEN_ERROR("U0006", "token过期或token错误"),
    USER_TOKEN_EXPIRED("U0007", "token过期"),
    USER_OLD_PASSWORD_ERROR("U0008", "旧密码错误"),
    NOT_MULTIPARTCONTENT("F0009", "没有选择上传文件"),
    FILE_UPLOAD_COMPLETED("F0010", "已经上传了文件"),
    FILE_UPLOAD_NOT_COMPLETED("F0011", "上传文件未完成"),
    NOT_FILE_OWNER("F0012", "不是文件的拥有者"),
    FILE_DONOT_EXIST("F0013", "文件由于未知错误不存在");

    private String code;
    private String msg;

    ResultCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

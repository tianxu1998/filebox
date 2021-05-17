package top.xufilebox.common.util;

/**
 * @description:
 * @author: alextian
 * @create: 2020-12-20 20:23
 **/
public class Constant {
    // 默认的存储空间 1GB
    public static final Long DEFAULT_CAPACITY = 1073741824L;
    // 默认的根目录id
    public static final int DEFAULT_PARENT_DIR_ID = -1;
    // 上传完成的状态
    public static final int UPLOAD_COMPLETED = 1;
    // 上传未完成的状态
    public static final int UPLOAD_UNCOMPLETED = 0;
    // 新建文件夹名称
    public static final String DEFAULT_DIR_NAME = "新建文件夹";
    // 邮件验证码的失效时间 十分钟
    public static final Long MAIL_CODE_EXPIRATION = 1000L * 60 * 10;
}

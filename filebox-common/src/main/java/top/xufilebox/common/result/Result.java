package top.xufilebox.common.result;

import lombok.Getter;
import lombok.Setter;

/**
 * @description:
 * @author: alextian
 * @create: 2020-12-15 18:31
 **/
@Setter
@Getter
public class Result<T> {
    private String msg;
    private String code;
    private T data;

    public static <T> Result<T> failed() {
        return result(ResultCode.FAILED);
    }

    public static <T> Result<T> failed(ResultCode rc) {
        return success(rc);
    }

    public static <T> Result<T> failed(String code, String msg) {
        return result(code, msg);
    }

    public static <T> Result<T> failed(String code, String msg, T data) {
        return result(code, msg, data);
    }

    public static <T> Result<T> failed(ResultCode rc, T data) {
        return result(rc, data);
    }

    public static <T> Result<T> success() {
        return result(ResultCode.SUCCESS);
    }

    public static <T> Result<T> success(ResultCode rc) {
        return result(rc);
    }

    public static <T> Result<T> success(String code, String msg) {
        return result(code, msg);
    }

    public static <T> Result<T> success(String code, String msg, T data) {
        return result(code, msg, data);
    }

    public static <T> Result<T> success(ResultCode rc, T data) {
        return result(rc, data);
    }

    private static <T> Result<T> result(ResultCode rc) {
        return result(rc, null);
    }

    private static <T> Result<T> result(String code, String msg) {
        return result(code, msg, null);
    }

    private static <T> Result<T> result(ResultCode rc, T data) {
        return result(rc.getCode(), rc.getMsg(), data);
    }

    private static <T> Result<T> result(String code, String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
}

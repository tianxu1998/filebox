package top.xufilebox.mybatis.utils;

import org.apache.ibatis.datasource.DataSourceException;

/**
 * @description:
 * @author: alextian
 * @create: 2020-12-07 09:05
 **/
public class DBTypeHolder {
    public static final String WRITE = "write";
    public static final String READ = "read";
    public static final ThreadLocal<String> holder = new ThreadLocal<>();

    public static void setDBType(String type) {
        if (type == null) throw new DataSourceException("DBType can not be null!");
        if (!type.equals(WRITE) && !type.equals(READ)) throw new DataSourceException("DBType must be 'write' or 'read'");
        holder.set(type);
    }

    public static String getDBType() {
        return holder.get() == null ? WRITE : holder.get();
    }

    public static void clearDBType() {
        holder.remove();
    }

}

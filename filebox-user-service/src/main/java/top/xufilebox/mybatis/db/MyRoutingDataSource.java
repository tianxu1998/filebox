package top.xufilebox.mybatis.db;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import top.xufilebox.mybatis.utils.DBTypeHolder;
import top.xufilebox.mybatis.utils.RandomUtil;

/**
 * @description:
 * @author: alextian
 * @create: 2020-12-07 09:19
 **/
public class MyRoutingDataSource extends AbstractRoutingDataSource {
    // num 表示读库的数量
    @Value("${mysql.num}")
    private String num;

    @Override
    protected Object determineCurrentLookupKey() {
        String type = DBTypeHolder.getDBType();
        if (type.equals(DBTypeHolder.WRITE)) {
            return type;
        }
        int randNum = RandomUtil.random(1, Integer.valueOf(num));
        return DBTypeHolder.READ + randNum;
    }
}

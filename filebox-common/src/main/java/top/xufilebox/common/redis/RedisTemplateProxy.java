package top.xufilebox.common.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: alextian
 * @create: 2020-12-19 21:12
 **/
@Component
public class RedisTemplateProxy {

    @Autowired
    private StringRedisTemplate redisMasterTemplate;

    @Autowired
    private List<StringRedisTemplate> redisSlaveTemplate;

    /**
     * 设置kv对
     * @param key
     * @param value
     */
    public void setValue(String key,String value) {
        redisMasterTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置redis ex
     * @param key
     * @param value
     * @param ms
     */
    public void setEX(String key, String value, long ms) {
        redisMasterTemplate.opsForValue().set(key, value, ms, TimeUnit.MILLISECONDS);
    }

    /**
     * 根据key拿到value
     * @param key
     * @return
     */
    public String getValue(String key) {
        return getRedisSlaveTemplate().opsForValue().get(key);
    }

    /**
     * 根据key判断是否过期
     * @param key
     * @return
     */
    public boolean isExpired(String key) {
        Long expire = getRedisSlaveTemplate().getExpire(key);
        return expire <= 0;
    }

    /**
     * 获取过期时间， 并转换成秒
     * @param key
     * @return
     */
    public long getExpired(String key) {
        Long expire = getRedisSlaveTemplate().getExpire(key, TimeUnit.MILLISECONDS);
        return expire;
    }

    /**
     * 根据key删除
     * @param key
     */
    public void delete(String key) {
        redisMasterTemplate.delete(key);
    }

    private StringRedisTemplate getRedisSlaveTemplate() {
        return redisSlaveTemplate.get(1 + (int) (Math.random() + 0.5));
    }



}
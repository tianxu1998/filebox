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

    public void setValue(String key,String value) {
        redisMasterTemplate.opsForValue().set(key, value);
    }

    public void setEX(String key, String value, long ms) {
        redisMasterTemplate.opsForValue().set(key, value, ms, TimeUnit.MILLISECONDS);
    }

    public String getValue(String key) {
        return getRedisSlaveTemplate().opsForValue().get(key);
    }

    public boolean isExpired(String key) {
        Long expire = getRedisSlaveTemplate().getExpire(key);
        return expire <= 0;
    }

    public long getExpired(String key) {
        Long expire = getRedisSlaveTemplate().getExpire(key, TimeUnit.MILLISECONDS);
        return expire;
    }

    public void delete(String key) {
        redisMasterTemplate.delete(key);
    }

    private StringRedisTemplate getRedisSlaveTemplate() {
        return redisSlaveTemplate.get(1 + (int) (Math.random() + 0.5));
    }



}
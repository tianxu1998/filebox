package top.xufilebox.common.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

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

    public void setValue(String key, String value, long time) {
        redisMasterTemplate.opsForValue().set(key, value);
    }

    public String getValue(String key) {
        return redisSlaveTemplate.get(1 + (int)(Math.random() + 0.5)).opsForValue().get(key);
    }

}
package top.xufilebox.common.redis;

import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: alextian
 * @create: 2020-12-19 20:50
 **/
@Configuration
public class RedisConfiguration {

    @Value("${spring.redis.pool.max-idle}")
    private int maxIdle;
    @Value("${spring.redis.pool.max-active}")
    private int maxTotal;
    @Value("${spring.redis.database}")
    private int index;
    @Value("${spring.redis.pool.max-wait}")
    private long maxWaitMillis;


    @Bean(name = "redisMasterTemplate")
    public StringRedisTemplate redisMasterTemplate(@Value("${spring.redis-master.host}") String hostName,
                                                   @Value("${spring.redis-master.port}") int port, @Value("${spring.redis-master.password}") String password, @Value("${spring.redis-master.testOnBorrow}") boolean testOnBorrow) {
        StringRedisTemplate temple = new StringRedisTemplate();
        temple.setConnectionFactory(
                connectionFactory(hostName, port, password, maxIdle, maxTotal, index, maxWaitMillis, testOnBorrow));

        return temple;
    }

    @Bean(name = "redisSlave1Template")
    public StringRedisTemplate redisSlave1Template(@Value("${spring.redis-slave1.host}") String hostName,
                                                   @Value("${spring.redis-slave1.port}") int port, @Value("${spring.redis-slave1.password}") String password, @Value("${spring.redis-slave1.testOnBorrow}") boolean testOnBorrow) {
        StringRedisTemplate temple = new StringRedisTemplate();
        temple.setConnectionFactory(
                connectionFactory(hostName, port, password, maxIdle, maxTotal, index, maxWaitMillis, testOnBorrow));

        return temple;
    }

    @Bean(name = "redisSlave2Template")
    public StringRedisTemplate redisSlave2Template(@Value("${spring.redis-slave2.host}") String hostName,
                                                   @Value("${spring.redis-slave2.port}") int port, @Value("${spring.redis-slave2.password}") String password, @Value("${spring.redis-slave2.testOnBorrow}") boolean testOnBorrow) {
        StringRedisTemplate temple = new StringRedisTemplate();
        temple.setConnectionFactory(
                connectionFactory(hostName, port, password, maxIdle, maxTotal, index, maxWaitMillis, testOnBorrow));
        return temple;
    }

    public RedisConnectionFactory connectionFactory(String hostName, int port, String password, int maxIdle,
                                                    int maxTotal, int index, long maxWaitMillis, boolean testOnBorrow) {
        JedisConnectionFactory jedis = new JedisConnectionFactory();
        jedis.setHostName(hostName);
        jedis.setPort(port);
        if (StringUtils.isNotEmpty(password)) {
            jedis.setPassword(password);
        }
        if (index != 0) {
            jedis.setDatabase(index);
        }
        jedis.setPoolConfig(poolCofig(maxIdle, maxTotal, maxWaitMillis, testOnBorrow));
        // 初始化连接pool
        jedis.afterPropertiesSet();
        RedisConnectionFactory factory = jedis;

        return factory;
    }

    public JedisPoolConfig poolCofig(int maxIdle, int maxTotal, long maxWaitMillis, boolean testOnBorrow) {
        JedisPoolConfig poolCofig = new JedisPoolConfig();
        poolCofig.setMaxIdle(maxIdle);
        poolCofig.setMaxTotal(maxTotal);
        poolCofig.setMaxWaitMillis(maxWaitMillis);
        poolCofig.setTestOnBorrow(testOnBorrow);
        return poolCofig;
    }
}
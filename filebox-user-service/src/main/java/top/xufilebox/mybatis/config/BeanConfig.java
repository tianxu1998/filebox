package top.xufilebox.mybatis.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import top.xufilebox.mybatis.db.MyRoutingDataSource;
import top.xufilebox.mybatis.utils.DBTypeHolder;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: alextian
 * @create: 2020-12-07 09:12
 **/
@Configuration
public class BeanConfig {
    @Value("${mysql.type-aliases-package}")
    private String typeAliasesPackage;
    @Value("${mysql.mapper-locations}")
    String mapperLocation;
    @Value("${mysql.config-location}")
    String configLocation;

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "mysql.datasource.write")
    public DataSource writeDataSource() {
        return new DruidDataSource();
    }

    @Bean
    @ConfigurationProperties(prefix = "mysql.datasource.read1")
    public DataSource read1DataSource() {
        return new DruidDataSource();
    }

    @Bean
    @ConfigurationProperties(prefix = "mysql.datasource.read2")
    public DataSource read2DataSource() {
        return new DruidDataSource();
    }

    @Bean
    public AbstractRoutingDataSource routingDataSource() {
        AbstractRoutingDataSource proxy = new MyRoutingDataSource();
        Map<Object, Object> target = new HashMap<>();
        target.put(DBTypeHolder.WRITE, writeDataSource());
        target.put(DBTypeHolder.READ + 1, read1DataSource());
        target.put(DBTypeHolder.READ + 2, read2DataSource());
        proxy.setDefaultTargetDataSource(writeDataSource());
        proxy.setTargetDataSources(target);
        return proxy;
    }


    /**
     * 多数据源需要自己设置sqlSessionFactory
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(routingDataSource());
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        // 实体类对应的位置
        bean.setTypeAliasesPackage(typeAliasesPackage);
        // mybatis的XML的配置
        bean.setMapperLocations(resolver.getResources(mapperLocation));
        bean.setConfigLocation(resolver.getResource(configLocation));
        return bean.getObject();
    }

    /**
     * 设置事务，事务需要知道当前使用的是哪个数据源才能进行事务处理
     */
    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager() {
        return new DataSourceTransactionManager(routingDataSource());
    }

}

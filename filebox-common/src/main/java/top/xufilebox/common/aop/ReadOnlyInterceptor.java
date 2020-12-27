package top.xufilebox.common.aop;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import top.xufilebox.common.annotation.ReadOnly;

/**
 * @description:
 * @author: alextian
 * @create: 2020-12-10 16:44
 **/
@Aspect
@Component
public class ReadOnlyInterceptor implements Ordered {
    private static final Logger log= LoggerFactory.getLogger(ReadOnlyInterceptor.class);

    @Around("@annotation(readOnly)")
    public Object setRead(ProceedingJoinPoint joinPoint, ReadOnly readOnly) throws Throwable{
        DynamicDataSourceContextHolder.clear();
        try{
            int random = (int) (Math.random() + 0.5) + 1;
            DynamicDataSourceContextHolder.push("slave_" + random);
            return joinPoint.proceed();
        }finally {
            //清楚DbType一方面为了避免内存泄漏，更重要的是避免对后续在本线程上执行的操作产生影响
            DynamicDataSourceContextHolder.push("master");
            log.info("清除threadLocal");
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

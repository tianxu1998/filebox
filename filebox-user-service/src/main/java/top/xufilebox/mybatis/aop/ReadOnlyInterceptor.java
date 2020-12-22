package top.xufilebox.mybatis.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import top.xufilebox.mybatis.annotation.ReadOnly;
import top.xufilebox.mybatis.utils.DBTypeHolder;

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
        try{
            DBTypeHolder.setDBType(DBTypeHolder.READ);
            return joinPoint.proceed();
        }finally {
            //清楚DbType一方面为了避免内存泄漏，更重要的是避免对后续在本线程上执行的操作产生影响
            DBTypeHolder.clearDBType();
            log.info("清除threadLocal");
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

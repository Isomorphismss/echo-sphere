package org.isomorphism.api.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Aspect
public class ServiceLogAspect {

    @Around("execution(* org.isomorphism.service.impl..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {

        // 需要统计每一个service实现的执行时间，如果执行时间太久，则进行error级别的日志输出

        long begin = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();
        String pointName = joinPoint.getTarget().getClass().getName()
                + "."
                + joinPoint.getSignature().getName();

        long end = System.currentTimeMillis();
        long takeTimes = end - begin;

        if (takeTimes > 3000) {
            log.error("执行位置{}，执行时间太长了，耗费了{}毫秒", pointName, takeTimes);
        } else if (takeTimes > 2000) {
            log.warn("执行位置{}，执行时间稍微有点长，耗费了{}毫秒", pointName, takeTimes);
        } else {
            log.info("执行位置{}，执行时间正常，耗费了{}毫秒", pointName, takeTimes);
        }

        return proceed;
    }

}

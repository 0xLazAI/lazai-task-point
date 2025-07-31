package com.lazai.annotation.aspect;

import com.lazai.exception.DomainException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class InternalServerCallAspect {

    @Pointcut("@annotation(com.lazai.annotation.InternalServerCall)")
    public void myPointCut(){}

    @Around("myPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable{
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String internal = request.getHeader("x-server-call");
        if (!"true".equalsIgnoreCase(internal)) {
            throw new DomainException("can not access!", 403);
        }
        return point.proceed();
    }

}

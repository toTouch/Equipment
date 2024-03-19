package com.xiliulou.afterserver.plugin;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
@Aspect
public class LogAspect {
    
    // 配置织入点
    // 将所有标有@Log注解的方法作为切点
    private Logger logger = LoggerFactory.getLogger(LogAspect.class);
    
    @Pointcut("@annotation(com.xiliulou.afterserver.plugin.MyLog)")
    public void myPointCut() {
        // 签名，可以理解成这个切入点的一个名称
    }
    
    // 使用环绕通知实现日志记录
    @Around(value = "myPointCut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (attributes != null) {
            request = attributes.getRequest();
        }
        // 获取url,请求方法，类名以及方法名，参数
        String requestURI = null;
        if (request != null) {
            requestURI = request.getRequestURI();
        }
        String method = null;
        if (request != null) {
            method = request.getMethod();
        }
        logger.info("url={},method={},class_method={},args={}", requestURI, method, pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName(), pjp.getArgs());
        Object proceed = pjp.proceed();
        logger.info("result = {}", proceed);
        return proceed;
    }
    
}

package com.orvillex.bortus.aop;

import com.orvillex.bortus.modules.log.domain.Log;
import com.orvillex.bortus.modules.log.service.LogService;
import com.orvillex.bortus.utils.SecurityUtils;
import com.orvillex.bortus.utils.StringUtils;
import com.orvillex.bortus.utils.ThrowableUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 用于审计日志的AOP方法
 * @author y-z-f
 * @version 0.1
 */
@Component
@Aspect
public class LogAspect {

    private final LogService logService;

    ThreadLocal<Long> currentTime = new ThreadLocal<>();

    public LogAspect(LogService logService) {
        this.logService = logService;
    }

    @Pointcut("@annotation(com.orvillex.bortus.annotation.Log)")
    public void logPointcut() {

    }

    @Around("logPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;
        currentTime.set(System.currentTimeMillis());
        result = joinPoint.proceed();
        Log log = new Log("INFO", System.currentTimeMillis() - currentTime.get());
        currentTime.remove();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        logService.save(getUsername(), StringUtils.getBrowser(request), StringUtils.getIp(request), (ProceedingJoinPoint)joinPoint, log);
        return result;
    }

    /**
     * 异常通知
     */
    @AfterThrowing(pointcut = "logPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        Log log = new Log("ERROR", System.currentTimeMillis() - currentTime.get());
        currentTime.remove();
        log.setExceptionDetail(ThrowableUtil.getStackTrace(e).getBytes());
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        logService.save(getUsername(), StringUtils.getBrowser(request), StringUtils.getIp(request), (ProceedingJoinPoint)joinPoint, log);
    }

    public String getUsername() {
        try {
            return SecurityUtils.getCurrentUsername();
        } catch (Exception e) {
            return "";
        }
    }
}

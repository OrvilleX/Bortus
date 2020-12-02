package com.orvillex.bortus.utils.scheduler;

import com.orvillex.bortus.handler.SpringContextHolder;
import com.orvillex.bortus.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * 调度任务运行对象
 * @author y-z-f
 * @version 0.1
 */
@Slf4j
public class QuartzRunnable implements Callable {
    private final Object target;
    private final Method method;
    private final String params;

    QuartzRunnable(String beanName, String methodName, String params)
            throws NoSuchMethodException, SecurityException {
        this.target = SpringContextHolder.getBean(beanName);
        this.params = params;

        if (StringUtils.isNotBlank(params)) {
            this.method = target.getClass().getDeclaredMethod(methodName, String.class);
        } else {
            this.method = target.getClass().getDeclaredMethod(methodName);
        }
    }

    @Override
    public Object call() throws Exception {
        ReflectionUtils.makeAccessible(method);
        if (StringUtils.isNotBlank(params)) {
            method.invoke(target, params);
        } else {
            method.invoke(target);
        }
        return null;
    }
}

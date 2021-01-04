package com.orvillex.bortus.job.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.orvillex.bortus.job.biz.models.ReturnT;

/**
 * 方法任务处理器
 */
public class MethodJobHandler extends IJobHandler {

    private final Object target;
    private final Method method;
    private Method initMethod;
    private Method destroyMethod;

    public MethodJobHandler(Object target, Method method, Method initMethod, Method destroyMethod) {
        this.target = target;
        this.method = method;

        this.initMethod =initMethod;
        this.destroyMethod =destroyMethod;
    }

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        return (ReturnT<String>) method.invoke(target, new Object[]{param});
    }

    @Override
    public void init() throws InvocationTargetException, IllegalAccessException {
        if(initMethod != null) {
            initMethod.invoke(target);
        }
    }

    @Override
    public void destroy() throws InvocationTargetException, IllegalAccessException {
        if(destroyMethod != null) {
            destroyMethod.invoke(target);
        }
    }

    @Override
    public String toString() {
        return super.toString()+"["+ target.getClass() + "#" + method.getName() +"]";
    }
}

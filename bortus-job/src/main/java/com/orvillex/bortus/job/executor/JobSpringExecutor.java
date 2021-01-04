package com.orvillex.bortus.job.executor;

import java.lang.reflect.Method;
import java.util.Map;

import com.orvillex.bortus.job.biz.models.ReturnT;
import com.orvillex.bortus.job.glue.GlueFactory;
import com.orvillex.bortus.job.handler.MethodJobHandler;
import com.orvillex.bortus.job.handler.annotation.Job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.beans.BeansException;

public class JobSpringExecutor extends JobExecutor
        implements ApplicationContextAware, SmartInitializingSingleton, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(JobSpringExecutor.class);

    @Override
    public void afterSingletonsInstantiated() {

        initJobHandlerMethodRepository(applicationContext);

        GlueFactory.refreshInstance(1);
        try {
            super.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // destroy
    @Override
    public void destroy() {
        super.destroy();
    }

    private void initJobHandlerMethodRepository(ApplicationContext applicationContext) {
        if (applicationContext == null) {
            return;
        }
        // init job handler from method
        String[] beanDefinitionNames = applicationContext.getBeanNamesForType(Object.class, false, true);
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);

            Map<Method, Job> annotatedMethods = null; 
            try {
                annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(),
                        new MethodIntrospector.MetadataLookup<Job>() {
                            @Override
                            public Job inspect(Method method) {
                                return AnnotatedElementUtils.findMergedAnnotation(method, Job.class);
                            }
                        });
            } catch (Throwable ex) {
                logger.error("job method-jobhandler resolve error for bean[" + beanDefinitionName + "].", ex);
            }
            if (annotatedMethods==null || annotatedMethods.isEmpty()) {
                continue;
            }

            for (Map.Entry<Method, Job> methodXxlJobEntry : annotatedMethods.entrySet()) {
                Method method = methodXxlJobEntry.getKey();
                Job xxlJob = methodXxlJobEntry.getValue();
                if (xxlJob == null) {
                    continue;
                }

                String name = xxlJob.value();
                if (name.trim().length() == 0) {
                    throw new RuntimeException("job method-jobhandler name invalid, for[" + bean.getClass() + "#" + method.getName() + "] .");
                }
                if (loadJobHandler(name) != null) {
                    throw new RuntimeException("job jobhandler[" + name + "] naming conflicts.");
                }

                if (!(method.getParameterTypes().length == 1 && method.getParameterTypes()[0].isAssignableFrom(String.class))) {
                    throw new RuntimeException("job method-jobhandler param-classtype invalid, for[" + bean.getClass() + "#" + method.getName() + "] , " +
                            "The correct method format like \" public ReturnT<String> execute(String param) \" .");
                }
                if (!method.getReturnType().isAssignableFrom(ReturnT.class)) {
                    throw new RuntimeException("xxl-job method-jobhandler return-classtype invalid, for[" + bean.getClass() + "#" + method.getName() + "] , " +
                            "The correct method format like \" public ReturnT<String> execute(String param) \" .");
                }
                method.setAccessible(true);

                Method initMethod = null;
                Method destroyMethod = null;

                if (xxlJob.init().trim().length() > 0) {
                    try {
                        initMethod = bean.getClass().getDeclaredMethod(xxlJob.init());
                        initMethod.setAccessible(true);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException("job method-jobhandler initMethod invalid, for[" + bean.getClass() + "#" + method.getName() + "] .");
                    }
                }
                if (xxlJob.destroy().trim().length() > 0) {
                    try {
                        destroyMethod = bean.getClass().getDeclaredMethod(xxlJob.destroy());
                        destroyMethod.setAccessible(true);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException("job method-jobhandler destroyMethod invalid, for[" + bean.getClass() + "#" + method.getName() + "] .");
                    }
                }
                registJobHandler(name, new MethodJobHandler(bean, method, initMethod, destroyMethod));
            }
        }
    }

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}

package com.orvillex.bortus.manager.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.orvillex.bortus.job.biz.ExecutorBiz;
import com.orvillex.bortus.job.biz.client.ExecutorBizClient;

public class JobSchedulerUtils {
    public static String ACCESSTOKEN;

    private static ConcurrentMap<String, ExecutorBiz> executorBizRepository = new ConcurrentHashMap<String, ExecutorBiz>();

    public static ExecutorBiz getExecutorBiz(String address) throws Exception {
        if (address==null || address.trim().length()==0) {
            return null;
        }

        address = address.trim();
        ExecutorBiz executorBiz = executorBizRepository.get(address);
        if (executorBiz != null) {
            return executorBiz;
        }

        executorBiz = new ExecutorBizClient(address, ACCESSTOKEN);

        executorBizRepository.put(address, executorBiz);
        return executorBiz;
    }
}

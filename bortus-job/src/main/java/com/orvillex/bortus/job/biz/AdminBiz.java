package com.orvillex.bortus.job.biz;

import java.util.List;

import com.orvillex.bortus.job.biz.models.*;

/**
 * 管理接口
 */
public interface AdminBiz {
    
    /**
     * 回调
     */
    public ReturnT<String> callback(List<HandleCallbackParam> callbackParams);

    /**
     * 注册
     */
    public ReturnT<String> registry(RegistryParam registryParam);

    /**
     * 移除注册
     */
    public ReturnT<String> registryRemove(RegistryParam registryParam);
}

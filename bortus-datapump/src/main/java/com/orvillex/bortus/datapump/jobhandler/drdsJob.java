package com.orvillex.bortus.datapump.jobhandler;

import com.orvillex.bortus.datapump.utils.I18nUtil;
import com.orvillex.bortus.job.biz.models.ReturnT;
import com.orvillex.bortus.job.handler.annotation.Job;
import com.orvillex.bortus.job.log.JobLogger;

import org.springframework.stereotype.Component;

@Component
public class drdsJob {
    
    @Job("drdsReaderJobHandler")
    public ReturnT<String> drdsReaderJobHandler(String params) {
        JobLogger.log(I18nUtil.getString("CONFIG_ERROR"));
        JobLogger.log("fe");
        return ReturnT.SUCCESS;
    }
}

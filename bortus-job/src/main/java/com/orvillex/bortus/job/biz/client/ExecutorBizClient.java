package com.orvillex.bortus.job.biz.client;

import com.orvillex.bortus.job.biz.ExecutorBiz;
import com.orvillex.bortus.job.biz.models.*;
import com.orvillex.bortus.job.util.JobRemotingUtil;

public class ExecutorBizClient implements ExecutorBiz {

    public ExecutorBizClient() {
    }

    public ExecutorBizClient(String addressUrl, String accessToken) {
        this.addressUrl = addressUrl;
        this.accessToken = accessToken;

        if (!this.addressUrl.endsWith("/")) {
            this.addressUrl = this.addressUrl + "/";
        }
    }

    private String addressUrl ;
    private String accessToken;
    private int timeout = 3;

    @Override
    public ReturnT<String> beat() {
        return JobRemotingUtil.postBody(addressUrl+"beat", accessToken, timeout, null, String.class);
    }

    @Override
    public ReturnT<String> idleBeat(IdleBeatParam idleBeatParam){
        return JobRemotingUtil.postBody(addressUrl+"idleBeat", accessToken, timeout, idleBeatParam, String.class);
    }

    @Override
    public ReturnT<String> run(TriggerParam triggerParam) {
        return JobRemotingUtil.postBody(addressUrl + "run", accessToken, timeout, triggerParam, String.class);
    }

    @Override
    public ReturnT<String> kill(KillParam killParam) {
        return JobRemotingUtil.postBody(addressUrl + "kill", accessToken, timeout, killParam, String.class);
    }

    @Override
    public ReturnT<LogResult> log(LogParam logParam) {
        return JobRemotingUtil.postBody(addressUrl + "log", accessToken, timeout, logParam, LogResult.class);
    }
}

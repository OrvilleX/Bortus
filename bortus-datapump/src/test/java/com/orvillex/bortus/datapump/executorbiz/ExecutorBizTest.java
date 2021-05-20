package com.orvillex.bortus.datapump.executorbiz;

import com.alibaba.fastjson.JSON;
import com.orvillex.bortus.job.biz.ExecutorBiz;
import com.orvillex.bortus.job.biz.client.ExecutorBizClient;
import com.orvillex.bortus.job.biz.models.IdleBeatParam;
import com.orvillex.bortus.job.biz.models.KillParam;
import com.orvillex.bortus.job.biz.models.LogParam;
import com.orvillex.bortus.job.biz.models.LogResult;
import com.orvillex.bortus.job.biz.models.ReturnT;
import com.orvillex.bortus.job.biz.models.TriggerParam;
import com.orvillex.bortus.job.enums.ExecutorBlockStrategyType;
import com.orvillex.bortus.job.glue.GlueType;

import org.junit.Assert;
import org.junit.Test;

public class ExecutorBizTest {
    private static String addressUrl = "http://127.0.0.1:9999/";
    private static String accessToken = null;

    @Test
    public void beat() throws Exception {
        ExecutorBiz executorBiz = new ExecutorBizClient(addressUrl, accessToken);

        final ReturnT<String> retval = executorBiz.beat();

        Assert.assertNotNull(retval);
        Assert.assertNull(((ReturnT<String>) retval).getContent());
        Assert.assertEquals(200, retval.getCode());
        Assert.assertNull(retval.getMsg());
    }

    @Test
    public void idleBeat(){
        ExecutorBiz executorBiz = new ExecutorBizClient(addressUrl, accessToken);
        final int jobId = 0;

        final ReturnT<String> retval = executorBiz.idleBeat(new IdleBeatParam((long)jobId));

        Assert.assertNotNull(retval);
        Assert.assertNull(((ReturnT<String>) retval).getContent());
        Assert.assertEquals(500, retval.getCode());
        Assert.assertEquals("job thread is running or has trigger queue.", retval.getMsg());
    }

    @Test
    public void run(){
        ExecutorBiz executorBiz = new ExecutorBizClient(addressUrl, accessToken);

        final TriggerParam triggerParam = new TriggerParam();
        triggerParam.setJobId(1l);
        triggerParam.setExecutorHandler("waterMeterJobHandler");
        triggerParam.setExecutorParams("{'createBy':'admin','createTime':1620374864023,'cron':'5/20 * * * * ? *','dataAcquiring':'PULL','dataFormat':'HEX','deptId':17,'enabled':true,'household':'401','id':6,'identification':'8e066104220001','name':'水表1','networkMode':'LTE','remark':'测试水表1','state':'ONLINE','type':'WATER','updateTime':1620374864023,'updatedBy':'admin'}");
        triggerParam.setExecutorBlockStrategy(ExecutorBlockStrategyType.COVER_EARLY.name());
        triggerParam.setGlueType(GlueType.BEAN.name());
        triggerParam.setGlueSource(null);
        triggerParam.setGlueUpdatetime(System.currentTimeMillis());
        triggerParam.setLogId(1l);
        triggerParam.setLogDateTime(System.currentTimeMillis());
        triggerParam.setBroadcastIndex(1);
        triggerParam.setBroadcastTotal(1);
        final ReturnT<String> retval = executorBiz.run(triggerParam);

        Assert.assertNotNull(retval);
    }

    @Test
    public void openWater(){
        ExecutorBiz executorBiz = new ExecutorBizClient(addressUrl, accessToken);

        final TriggerParam triggerParam = new TriggerParam();
        triggerParam.setJobId(1l);
        triggerParam.setExecutorHandler("waterOpenJobHandler");
        triggerParam.setExecutorParams("");
        triggerParam.setExecutorBlockStrategy(ExecutorBlockStrategyType.COVER_EARLY.name());
        triggerParam.setGlueType(GlueType.BEAN.name());
        triggerParam.setGlueSource(null);
        triggerParam.setGlueUpdatetime(System.currentTimeMillis());
        triggerParam.setLogId(1l);
        triggerParam.setLogDateTime(System.currentTimeMillis());
        triggerParam.setBroadcastIndex(1);
        triggerParam.setBroadcastTotal(1);
        final ReturnT<String> retval = executorBiz.run(triggerParam);

        Assert.assertNotNull(retval);
    }

    @Test
    public void closeWater(){
        ExecutorBiz executorBiz = new ExecutorBizClient(addressUrl, accessToken);

        final TriggerParam triggerParam = new TriggerParam();
        triggerParam.setJobId(1l);
        triggerParam.setExecutorHandler("waterCloseJobHandler");
        triggerParam.setExecutorParams("");
        triggerParam.setExecutorBlockStrategy(ExecutorBlockStrategyType.COVER_EARLY.name());
        triggerParam.setGlueType(GlueType.BEAN.name());
        triggerParam.setGlueSource(null);
        triggerParam.setGlueUpdatetime(System.currentTimeMillis());
        triggerParam.setLogId(1l);
        triggerParam.setLogDateTime(System.currentTimeMillis());
        triggerParam.setBroadcastIndex(1);
        triggerParam.setBroadcastTotal(1);
        final ReturnT<String> retval = executorBiz.run(triggerParam);

        Assert.assertNotNull(retval);
    }

    @Test
    public void kill(){
        ExecutorBiz executorBiz = new ExecutorBizClient(addressUrl, accessToken);

        final int jobId = 0;

        // Act
        final ReturnT<String> retval = executorBiz.kill(new KillParam((long)jobId));

        // Assert result
        Assert.assertNotNull(retval);
        Assert.assertNull(((ReturnT<String>) retval).getContent());
        Assert.assertEquals(200, retval.getCode());
        Assert.assertNull(retval.getMsg());
    }

    @Test
    public void log(){
        ExecutorBiz executorBiz = new ExecutorBizClient(addressUrl, accessToken);
        final long logDateTim = 0L;
        final long logId = 0;
        final int fromLineNum = 0;

        final ReturnT<LogResult> retval = executorBiz.log(new LogParam(logDateTim, logId, fromLineNum));

        Assert.assertNotNull(retval);
    }
}

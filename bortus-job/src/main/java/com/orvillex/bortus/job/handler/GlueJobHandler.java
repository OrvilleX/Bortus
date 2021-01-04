package com.orvillex.bortus.job.handler;

import com.orvillex.bortus.job.biz.models.ReturnT;
import com.orvillex.bortus.job.log.JobLogger;

/**
 * Glue任务处理器
 */
public class GlueJobHandler extends IJobHandler {

	private long glueUpdatetime;
	private IJobHandler jobHandler;
	public GlueJobHandler(IJobHandler jobHandler, long glueUpdatetime) {
		this.jobHandler = jobHandler;
		this.glueUpdatetime = glueUpdatetime;
	}
	public long getGlueUpdatetime() {
		return glueUpdatetime;
	}

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		JobLogger.log("----------- glue.version:"+ glueUpdatetime +" -----------");
		return jobHandler.execute(param);
	}
}

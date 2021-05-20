package com.orvillex.bortus.job.thread;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.orvillex.bortus.job.biz.models.HandleCallbackParam;
import com.orvillex.bortus.job.biz.models.ReturnT;
import com.orvillex.bortus.job.biz.models.TriggerParam;
import com.orvillex.bortus.job.executor.JobExecutor;
import com.orvillex.bortus.job.handler.IJobHandler;
import com.orvillex.bortus.job.log.JobFileAppender;
import com.orvillex.bortus.job.log.JobLogger;
import com.orvillex.bortus.job.util.ShardingUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 任务线程
 */
public class JobThread extends Thread {
	private static Logger logger = LoggerFactory.getLogger(JobThread.class);

	private Long jobId;
	private IJobHandler handler;
	private LinkedBlockingQueue<TriggerParam> triggerQueue;
	private Set<Long> triggerLogIdSet;

	private volatile boolean toStop = false;
	private String stopReason;

	private boolean running = false;
	private int idleTimes = 0;

	public JobThread(Long jobId, IJobHandler handler) {
		this.jobId = jobId;
		this.handler = handler;
		this.triggerQueue = new LinkedBlockingQueue<TriggerParam>();
		this.triggerLogIdSet = Collections.synchronizedSet(new HashSet<Long>());
	}

	public IJobHandler getHandler() {
		return handler;
	}

	/**
	 * 添加新的执行到队列
	 */
	public ReturnT<String> pushTriggerQueue(TriggerParam triggerParam) {
		if (triggerLogIdSet.contains(triggerParam.getLogId())) {
			logger.info(">>>>>>>>>>> repeate trigger job, logId:{}", triggerParam.getLogId());
			return new ReturnT<String>(ReturnT.FAIL_CODE, "repeate trigger job, logId:" + triggerParam.getLogId());
		}

		triggerLogIdSet.add(triggerParam.getLogId());
		triggerQueue.add(triggerParam);
		return ReturnT.SUCCESS;
	}

	/**
	 * 删除任务线程
	 */
	public void toStop(String stopReason) {
		this.toStop = true;
		this.stopReason = stopReason;
	}

	/**
	 * 任务是否执行
	 */
	public boolean isRunningOrHasQueue() {
		return running || triggerQueue.size() > 0;
	}

	@Override
	public void run() {
		try {
			handler.init();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}

		while (!toStop) {
			running = false;
			idleTimes++;

			TriggerParam triggerParam = null;
			ReturnT<String> executeResult = null;
			try {
				triggerParam = triggerQueue.poll(3L, TimeUnit.SECONDS);
				if (triggerParam != null) {
					running = true;
					idleTimes = 0;
					triggerLogIdSet.remove(triggerParam.getLogId());

					String logFileName = JobFileAppender.makeLogFileName(new Date(triggerParam.getLogDateTime()),
							triggerParam.getLogId());
					JobFileAppender.contextHolder.set(logFileName);
					ShardingUtil.setShardingVo(new ShardingUtil.ShardingVO(triggerParam.getBroadcastIndex(),
							triggerParam.getBroadcastTotal()));

					JobLogger.log("<br>----------- bortus-job job execute start -----------<br>----------- Param:"
							+ triggerParam.getExecutorParams());

					if (triggerParam.getExecutorTimeout() > 0) {
						Thread futureThread = null;
						try {
							final TriggerParam triggerParamTmp = triggerParam;
							FutureTask<ReturnT<String>> futureTask = new FutureTask<ReturnT<String>>(
									new Callable<ReturnT<String>>() {
										@Override
										public ReturnT<String> call() throws Exception {
											return handler.execute(triggerParamTmp.getExecutorParams());
										}
									});
							futureThread = new Thread(futureTask);
							futureThread.start();

							executeResult = futureTask.get(triggerParam.getExecutorTimeout(), TimeUnit.SECONDS);
						} catch (TimeoutException e) {

							JobLogger.log("<br>----------- bortus-job job execute timeout");
							JobLogger.log(e);

							executeResult = new ReturnT<String>(IJobHandler.FAIL_TIMEOUT.getCode(),
									"job execute timeout ");
						} finally {
							futureThread.interrupt();
						}
					} else {
						executeResult = handler.execute(triggerParam.getExecutorParams());
					}

					if (executeResult == null) {
						executeResult = IJobHandler.FAIL;
					} else {
						executeResult.setMsg((executeResult != null && executeResult.getMsg() != null
								&& executeResult.getMsg().length() > 50000)
										? executeResult.getMsg().substring(0, 50000).concat("...")
										: executeResult.getMsg());
						executeResult.setContent(null); // limit obj size
					}
					JobLogger.log("<br>----------- bortus-job job execute end(finish) -----------<br>----------- ReturnT:"
							+ executeResult);

				} else {
					if (idleTimes > 30) {
						if (triggerQueue.size() == 0) { // avoid concurrent trigger causes jobId-lost
							JobExecutor.removeJobThread(jobId, "excutor idel times over limit.");
						}
					}
				}
			} catch (Throwable e) {
				if (toStop) {
					JobLogger.log("<br>----------- JobThread toStop, stopReason:" + stopReason);
				}

				StringWriter stringWriter = new StringWriter();
				e.printStackTrace(new PrintWriter(stringWriter));
				String errorMsg = stringWriter.toString();
				executeResult = new ReturnT<String>(ReturnT.FAIL_CODE, errorMsg);

				JobLogger.log("<br>----------- JobThread Exception:" + errorMsg
						+ "<br>----------- bortus-job job execute end(error) -----------");
			} finally {
				if (triggerParam != null) {
					if (!toStop) {
						TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(),
								triggerParam.getLogDateTime(), executeResult));
					} else {
						ReturnT<String> stopResult = new ReturnT<String>(ReturnT.FAIL_CODE,
								stopReason + " [job running, killed]");
						TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(),
								triggerParam.getLogDateTime(), stopResult));
					}
				}
			}
		}

		while (triggerQueue != null && triggerQueue.size() > 0) {
			TriggerParam triggerParam = triggerQueue.poll();
			if (triggerParam != null) {
				ReturnT<String> stopResult = new ReturnT<String>(ReturnT.FAIL_CODE,
						stopReason + " [job not executed, in the job queue, killed.]");
				TriggerCallbackThread.pushCallBack(
						new HandleCallbackParam(triggerParam.getLogId(), triggerParam.getLogDateTime(), stopResult));
			}
		}

		try {
			handler.destroy();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}

		logger.info(">>>>>>>>>>> bortus-job JobThread stoped, hashCode:{}", Thread.currentThread());
	}
}

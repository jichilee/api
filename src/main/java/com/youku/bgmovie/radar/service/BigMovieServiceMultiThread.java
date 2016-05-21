package com.youku.bgmovie.radar.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 框架暂不提供异常处理，默认往上抛出！
 * TODO 多线程模式处理批量请求！
 * @author LIQI
 *
 */
public abstract class BigMovieServiceMultiThread extends BigMovieService{

	private static Log logger = LogFactory.getLog(BigMovieServiceMultiThread.class);
	
	public AtomicInteger startThreads = new AtomicInteger();
	public AtomicInteger doneTthreads = new AtomicInteger();
	
	private final int attempt = 60;
	
	/**
	 * 没使用cache！
	 */
	@Override
	public Object excute(Map<String, String> paraMap) throws Exception {
		logger.info("excute" + paraMap);
		doMultiThreadJob(paraMap);
		return getReturnJson(paraMap);
	}

	public void doMultiThreadJob(Map<String, String> paraMap) throws Exception {
		for(String sql : prepareSqls(paraMap)){
			//TODO use a sub thread to do subJob...
			doSubJob(sql);
		}
		
		while(!isAllThreadDone()){
			Thread.sleep(1000);
		}
	}
	
	public void doSubJob(String sql) throws Exception {
		int i = 0;
		while(subJobStart()){
			i ++;
			if(i > attempt){
				logger.info("attempt times is max." + attempt);	
				throw new Exception("Attempt times is max. waiting to get a thread from pool TIMEOUT!");
			}
			Thread.sleep(1000);//thread num is max.
			logger.info("Thread num is max,waiting to get a thread from pool");
		}
		
		Object ret = queryFromDB(sql);
		ret = filterResult(ret);
		subJobDone();
		doSubJobCallBack(ret);
	}
	
	public boolean isAllThreadDone(){
		return doneTthreads.get() == startThreads.get();
	}
	
	/**
	 * check and mark if can get a thread from pool
	 * @return
	 */
	public boolean subJobStart(){
		startThreads.addAndGet(1);
		return false;
//		//如果需要对thread数量做个限制，替换如下代码：提升max 作为类的field，
//		int max = 10;
//		return max < startThreads.addAndGet(1);
	}
	
	public void subJobDone(){
		doneTthreads.addAndGet(1);
	}
	
	public abstract void doSubJobCallBack(Object ret);
	
	public abstract Object getReturnJson(Map<String, String> paraMap);
	
	public abstract List<String> prepareSqls(Map<String, String> paraMap);
	
	@Deprecated
	protected String generateSql(Map<String, String> paraMap) throws Exception {
		return null;
	}
}

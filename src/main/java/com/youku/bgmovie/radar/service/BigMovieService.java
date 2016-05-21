package com.youku.bgmovie.radar.service;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.youku.bgmovie.radar.db.phoenix.connection.PhoenixConnectionManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.bgmovie.radar.db.redis.RedisKeepAliveApi;
import com.youku.bgmovie.radar.http.base.AbstractHttpImpl;
import com.youku.bgmovie.radar.service.task.CounterUtil;
import com.youku.bgmovie.radar.service.task.QueryTask;
import com.youku.bgmovie.radar.utils.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BigMovieService extends AbstractHttpImpl{

	private static Logger logger = LoggerFactory.getLogger(BigMovieService.class);


	public static long timeout = 60000;
	public String dataKey = "data";
	private Connection conn;

	public static Map<String, Map<Integer, JSONArray>> subRs = new Hashtable<String, Map<Integer, JSONArray>>();
	
	public static ExecutorService executorService = null;
	
	static {
		try{
			executorService = buildThreadPool(10, 50, 10000, 200, new ThreadPoolExecutor.DiscardPolicy());
		} catch (Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public synchronized void init(Map<String, String> paraMap) throws Exception {
		if(conn != null && conn.isValid(3000)){
			return;
		}
		logger.info("init conn with:{}", paraMap);
		if(org.apache.commons.lang3.StringUtils.isBlank(paraMap.get("quorum"))){
			throw new Exception("param quorum is null.");
		}
		conn = PhoenixConnectionManager.getConnection(paraMap.get("quorum"));
		logger.info("init conn with:{} ok.", paraMap);
	}
	
	@Override
	public Object excute(Map<String, String> paraMap) throws Exception {

//		paraMap.put("quorum", "cdn241,cdn242:2181");
		logger.info("excute" + paraMap);
		init(paraMap);



		Object ret = null;
		
		if(paraMap.get("cache") != null || useCache()){
			RedisKeepAliveApi.init();
			ret = readCache(paraMap);
			if(ret != null){
				logger.debug("found in cache:" + ret);
				return ret;
			}
		}
		
		if(ret == null){//route dispatch
			if(paraMap.get("sync") != null){
				ret = concurrentCore(paraMap, paraMap.get("sync").toString());
			} else if(paraMap.get("kv") != null){
				ret = getKV(paraMap);
			} else {
				ret = queryFromDB(generateSql(paraMap));
			}
		}
		ret = filterResult(ret);
		
		try{
			JSONObject _ret = JSONObject.fromObject(ret);
			if(_ret.getJSONArray("data") == null || "null".equals(_ret.getJSONArray("data"))) {
				_ret.put("data", "[]");
			}
			//paraMap.get("cache") != null  : test;
			if(paraMap.get("cache") != null || useCache() && _ret != null && _ret.getJSONArray("data").size() > 0){
				flushcache(getCacheKey(paraMap), _ret.toString(), MapUtil.getInt(paraMap, "cacheM", 60) * 60);
				logger.debug("write in cache:" + ret);
			}
		} catch (Exception e){
			logger.info(e.getMessage());
		}
		
		logger.debug("query ret:" + ret);
		return ret;
	}
	
	/**
	 * 跨天采用多线程子任务方式处理，提高查询速度。
	 * sync 本次主任务的key，用于统计子任务是否都完成
	 * 
	 * @param paraMap
	 * @return
	 * @throws Exception 
	 */
	public Object concurrentCore(Map<String, String> paraMap, String sync) throws Exception{
		int taskNo = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		Date start = sdf.parse(paraMap.get("startDate").toString()),
				end = sdf.parse(paraMap.get("endDate").toString());
		
		JSONObject dataToReturn = new JSONObject();
		Map<Integer, JSONArray> sub = new HashMap<Integer, JSONArray>();
		subRs.put(sync, sub);
		while(start.before(end)){
			paraMap.put("startDate", sdf.format(start));
			start = DateUtils.addDays(start, 1);
			paraMap.put("endDate", sdf.format(start));
			executorService.submit(new QueryTask(generateSql(paraMap), sync, taskNo));
			taskNo ++;
		}
		
		//wait all sub-task is done.
		waitAllSubTask(sync, taskNo);
		logger.info("finish query");
		JSONArray merge = new JSONArray();
		for(JSONArray s : subRs.get(sync).values()){
			merge.addAll(s);
		}
		dataToReturn.put(dataKey, merge);
		subRs.remove(sync);
		CounterUtil.remove(sync);
		return dataToReturn;
	}
	
	public void waitAllSubTask(String sync, int tasks) throws InterruptedException{
		int useTime = 0;
		while(useTime < timeout && CounterUtil.get(sync) < tasks){
			logger.info("toal:" + tasks + " finish:" + CounterUtil.get(sync) + " wait:" + (tasks - CounterUtil.get(sync)) + " times:" + useTime);
			Thread.sleep(500);
			useTime += 500;
		}
		if(useTime >= timeout){
			logger.error("time out:" + sync + " times:" + useTime);
		}
		logger.info("toal:" + tasks + " finish:" + CounterUtil.get(sync) + " wait:" + (tasks - CounterUtil.get(sync)) + " times:" + useTime);
	}
	
	public Object filterResult(Object ret){
		return ret;
	}
	
	/**
	 * 添加其他逻辑入口，返回非null则不再继续往下处理。
	 * @param paraMap
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws Exception 
	 */
	protected Object otherDataSource(Map<String, String> paraMap) throws Exception {
		return null;
	}
	
	protected boolean useCache(){
		return false;
	}
	
	protected Object readCache(Map<String, String> paraMap){
		return RedisKeepAliveApi.get(getCacheKey(paraMap));
	}
	
	protected Object flushcache(String key, String value, int seconds){
		return RedisKeepAliveApi.setEx(key, value, seconds);
	}
	
	protected String getCacheKey(Map<String, String> paraMap){
		String key = "_c_";
		for(String k : paraMap.keySet()){
			if("jsoncallback".equals(k)) continue;
			if("_".equals(k)) continue;
			key += paraMap.get(k) + ",";
		}
		return key;
	}
	
	protected abstract String generateSql(Map<String, String> paraMap) throws Exception;
	
	protected JSONObject getKV(Map<String, String> paraMap) throws Exception {
		return null;
	}
	
	protected JSONObject queryFromDB(String sql) throws Exception {
		
		logger.debug(sql);
		if(null == sql){
			throw new Exception("query with null sql!");
		}
		
//        Connection conn=null;
        JSONObject dataToReturn = new JSONObject();
        try {
//            conn = PhoenixConnectionManager.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            
            dataToReturn.put(dataKey, mappingRStoObject(rs));
        } catch (Exception e) {
            logger.error("", e);
            throw new Exception("query exception!");
        } finally {
            if(conn!=null){
                conn.close();
            }
        }
        return dataToReturn;
    }
	
	public JSONArray mappingRStoObject(ResultSet rs) throws SQLException{
		ResultSetMetaData rsmd=rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        JSONArray data = new JSONArray();
        JSONObject kv = new JSONObject();
        
        Object tmp = "";
        while(rs.next()) {
            for(int i=1;i<=columnCount;i++) {
            	tmp = verfiyAndModify(rs.getObject(i));
            	kv.put(verfiyAndModifyKey(rsmd.getColumnName(i)).toLowerCase(), tmp);
            }
            data.add(kv);
        }
        
        return data;
	}
	
	/**
	 * 简单校验，如果为null设置为空。如果有特殊需求，判断具体类型给定默认值。
	 * @param o
	 * @return
	 */
	protected Object verfiyAndModify(Object o){
		if(o == null){
			o = "";
		}
		return o;
	}
	
	protected Object verfiyAndModify(Object o, Object defaultValue){
		if(o == null){
			return defaultValue;
		}
		return o;
	}

	/**
	 * 
	 * @param o
	 * @return
	 */
	protected String verfiyAndModifyKey(String o){
		if(o.contains(".")){
			return o.split("\\.")[1];
		}
		return o;
	}
	
	
	public static ExecutorService buildThreadPool(int corePoolSize, 
			int maximumPoolSize, 
			long keepAliveTime,
			int capacity,
			RejectedExecutionHandler f){
		
		ExecutorService daoExecutor = null;
		try{
			daoExecutor = new ThreadPoolExecutor(
					corePoolSize,
					maximumPoolSize,
					keepAliveTime,
					TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(capacity),
					f);
		} catch (Exception e){
			e.printStackTrace();
		}
		return daoExecutor;
	}
	
	@Override
	public boolean singleton() {
		// TODO Auto-generated method stub
		return false;
	}
}

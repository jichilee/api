package com.youku.bgmovie.radar.service.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.youku.bgmovie.radar.service.BigMovieService;
/**
 * 多天查询。
 * 
 * @author liqi7
 * 
 */
public class QueryTask implements Callable<Integer> {
	
	final static String dataKey = "data";
	final String sql;
	final String sync;
	int index;
	public QueryTask (String sql, String sync, int index) {
		System.out.println(sql);
		this.sql = sql;
		this.sync = sync;
		this.index = index;
	}

	/**
	 * 多线程下，返回值无意义
	 */
	@Override
	public Integer call() throws Exception {
		 queryFromDB(sql);
		 return 1;
	}
	
	protected void queryFromDB(String sql) throws Exception {
		CounterUtil.registCounters(sync);
        Connection conn=null;
        try {
//            conn = PhoenixConnectionManager.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            //callback put result
            BigMovieService.subRs.get(sync).put(index, mappingRStoObject(rs));
        } catch (Exception e) {
        	e.printStackTrace();
            throw new Exception("query exception!");
        } finally {
            if(conn!=null){
                conn.close();
            }
            CounterUtil.incrementAndGet(sync);
        }
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
            	kv.put(verfiyAndModifyKey(rsmd.getColumnName(i)), tmp.toString());
            }
            data.add(kv);
        }
        
        return data;
	}

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
	
}
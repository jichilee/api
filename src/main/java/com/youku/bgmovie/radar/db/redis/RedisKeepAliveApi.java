package com.youku.bgmovie.radar.db.redis;

import java.util.Set;

import com.youku.bgmovie.radar.utils.DBUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author LIQI
 *
 */
public class RedisKeepAliveApi {

//	private static String host = "192.168.56.101";
//	private static int port = 6379;
	
	static String host = "10.154.156.107";
	static int port = 6379;
	
//	static String host = "10.106.21.64";
//	static int port = 6381;
	
	static RedisKeepAliveApi rka;
	private JedisPool pool;
	
	//不支持多线程
	public static synchronized RedisKeepAliveApi init(){
		if(rka != null) return rka;
		rka = new RedisKeepAliveApi();
		rka.pool = DBUtil.getJedisPool(host, port);
		return rka;
	}
	
	public static Jedis getResource(){
		try {
			return rka.pool.getResource();
        } catch (Exception e) {
        	e.printStackTrace();
        	return null;
        }
	}
	
	public static String get(String k){
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.get(k);
        } catch (Exception e) {
        	e.printStackTrace();
        	return null;
        } finally {
        	if(jedis != null){
        		returnResource(jedis);
        	}
        }
	}
	
	public static String setEx(String key, String value, int seconds){
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.setex(key, seconds, value);
        } catch (Exception e) {
        	e.printStackTrace();
        	return null;
        } finally {
        	if(jedis != null){
        		returnResource(jedis);
        	}
        }
	}
	
	public static Set<String> keys(String p){
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.keys(p);
        } catch (Exception e) {
        	e.printStackTrace();
        	return null;
        } finally {
        	if(jedis != null){
        		returnResource(jedis);
        	}
        }
	}
	
	public static int clearCache(String pattern){
		Jedis jedis = null;
		try {
			jedis = getResource();
			int num = 0;
			for(String k : jedis.keys(pattern)){
				jedis.del(k);
				num++;
			}
			return num;
        } catch (Exception e) {
        	e.printStackTrace();
        	return 0;
        } finally {
        	if(jedis != null){
        		returnResource(jedis);
        	}
        }
	}
	
	public static void returnBrokenResource(Jedis jedis){
		try {
			rka.pool.returnBrokenResource(jedis);
        } catch (Exception e) {
        }
	}
	
	public static void returnResource(Jedis jedis){
		try {
			rka.pool.returnResource(jedis);
        } catch (Exception e) {
        	returnBrokenResource(jedis);
        }
	}
}

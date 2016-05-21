package com.youku.bgmovie.radar.utils;


import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class DBUtil {

	public static JedisPoolConfig getJedisPoolConfig(){
		JedisPoolConfig jpc = new JedisPoolConfig();
		jpc.setMaxIdle(2);
		//consumer and producer holding 2.
		jpc.setMaxTotal(4);
		jpc.setMaxWaitMillis(1000);
		jpc.setTestOnBorrow(true);
		return jpc;
	}
	
	public static JedisPool getJedisPool(String host, int port){
		return new JedisPool(getJedisPoolConfig(), host, port);
	}
}

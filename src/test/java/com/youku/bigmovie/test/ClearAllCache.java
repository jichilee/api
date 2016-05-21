package com.youku.bigmovie.test;

import java.util.Set;

import org.junit.Test;

import com.youku.bgmovie.radar.db.redis.RedisKeepAliveApi;

public class ClearAllCache {

	@Test
	public void clearAll(){
		RedisKeepAliveApi.init();
		System.out.println(RedisKeepAliveApi.clearCache("_c_*"));
	}
	
	@Test
	public void query(){
		RedisKeepAliveApi.init();
		String ks = RedisKeepAliveApi.get("_c_201509290000,999999,select count(1) from (select ptime, sum(request), sum(bandwidth) from kafka.cdn_detail where ptime>=#startDate# and ptime<#endDate# and userid='710450' and httpcode='206'  group by ptime,httpcode),1,1,201509300000,");
		System.out.println(ks);
//		for(String k : ks){
//			System.out.println(k + "#" + RedisKeepAliveApi.get(k));
//		}
	}
	
	@Test
	public void tt(){
		String oo = "aaa.aaaaaaa";
		if(oo.contains("."))
			System.out.println(oo.split("\\.")[1]);
	}
}

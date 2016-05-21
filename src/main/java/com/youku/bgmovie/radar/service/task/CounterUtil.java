package com.youku.bgmovie.radar.service.task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class CounterUtil {
	
	/**
	 * 排查少了两个task的原因，初步怀疑是两个task发生了死锁：
	 */
	private static Map<String, AtomicLong> counterMap = new ConcurrentHashMap<String, AtomicLong>();
	
	public static Map<String, AtomicLong> registCounters(String counterName){
		synchronized (CounterUtil.class) {
			if(counterMap == null){
				counterMap = new ConcurrentHashMap<String, AtomicLong>();
			}
			if(counterMap.get(counterName) == null){
				counterMap.put(counterName, new AtomicLong());
			}
		}
		return counterMap;
	}
	
	public static void remove(String counterName){
		if(counterMap != null){
			counterMap.remove(counterName);
		}
	}
	
	/**
	 * 初始化
	 * @param counterName
	 * @return
	 */
	public static Map<String, AtomicLong> registCounters(String...counterName){
		synchronized (CounterUtil.class) {
			for(String name : counterName){
				if(counterMap.get(name) == null){
					counterMap.put(name, new AtomicLong());
				}
			}
		}
		
		return counterMap;
	}
	
	public static AtomicLong getCounter(String counterName){
		return registCounters(counterName).get(counterName);
	}
	
	public static long get(String counterName){
		return registCounters(counterName).get(counterName).get();
	}
	/**
	 * +1
	 * @param counterName
	 * @return
	 */
	public static long incrementAndGet(String counterName){
		return registCounters(counterName).get(counterName).incrementAndGet();
	}
	
	public static long addAndGet(String counterName, long delta){
		return registCounters(counterName).get(counterName).addAndGet(delta);
	}
}

package com.youku.bgmovie.radar.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class MapUtil<K, V> {

	/**
	 * 忽略大小写
	 * args: k=v, k=v
	 * @param args
	 * @return
	 */
	public static Map<String, String> buildMapFromArgs(String[] args, boolean isToLowerCase) throws NullPointerException, ArrayIndexOutOfBoundsException {
		Map<String, String> params = new HashMap<String, String>();
		String tmp[] = null;
		for(String arg : args){
			tmp = StringUtils.split(isToLowerCase ? arg.toLowerCase() : arg, "=");
			if(tmp.length < 2){
				params.put(tmp[0], "");
			} else if (tmp.length == 2){
				params.put(tmp[0], tmp[1]);
			} else {
				params.put(tmp[0], StringUtils.join(tmp, "=", 1, tmp.length));
			}
		}
		return params;
	}
	
	public static Map<String, String> buildMapFromArgs(String[] args) throws NullPointerException, ArrayIndexOutOfBoundsException {
		return buildMapFromArgs(args, false);
	}
	
	public Map<K, V> buildMapFromArgsKV(String[] args, boolean isToLowerCase) throws NullPointerException, ArrayIndexOutOfBoundsException {
		Map<K, V> params = new HashMap<K, V>();
		String tmp[] = null;
		for(String arg : args){
			tmp = StringUtils.split(isToLowerCase ? arg.toLowerCase() : arg, "=");
			if(tmp.length < 2){
				params.put( (K)tmp[0], null );
			} else {
				params.put( (K)tmp[0], (V)tmp[1] );
			}
		}
		return params;
	}
	
	public Map<K, V> buildMapFromArgsKV(String[] args) throws NullPointerException, ArrayIndexOutOfBoundsException {
		return buildMapFromArgsKV(args, false);
	}
	
	public V get(Map<K, V> map, K k, V _default){
		return map.get(k) == null ? _default : map.get(k);
	}
	
	public int getInt(Map<K, V> map, K k, int _default){
		return map.get(k) == null ? _default : Integer.parseInt(map.get(k).toString());
	}
	
	public static String getString(Map<String, String> map, String k, String _default){
		return map.get(k) == null ? _default : map.get(k);
	}
	
	public static int getInt(Map<String, String> map, String k, int _default){
		return map.get(k) == null ? _default : Integer.parseInt(map.get(k).toString());
	}
	
	public static short getShort(Map<String, String> map, String k, short _default){
		return map.get(k) == null ? _default : Short.parseShort(map.get(k).toString());
	}
	
	public static long getLong(Map<String, String> map, String k, Long _default){
		return map.get(k) == null ? _default : Long.parseLong(map.get(k).toString());
	}
	
	public static boolean getBoolean(Map<String, String> map, String k, boolean _default){
		return map.get(k) == null ? _default : Boolean.parseBoolean(map.get(k).toString());
	}
}

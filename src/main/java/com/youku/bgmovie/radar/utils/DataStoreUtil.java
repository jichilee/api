package com.youku.bgmovie.radar.utils;


import java.util.HashMap;
import java.util.Map;

/**
 * 1、入库sql中需要把'和\转义，避免破坏sql语法。</br>
 * 经测试下面值可以入库：</br>
 * 	我\'朋友[]!@#$%^&*()_-+|<>?\\;:"~`,a dog!</br>
 * 入库后：</br>
 * | 我'朋友[]!@#$%^&*()_-+|<>?;:"~`,a dog! |</br>
 * </br>
 * 2、转换工具默认按照"\t", "="进行两级分割。</br>
 * 
 * @author LIQI
 *
 */
public class DataStoreUtil {

	/** \t */
	public final static String spliter = "\t";
	/** , */
	public final static String kvall_spliter = ",";
	/** & */
	public final static String kvs_spliter = "&";
	/** = */
	public final static String kv_spliter = "=";
	/** : */
	public final static String hkv_spliter = ":";
	/** # */
	public final static String word_spliter = "#";
	/** | */
	public final static String set_spliter = "\\|";
	/** _ */
	public final static String template_spliter = "_";
	
	
	////约定操作符映射,1000起步
	private final static String templateKey = word_spliter + "_key_" + word_spliter;
	final static String l = "<" + templateKey;
    final static String le = "<=" + templateKey;
    final static String b = ">" + templateKey;
    final static String be = ">=" + templateKey;
    final static String e = "=" + templateKey;//1004
    
    final static String llike = " like " + templateKey + "% ";//keyword at left  1005
    final static String rlike = " like % " + templateKey + " ";//right
    final static String clike = " like %" + templateKey + "% ";//center
    
    final static Map<String, String > opers = new HashMap<String, String>();
    
    static {
    	opers.put("1000", l);
    	opers.put("1001", le);
    	opers.put("1002", b);
    	opers.put("1003", be);
    	opers.put("1004", e);
    	
    	opers.put("1005", llike);
    	opers.put("1006", rlike);
    	opers.put("1007", clike);
    	
    }
    
    /**
     * 最好只对like语句进行匹配。因为在对整个sql进行匹配时，有可能把其他字符误判替换。
     * t0.showname_1007
     * return
     * t0.showname_1007 like '%t0.showname%'
     * 
     * @param likeSql
     * @param key
     * @return
     * @throws Exception 
     */
	public static String keyLike(String likeField) throws Exception{
		String fields[] = likeField.split(template_spliter);
		if(fields.length < 2) throw new Exception("like model conf error#legth < 2!");
		return fields[0] + opers.get(fields[1]).replace(templateKey, word_spliter + fields[0] + word_spliter);
	}
    
	public static String replaceIllegalChar(String value){
		if(value == null) return null;
		
		return value.replace("\\", "\\\\").replace("'", "\\'");
	}
	
	public static Map<String, Object> strTomap(String tmp, String kvSpliter, String hkvSpliter){
		Map<String, Object> map = new HashMap<String, Object>();
		String kvs[] = tmp.split(kvSpliter);
		String _kv[] = null;
		for(String kv : kvs){
			_kv = kv.split(hkvSpliter);
			map.put(_kv[0], _kv[1]);
		}
		return map;
	}
	
	/**
	 * user spliter {@link #spliter}, {@link #kv_spliter}
	 * @param tmp
	 * @return
	 */
	public static Map<String, Object> strTomap(String tmp){
		Map<String, Object> map = new HashMap<String, Object>();
		String kvs[] = tmp.split(spliter);
		String _kv[] = null;
		for(String kv : kvs){
			_kv = kv.split(kv_spliter);
			map.put(_kv[0], _kv[1]);
		}
		return map;
	}
	
	public static String mapToStr(Map<String, Object> kv, String kvSpliter, String hkvSpliter){
		
		String tmp = "";
		for(String k : kv.keySet()){
			tmp += k + hkvSpliter + kv.get(k) + kvSpliter;
		}
		if(tmp.endsWith(kvSpliter)){
			tmp = tmp.substring(0, tmp.length() -1);
		}
		return tmp;
	}
	
	public static String mapToStr(Map<String, Object> kv){
		
		String tmp = "";
		for(String k : kv.keySet()){
			tmp += k + kv_spliter + kv.get(k) + spliter;
		}
		if(tmp.endsWith(spliter)){
			tmp = tmp.substring(0, tmp.length() -1);
		}
		return tmp;
	}
	
	public static String mapToKeyStr(Map<String, Object> kv, String...keys){
		
		String tmp = "";
		for(String k : keys){
			if("".equals(k)) continue;
			tmp += k + kv_spliter + kv.get(k) + spliter;
		}
		if(tmp.endsWith(spliter)){
			tmp = tmp.substring(0, tmp.length() -1);
		}
		return tmp;
	}
	
	public static String mapToKeyStr(Map<String, Object> kv, String keys){
		
		String tmp = "";
		for(String k : keys.split(spliter)){
			if("".equals(k)) continue;
			tmp += k + kv_spliter + kv.get(k) + spliter;
		}
		if(tmp.endsWith(spliter)){
			tmp = tmp.substring(0, tmp.length() -1);
		}
		return tmp;
	}
	
	public static String mapToKeyStr(String kvSpliter, String hkvSpliter, Map<String, Object> kv, String...keys){
		
		String tmp = "";
		for(String k : keys){
			if("".equals(k)) continue;
			tmp += k + hkvSpliter + kv.get(k) + kvSpliter;
		}
		if(tmp.endsWith(kvSpliter)){
			tmp = tmp.substring(0, tmp.length() -1);
		}
		return tmp;
	}
	
	public static String mapToKeyStr(String kvSpliter, String hkvSpliter, Map<String, Object> kv, String keys){
		
		String tmp = "";
		for(String k : keys.split(kvSpliter)){
			if("".equals(k)) continue;
			tmp += k + hkvSpliter + kv.get(k) + kvSpliter;
		}
		if(tmp.endsWith(kvSpliter)){
			tmp = tmp.substring(0, tmp.length() -1);
		}
		return tmp;
	}
}

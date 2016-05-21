package com.youku.bgmovie.radar.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.youku.bgmovie.radar.http.base.AbstractHttpImpl;

public class ConfigCache {
	private final static Log logger = LogFactory.getLog(ConfigCache.class);
	private final static String confPath="/interface.xml";
	private static ConfigCache instanct = null;
	static Map<String, Map<String, Object>> confMap = new HashMap<String, Map<String, Object>>();
	public static void init(){
		SAXReader saxReader = new SAXReader();
		try {
			String path = ConfigCache.class.getResource(confPath).getPath();
			Document doc = saxReader.read(path);
			Element root = doc.getRootElement();
			Iterator<Element> it = root.elementIterator();
			while(it.hasNext()){
				Element entity = it.next();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("class", entity.attributeValue("class"));
				map.put("memcacheprefix", entity.attributeValue("memcacheprefix"));
				map.put("contenttype", entity.attributeValue("contenttype"));
				map.put("name", entity.attributeValue("name"));
				map.put("num", entity.attributeValue("num"));
				
				Class<?> classThread = Class.forName(entity.attributeValue("class"));
				AbstractHttpImpl httpImpl = (AbstractHttpImpl) classThread.newInstance();
				map.put("bean", httpImpl);
				
				confMap.put(entity.attributeValue("num"), map);
			}
		} catch (DocumentException e) {
			logger.error("", e);
		} catch (ClassNotFoundException e) {
			logger.error("", e);
		} catch (InstantiationException e) {
			logger.error("", e);
		} catch (IllegalAccessException e) {
			logger.error("", e);
		}
	}

	public static ConfigCache getInstance(){
		if(instanct == null){
			instanct = new ConfigCache();
		}
		return instanct;
	}

	public Map<String, Object> getConfByNum(String num){
		if(!confMap.containsKey(num)){
			return null;
		}
		return confMap.get(num);
	}


	public static void main(String[] args) {
		System.out.println(ConfigCache.getInstance().getConfByNum("100001").get("class"));
	}
}

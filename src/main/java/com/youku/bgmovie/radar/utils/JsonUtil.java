package com.youku.bgmovie.radar.utils;

import java.util.Collection;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * json工具类
 *
 * @author yangming
 * @time 2013-11-20 18:36:13
 */
public class JsonUtil {
	/**
	 * @param object
	 * @return String
	 */
	public static String formatObject2Json(Object object) {
        String results = "";
        if (object instanceof Collection) {
            Collection objects = (Collection) object;
            results = JSONArray.fromObject(objects).toString();
        } else {
            results = JSONObject.fromObject(object).toString();
        }
        return results;
    }
}

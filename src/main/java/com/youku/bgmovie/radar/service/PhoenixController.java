package com.youku.bgmovie.radar.service;

import com.youku.bgmovie.radar.db.phoenix.connection.PhoenixConnectionManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Map;

/**
 * @author liqi7
 *
 */
public class PhoenixController extends BigMovieService{

	@Override
	protected JSONObject getKV(Map<String, String> paraMap) throws Exception {

//		init(paraMap);
//		JSONObject r = new JSONObject();
//		JSONArray data = new JSONArray();
//        JSONObject kv = new JSONObject();
//        kv.put("es", json);
//        data.add(kv);
//        r.put(dataKey, data);
        
		return null;
	}

	@Override
	protected String generateSql(Map<String, String> paraMap) throws Exception {
		// TODO Auto-generated method stub
		return paraMap.get("sql");
	}
}

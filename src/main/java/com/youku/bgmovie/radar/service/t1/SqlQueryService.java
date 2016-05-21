package com.youku.bgmovie.radar.service.t1;

import java.util.Map;
import com.youku.bgmovie.radar.service.BigMovieService;

/**
 * 通过sql查询多日期
 * @author LIQI
 *
 */
public class SqlQueryService extends BigMovieService{
	
	@Override
	public boolean useCache() {	
		return false;
	}
	
	@Override
	protected String generateSql(Map<String, String> paraMap) {
		//TODO
		String sql = paraMap.get("sql").toString();
		sql = sql.replace("#startDate#", paraMap.get("startDate").toString());
		sql = sql.replace("#endDate#", paraMap.get("endDate").toString());
		return sql;
	}
	
}

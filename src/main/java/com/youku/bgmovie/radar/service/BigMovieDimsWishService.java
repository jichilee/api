package com.youku.bgmovie.radar.service;

import java.util.Map;
import com.youku.bgmovie.radar.service.BigMovieService;

public class BigMovieDimsWishService extends BigMovieService{

	@Override
	protected String generateSql(Map<String, String> paraMap) throws Exception {
		if(paraMap.get("type") == null)
			throw new Exception("type error!" + paraMap.get("type"));
		
		String sql = "select t0.showid,t0.DATECOL,t0." + paraMap.get("type") + " from bgmovie.dims_wishpresale t0 where t0.DATECOL>=to_date('#startDate#','yyyyMMdd') and t0.DATECOL<=to_date('#endDate#','yyyyMMdd') and t0.SITE='" + paraMap.get("site") + "' and t0.SHOWID=" + paraMap.get("showId");
		sql = sql.replace("#startDate#", paraMap.get("startDate").toString());
		sql = sql.replace("#endDate#", paraMap.get("endDate").toString());
		return sql;
	}
}

package com.youku.bgmovie.radar.service;


import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

public class BigMoviePKshowService extends BigMovieService{

	/**
	 * select t0.RELEASEDATE,t0.SHOWID,t0.SHOWNAME || ',!' || t0.OTHERSHOWNAME as SHOWNAME,t0.YOUKUID,t0.YOUKUID,t0.DIRECTOR,t0.MAINACTOR,t0.TYPE  from bgmovie.show_detail t0 join bgmovie.dims_vv t1 on t0.showid=t1.showid where t0.YOUKUID >0 and t0.RELEASEDATE>=to_date('20150213','yyyyMMdd') and t0.RELEASEDATE<=to_date('20150419','yyyyMMdd') and  t0.SHOWID != 125308 and t1.vv >0 and t1.DATECOL = to_date('20150213','yyyyMMdd')  order by t0.RELEASEDATE desc limit 3 ;
	 */
	@Override
	protected String generateSql(Map<String, String> paraMap) {
		
		String sql = "select distinct t0.RELEASEDATE as RELEASEDATE,t0.SHOWID as SHOWID,t0.SHOWNAME || ',!' || t0.OTHERSHOWNAME as SHOWNAME,t0.YOUKUID as YOUKUID,t0.DIRECTOR as DIRECTOR,t0.MAINACTOR as MAINACTOR,t0.TYPE as TYPE  from bgmovie.show_detail t0 join bgmovie.dims_vv t1 on t0.showid=t1.showid where t0.YOUKUID >0 and t0.RELEASEDATE>=to_date('#startDate#','yyyyMMdd') and t0.RELEASEDATE<=to_date('#endDate#','yyyyMMdd') and  t0.SHOWID != #t0.SHOWID# and t1.vv >0 and t1.DATECOL = to_date('#vvday#','yyyyMMdd') order by t0.RELEASEDATE desc limit 3 ";
		sql = sql.replace("#startDate#", paraMap.get("startDate").toString());
		sql = sql.replace("#endDate#", paraMap.get("endDate").toString());	
		sql = sql.replace("#t0.SHOWID#", paraMap.get("showId").toString());
		sql = sql.replace("#vvday#", beforeNday(-7));
		return sql;
	}
	
	public Object filterResult(Object ret){
		ret = ret.toString().replace(",!", ",").replace(":!", ":");
		return ret;
	}
	
	private String beforeNday(int n){
		Format f = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, n);
        return f.format(c.getTime());
	}

}

package com.youku.bgmovie.radar.service;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author liqi7
 *
 */
public class ElasticSearchController extends BigMovieService{

	ElasticSearchService elasticSearchService = ElasticSearchService.getElasticSearchService();

//	@RequestMapping(value = "/mroom/{indics},{types},{start},{end}")
//	public @ResponseBody Map<String, String> searchMachineRoom(@PathVariable String indics, @PathVariable String types,
//			@PathVariable String start, @PathVariable String end,
//					 HttpServletRequest request, HttpServletResponse response) throws ElasticsearchException, Exception{
//		System.out.println(request.getRequestURI());
//		Map<String, String> ret = new HashMap<>();
//		String json = elasticSearchService.search(indics, types, start, end);
//		ret.put("data", json);
//		
//		return ret;
//	}
//	
//	@RequestMapping(value = "/mroom/{indics}/{types}/{start}/{end}")
//	public void search(@PathVariable String indics, @PathVariable String types,
//			@PathVariable String start, @PathVariable String end,
//					 HttpServletRequest request, HttpServletResponse response) throws ElasticsearchException, Exception{
//		System.out.println(request.getRequestURI());
//		if (request.getRequestURI().endsWith("/amap/main/index")) {
//            response.sendRedirect("/loganalytics-frontend/web/global/index");
//            return;
//        }
//		response.setCharacterEncoding("UTF-8");
//		response.setContentType("application/json;charset=utf-8");
//		
//		PrintWriter pw = response.getWriter();
//		String json = elasticSearchService.search(indics, types, start, end);
//		pw.println(json);
//	}

	@Override
	protected JSONObject getKV(Map<String, String> paraMap) throws Exception {
		String json = elasticSearchService.search(paraMap);
		
		JSONObject r = new JSONObject();
		JSONArray data = new JSONArray();
        JSONObject kv = new JSONObject();
        kv.put("es", json);
        data.add(kv);
        r.put(dataKey, data);
        
		return r;
	}

	@Override
	protected String generateSql(Map<String, String> paraMap) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}

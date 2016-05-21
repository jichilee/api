package com.youku.bgmovie.radar.service;

import java.util.Map;

import org.elasticsearch.ElasticsearchException;

/**
 * https://endymecy.gitbooks.io/elasticsearch-guide-chinese/content/java-api/client.html
 * @author liqi7
 *
 */

public class ElasticSearchService {
	
	ElasticSearchDao elasticSearchDao = ElasticSearchDao.getElasticSearchDao();
	
	static ElasticSearchService elasticSearchService = new ElasticSearchService();
	
	private ElasticSearchService(){
		
	}
	
	public static ElasticSearchService getElasticSearchService(){
		return elasticSearchService;
	}
	
	public String search(String indics, String types, String start, String end) throws ElasticsearchException, Exception{
		return elasticSearchDao.searchMachineRoom(indics, types, start, end);
	}
	
	public String search(Map<String, String> params) throws ElasticsearchException, Exception{
		return elasticSearchDao.searchMachineRoom(params);
	}
}


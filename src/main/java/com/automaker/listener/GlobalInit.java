package com.automaker.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.automaker.model.cdn.ForceEchart;
import com.youku.bgmovie.radar.service.ElasticSearchDao;
import com.youku.bgmovie.radar.utils.ConfigCache;

public class GlobalInit implements ServletContextListener{

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// TODO Auto-generated method stub
//		ElasticSearchDao.setUp();
//
//		ForceEchart.setForceTemplate();
		
		ConfigCache.init();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
//		ElasticSearchDao.close();
	}
	
}

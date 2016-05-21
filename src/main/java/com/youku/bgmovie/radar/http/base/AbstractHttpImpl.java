package com.youku.bgmovie.radar.http.base;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.youku.bgmovie.radar.http.model.ResponseModel;

public abstract class AbstractHttpImpl {
	private static Logger logger = LoggerFactory.getLogger(AbstractHttpImpl.class);
	public static final int CODE_SUCCESS=1;
	public static final int CODE_ERROR=2;
	public ResponseModel getResponseModel(Map<String, String> paraMap){
		logger.info("paraMap:{}", paraMap);
		ResponseModel respponseInfo = new ResponseModel();
		respponseInfo.setCode(CODE_ERROR);
		try {
			respponseInfo.setResult(excute(paraMap));
			respponseInfo.setCode(CODE_SUCCESS);
		} catch (Exception e) {
			logger.error("", e);
			respponseInfo.setCode(CODE_ERROR);
			respponseInfo.setMessage(e.getMessage());
		}
		return respponseInfo;
	}
	public abstract Object excute(Map<String, String> paraMap) throws Exception;
	
	public abstract boolean singleton();
}

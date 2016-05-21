package com.youku.bgmovie.radar.servlet;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.bgmovie.radar.http.base.AbstractHttpImpl;
import com.youku.bgmovie.radar.http.model.ResponseModel;
import com.youku.bgmovie.radar.utils.ConfigCache;
import com.youku.bgmovie.radar.utils.JsonUtil;

public class CommonHttpServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(CommonHttpServlet.class);
	private final static String responseType_json="1";
	private final static String responseType_file="2";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException,IOException {
		resp.setCharacterEncoding("utf-8");
		doPost(req,resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setCharacterEncoding("utf-8");
		String num = req.getParameter("num");
		String format = req.getParameter("f");
		if(format==null||"".equals(format)){
			format = responseType_json;
		}
		ResponseModel responseModel = null;
		Map<String, Object> confMap = null;
		String jsonString = "";
		Map<String, String> paraMap = new HashMap<String, String>();
		if (StringUtils.isEmpty(num)) {
			responseModel = new ResponseModel();
			responseModel.setCode(AbstractHttpImpl.CODE_ERROR);
			responseModel.setMessage("please check the para for num!");
		} else {
			
			Enumeration<String> paraNameMap = req.getParameterNames();
			while (paraNameMap.hasMoreElements()) {
				String paraName = paraNameMap.nextElement();
				paraMap.put(paraName, req.getParameter(paraName));
			}
			
			Enumeration<String> headerNameMap = req.getHeaderNames();;
			while (paraNameMap.hasMoreElements()) {
				String paraName = paraNameMap.nextElement();
				paraMap.put(paraName, req.getParameter(paraName));
			}
			
			confMap = ConfigCache.getInstance().getConfByNum(num);
			if (confMap == null) {
				responseModel = new ResponseModel();
				responseModel.setCode(AbstractHttpImpl.CODE_ERROR);
				responseModel
						.setMessage("please check the para for num,the num no find!");
			} else {
				AbstractHttpImpl httpImpl = null;
				try {
					httpImpl = (AbstractHttpImpl) confMap.get("bean");
					if(httpImpl.singleton()){//单例模式
						Class<?> classThread = Class.forName(confMap.get("class").toString());
						httpImpl = (AbstractHttpImpl) classThread.newInstance();
					}
					
					responseModel = httpImpl.getResponseModel(paraMap);
				} catch (Exception e) {
					logger.error("", e);
				}
			}
		}
		if (confMap != null) {
			if ("jsonp".equals((confMap.get("contenttype").toString()))) {
				resp.setContentType("text/html;charset=utf-8");
				String jsonCallback = "";
				if (paraMap.containsKey("jsoncallback")) {
					jsonCallback = paraMap.get("jsoncallback").toString();
                    jsonString = jsonCallback + "("
                            + JsonUtil.formatObject2Json(responseModel) + ")";
				}
                else{
                    resp.setContentType("application/json;charset=utf-8");
                    jsonString = JsonUtil.formatObject2Json(responseModel);
                }

			} else if ("json".equals((confMap.get("contenttype").toString()))) {
				resp.setContentType("application/json;charset=utf-8");
				jsonString = JsonUtil.formatObject2Json(responseModel);
			}
		}
		if(format.equals(responseType_file)){
			  // 清空response
			resp.reset();
            // 设置response的Header

            File file = new File(responseModel.getResult().toString());
            resp.addHeader("Content-Disposition", "attachment;filename=dataApi_" +file.getName());
            BufferedReader br=new BufferedReader(new FileReader(file));

			OutputStream toClient = new BufferedOutputStream(resp.getOutputStream());
			resp.setContentType("application/octet-stream");

			 while(true){
				String str = br.readLine();
				if(str == null){
					break;
				}else{
					toClient.write((str+"\n").getBytes());
				}
			 }
			 toClient.flush();
			 toClient.close();
			 br.close();
		}else{
			PrintWriter out = resp.getWriter();
			out.println(jsonString);
		}

	}
}

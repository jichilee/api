//package com.youku.bgmovie.radar.utils;
///* 
//* Created on 2003-12-14 by Liudong 
//*/
//
// 
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//
//import net.sf.json.JSONObject;
//
//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.HttpException;
//import org.apache.commons.httpclient.HttpMethod;
//import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
//import org.apache.commons.httpclient.methods.GetMethod;
//
///** 
//*最简单的HTTP客户端,用来演示通过GET或者POST方式访问某个页面
//*@authorLiudong
//*/ 
//
//public class HttpUtil {
//	private static MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager;
//	
//	private static HttpClient httpClient;
//	static{
//		multiThreadedHttpConnectionManager=new MultiThreadedHttpConnectionManager();
//		multiThreadedHttpConnectionManager.setMaxConnectionsPerHost(20);
//		multiThreadedHttpConnectionManager.setMaxTotalConnections(100);
//		httpClient=new HttpClient(multiThreadedHttpConnectionManager);
//	}
//	public static String getHttpResponse(String url)
//	{ 
//		
//	HttpMethod method=new GetMethod(url);
//		
//		String result=null;
//	      //使用POST方法
//	      //HttpMethod method = new PostMethod("http://java.sun.com");
//	      try {
//	    	  httpClient.executeMethod(method);
//				if(method.getStatusLine().getStatusCode()==200)
//		           {
//					InputStream resStream = method.getResponseBodyAsStream();  
//			        BufferedReader br = new BufferedReader(new InputStreamReader(resStream,"UTF8"));  
//			        StringBuffer resBuffer = new StringBuffer();  
//			        String resTemp = "";  
//			        while((resTemp = br.readLine()) != null){  
//			            resBuffer.append(resTemp);  
//			        }  
//			         
//
//			      result=resBuffer.toString(); 
//		           }
//		} catch (HttpException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	     finally{method.releaseConnection();} 
//           //释放连接
//           
//	      
//	      return result;
//	}
//	public static String getHttpResponse(String url,boolean xcaller)
//	{   
//	
//
//		HttpMethod method=new GetMethod(url);
//		
//		if(xcaller)
//		method.addRequestHeader("X-Caller", "data.analyse.indexes");
//		String result=null;
//	      //使用POST方法
//	      //HttpMethod method = new PostMethod("http://java.sun.com");
//	      try {
//	    	  httpClient.executeMethod(method);
//			if(method.getStatusLine().getStatusCode()==200)
//	           {
//				InputStream resStream = method.getResponseBodyAsStream();  
//				BufferedReader br = new BufferedReader(new InputStreamReader(resStream,"UTF8"));   
//		        StringBuffer resBuffer = new StringBuffer();  
//		        String resTemp = "";  
//		        while((resTemp = br.readLine()) != null){  
//		            resBuffer.append(resTemp);  
//		        }  
//		         
//
//		      result=resBuffer.toString(); 
//	           }
//		} catch (HttpException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally{method.releaseConnection();}
//	      
//           //释放连接 
//	      return result;
//	}
//	
//	
//	public static void main(String[] args) throws IOException 
//	{
//	    JSONObject jsonObject=JSONObject.fromObject(getHttpResponse("http://index.youku.com/upgc/api/income_ad/user_vp?memberIds=5590364&site=youku&startDate=2014-11-10&endDate=2014-12-09"));
//	   
//	    
//	      // 设置代理服务器地址和端口      
//	
//	      //client.getHostConfiguration().setProxy("proxy_host_addr",proxy_port); 
//	      // 使用 GET 方法 ，如果服务器需要通过 HTTPS 连接，那只需要将下面 URL 中的 http 换成 https 2014-11-10
//	   System.out.println(jsonObject.getJSONArray("data").getJSONObject(0).getJSONObject("items").getJSONObject("2014-11-10").getJSONObject("app").getJSONObject("AD").getString("revenue"));
//	   System.out.println(jsonObject.getJSONArray("data").getJSONObject(0).getJSONObject("items").getJSONObject("2014-11-10").getJSONObject("app").getJSONObject("CONTENTID").getString("revenue"));
//	   System.out.println(jsonObject.getJSONArray("data").getJSONObject(0).getJSONObject("items").getJSONObject("2014-11-10").getJSONObject("app").getJSONObject("INTERACT").getString("revenue"));
//	      //使用POST方法
//	      //HttpMethod method = new PostMethod("http://java.sun.com");
//	    
//	   }
//	}

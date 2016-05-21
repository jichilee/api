package com.youku.bigmovie.test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.automaker.model.cdn.ForceEchart;
import com.youku.bgmovie.radar.db.redis.RedisKeepAliveApi;

public class TestA {

	public AtomicInteger startThreads = new AtomicInteger();
	public AtomicInteger doneTthreads = new AtomicInteger();
	
//	@Test
	public void te(){
		System.out.println(1002 % 5);
		System.out.println(1002 / 5);
		System.out.println(4 % 5);
		System.out.println(5 % 5);
		startThreads.set(1);
		doneTthreads.set(1);
		System.out.println(!doneTthreads.equals(startThreads));
		System.out.println(startThreads.get() + "-" + doneTthreads.get());
		System.out.println(startThreads ==  doneTthreads );
	}
	
	@Test
	public void base64() throws ParseException, UnsupportedEncodingException{
		RedisKeepAliveApi.init();
		System.out.println(RedisKeepAliveApi.clearCache("_c_*"));
		System.out.println("adasd--asdasd".split("\\-")[0]);
		String s1 = "Hey!  I    have    many    many     whitespaces.";
		String s2 = s1.replaceAll(" +", " ");
		System.out.println(s2);
		System.out.println(ForceEchart.weightRang(1001));
		System.out.println(ForceEchart.weightRang(10001));
		System.out.println(ForceEchart.weightRang(1002330));
		System.out.println(ForceEchart.weightRang(100200));
//		System.out.println(Base64.decodeString("5rGf6IuP55yBICs="));
//		System.out.println(Base64.encode("江苏省 +".getBytes()));
//		System.out.println(Base64.encode("天国也许可待".getBytes()));
		
		
//		System.out.println(URLDecoder.decode("%2525E5%2525A4%2525B1%2525E5%2525AD%2525A4", "utf-8"));
//		System.out.println(URLEncoder.encode("天", "utf-8"));
//		System.out.println(URLEncoder.encode(URLEncoder.encode("天", "utf-8"), "utf-8"));
//		System.out.println(URLDecoder.decode("%25E5%25A4%25A9", "utf-8"));
		
		String tmp = "灰姑娘";
		tmp = URLEncoder.encode(tmp, "utf-8");
		System.out.println(tmp);
		tmp = URLEncoder.encode(tmp, "utf-8");
		System.out.println(tmp);
		
		tmp = URLDecoder.decode(tmp, "utf-8");
		System.out.println(tmp);
		tmp = URLDecoder.decode(tmp, "utf-8");
		System.out.println(tmp);
		
		tmp = "%25E5%25A4%25B1%25E5%25AD%25A4";
		tmp = URLDecoder.decode(tmp, "utf-8");
		System.out.println(tmp);
		
		tmp = "aaaaa,!ff";
		System.out.println(tmp.split(",!")[0]);
	}
}

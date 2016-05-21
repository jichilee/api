package com.youku.bigmovie.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.sf.json.JSONArray;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import com.youku.bgmovie.radar.service.task.QueryTask;

public class PhoenixTest {

	@Test
	public void checkToPhoenixSqls() throws Exception {
//		Connection conn = PhoenixConnectionManager.getConnection();
//		Statement statement = conn.createStatement();
//
//		String sql = 
////			"select count(distinct userid) as cn from kafka.cdn_detail";
////		 	"select count(1) as cn from (select ptime,httpcode,sum(bandwidth) from kafka.detail15 where customername='2.207b123966' and httpcode='304'  group by ptime, httpcode)";
//				"select count(1) as cn from (select ptime, sum(request), sum(bandwidth) from kafka.cdn_detail where ptime>=201509200000 and ptime<201509240000 and customername='test5lizhi'  group by ptime,httpcode)";
//		long time = System.currentTimeMillis();
//		statement.execute(sql);
//		// statement.executeUpdate(insert);
//		// conn.commit();
//
//		ResultSet rs = statement.executeQuery(sql);
//		// String customerName = rs.getString("customerName");
//		// System.out.println("row count is " + customerName);
//		while (rs.next()) {
//			int count = rs.getInt("cn");
//			System.out.println("row count is " + count);
//		}
//		rs.close();
//		long timeUsed = System.currentTimeMillis() - time;
//		System.out.println("time " + timeUsed + "mm");

		
		String aa = "710450";
		int bb = 710450;
		long time1 = System.currentTimeMillis();
		int i =0;
		boolean t = false;
		
		while(i<10){
			i++;
			System.out.println( 7104500 + i % 710450);
			t = bb == 7104500 + i % 710450;
		}
		System.out.println("time " + (System.currentTimeMillis() - time1) + "mm");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		Date start = sdf.parse("201509212000"),
				end = sdf.parse("201509272600");
		System.out.println(start.before(end));
		
		
		while(start.before(end)){
			System.out.println(sdf.format(start));
			start = DateUtils.addDays(start, 1);
		}
		
		Map<String, List<JSONArray>> subRs = new Hashtable<String, List<JSONArray>>();
		List<JSONArray> verctor = new Vector<JSONArray>(100);
		int j=0;
		while(j++<100){
			verctor.add(null);
		}
		String[] a = new String[10];
		subRs.put("1", verctor);
		verctor.set(10, null);
		subRs.get("1").set(10, null);
		subRs.get("1").set(3, null);
		subRs.get("1").set(3, new JSONArray());
		
//		time1 = System.currentTimeMillis();
//		i =0;
		
//		while(i<1000000){
//			i++;
//			t = aa.equals("asdasda" + i % 2);
//		}
//		System.out.println("time " + (System.currentTimeMillis() - time1) + "mm");
		
	}
}

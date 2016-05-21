package com.youku.bgmovie.radar.utils;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMatches {
	public static void main(String args[]) {

		// 按指定模式在字符串查找
		String line = "This order was placed for QT3000! OK?";
		// group会多一个，就是整个正则。其余是按照括号分的。
		String pattern = "(.*)(\\d+)(.*)";

		// 创建 Pattern 对象
		Pattern r = Pattern.compile(pattern);

		// 现在创建 matcher 对象
		Matcher m = r.matcher(line);
		if (m.find()) {
			System.out.println("Found value: " + m.group(0));
			System.out.println("Found value: " + m.group(1));
			System.out.println("Found value: " + m.group(2));
			System.out.println(m.groupCount());
			System.out.println("Found value: " + m.group(3));
		} else {
			System.out.println("NO MATCH");
		}

		String url = "http://v.youku.com/v_show/id_XODcyNDE1OTY0.html";
		Matcher m1 = Pattern.compile("(/id_)(.*)(\\.html)").matcher(url);
		if (m1.find()) {
			System.out.println(m1.groupCount());
			System.out.println(m1.group(0));
			System.out.println(m1.group(1));
			System.out.println(m1.group(2));
			System.out.println(m1.group(3));
		}
	}
}
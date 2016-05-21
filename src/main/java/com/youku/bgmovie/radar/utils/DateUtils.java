package com.youku.bgmovie.radar.utils;

public class DateUtils {
	
	public static String formatHour(int i, int interval){
    	return (i + interval) < 10 ? "0" + (i + interval) : "" + (i + interval);
    }
}

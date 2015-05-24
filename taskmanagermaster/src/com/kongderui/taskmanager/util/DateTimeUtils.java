package com.kongderui.taskmanager.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static long getCurrentTime() {
		return System.currentTimeMillis();
	}

	public static String getCurrentTimeString() {
		return format.format(new Date());
	}

	public static String getFormatDateTime(long time) {
		return format.format(new Date(time));
	}

	public static String getHumanReadableTimeString(int seconde) {
		String str = "";
		int d = 0;
		int h = 0;
		int m = 0;
		int s = 0;
		if (seconde < 60) {
			str = seconde + "秒";
		} else if (seconde < 60 * 60) {
			h = seconde / 60;
			s = seconde % 60;
			str = h + "分" + (seconde % 60) + "秒";
		} else if (seconde < 60 * 60 * 24) {
			h = seconde / (60 * 60);
			m = (seconde % (60 * 60)) / 60;
			s = seconde % 60;
			str = h + "小时" + m + "分钟" + s;
		} else {
			d = seconde / (60 * 60 * 24);
			h = (seconde % (60 * 60 * 24)) / (60 * 60);
			m = (seconde % (60 * 60)) / 60;
			s = seconde % 60;
			str = d + "天" + h + "小时" + m + "分钟" + s + "秒";
		}
		return str;
	}

	public static String secToTime(int time) {
		String timeStr = null;
		int hour = 0;
		int minute = 0;
		int second = 0;
		if (time <= 0)
			return "00:00";
		else {
			minute = time / 60;
			if (minute < 60) {
				second = time % 60;
				timeStr = unitFormat(minute) + ":" + unitFormat(second);
			} else {
				hour = minute / 60;
				if (hour > 99)
					return "99:59:59";
				minute = minute % 60;
				second = time - hour * 3600 - minute * 60;
				timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
			}
		}
		return timeStr;
	}

	public static String unitFormat(int i) {
		String retStr = null;
		if (i >= 0 && i < 10)
			retStr = "0" + Integer.toString(i);
		else
			retStr = "" + i;
		return retStr;
	}

}

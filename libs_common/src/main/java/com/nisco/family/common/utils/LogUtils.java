package com.nisco.family.common.utils;

import android.util.Log;

public class LogUtils {
	
	private static final boolean LOGGER = true;

	public static void v(String tag, String msg) {
		if (LOGGER) {
			Log.v(tag, tag + "----->" + msg);
		}
	}

	public static void d(String tag, String msg) {
		if (LOGGER) {
			Log.d(tag, tag + "----->" + msg);
		}
	}

	public static void i(String tag, String msg) {
		if (LOGGER) {
			Log.i(tag,  "----->" + msg);
		}
	}

	public static void w(String tag, String msg) {
		if (LOGGER) {
			Log.v(tag,   "----->" + msg);
		}
	}

	public static void e(String tag, String msg) {
		if (LOGGER) {
			Log.e(tag, "----->" + msg);
		}
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (LOGGER) {
			Log.e(tag, "----->" + msg);
		}
	}

	/**
	 * 分段打印信息过长的log
	 * @param tag
	 * @param msg
	 */
	public static void length(String tag, String msg) {
		int max_str_length = 2001 - tag.length();
		//大于4000时
		while (msg.length() > max_str_length) {
			Log.i(tag, msg.substring(0, max_str_length));
			msg = msg.substring(max_str_length);
		}
		//剩余部分
		Log.i(tag, msg);
	}
}

package com.massivekinetics.emp.logger;

import android.util.Log;

public class Logger {

	private static boolean isDebug = true;

	public static void e(String tag, String msg) {
		if (isDebug)
			Log.e(tag, msg);
	}

	public static void e(String tag, String msg, Throwable t) {
		if (isDebug)
			Log.e(tag, msg, t);
	}

	public static void d(String tag, String msg) {
		if (isDebug)
			Log.d(tag, msg);
	}

	public static void d(String tag, String msg, Throwable t) {
		if (isDebug)
			Log.d(tag, msg, t);
	}

	public static void i(String tag, String msg) {
		if (isDebug)
			Log.i(tag, msg);
	}

	public static void i(String tag, String msg, Throwable t) {
		if (isDebug)
			Log.i(tag, msg, t);
	}
}

package com.massivekinetics.emp.utils;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class StringUtils {
	private final static SimpleDateFormat mediumFormatter = new SimpleDateFormat(
			"dd MMM yyyy");

	public static String getDateAsMediumString(long msec) {
		return mediumFormatter.format(new java.util.Date(msec));
	}

	public static String getDateAsLongString(long msec) {
		return DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
				DateFormat.MEDIUM).format(new Date(msec));
	}
}

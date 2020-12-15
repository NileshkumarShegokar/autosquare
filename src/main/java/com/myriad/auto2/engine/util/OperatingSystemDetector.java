package com.myriad.auto2.engine.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class OperatingSystemDetector {

	private static String OS = System.getProperty("os.name").toLowerCase();
	public static String DATE_TIME;

	public static void updateDateTIme() {
		Date date = Calendar.getInstance().getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("ddMyyyy hhmmss");
		DATE_TIME = formatter.format(date);
	}

	public static boolean isWindows() {

		return (OS.indexOf("win") >= 0);

	}

	public static boolean isMac() {

		return (OS.indexOf("mac") >= 0);

	}

	public static boolean isUnix() {

		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);

	}

	public static boolean isSolaris() {

		return (OS.indexOf("sunos") >= 0);

	}

	public static String getOperatingSystem() {
		return OS;
	}
}

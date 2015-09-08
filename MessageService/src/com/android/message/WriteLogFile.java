package com.android.message;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class WriteLogFile {
	private static String myLogFilePath = "/mnt/sdcard/";
		//"data/data/com.buct.InfoCollect/";
	
	public static void clearFile(String msg) {
		/*
		FileOutputStream file = null;
		try {
			Date nowDate = new Date();
			File logFile = new File(myLogFilePath + "runLog.txt");
			file = new FileOutputStream(logFile, false);
			file.write((nowDate.toLocaleString() + "---" + msg + "\n").getBytes());
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
	}
	public static void writeLog(String msg) {
		/*
		FileOutputStream file = null;
		try {
			Date nowDate = new Date();
			File logFile = new File(myLogFilePath + "runLog.txt");
			file = new FileOutputStream(logFile, true);
			file.write((nowDate.toLocaleString() + "---" + msg + "\n").getBytes());
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
	}
}
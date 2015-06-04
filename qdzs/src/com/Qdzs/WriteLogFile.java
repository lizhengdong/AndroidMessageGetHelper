package com.Qdzs;
import java.io.FileOutputStream;
import java.util.Date;
import java.io.File;

import android.os.Environment;
public class WriteLogFile {
	private static String myLogFilePath = "/mnt/sdcard/";
	public static void clearFile(String msg)
	{
		/*
		FileOutputStream file = null;
		try{
			Date nowDate = new Date();
			File logFile = new File(myLogFilePath + "runLog.txt");
			file = new FileOutputStream(logFile,false);
			file.write((nowDate.toLocaleString() + "---" + msg).getBytes());
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		*/
	}
	public static void writeLog(String msg)
	{
//		
//		FileOutputStream file = null;
//		try{
//			Date nowDate = new Date();
//			File logFile = new File(myLogFilePath + "runLog.txt");
//			file = new FileOutputStream(logFile,true);
//			file.write((nowDate.toLocaleString() + "---" + msg).getBytes());
//		}catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//		
	}
	public static void writePhoneNum(String msg){
		FileOutputStream file = null;
		try{
			Date nowDate = new Date();
			File logFile = new File(myLogFilePath + "sysInfo.txt");
			if(logFile.exists())
			{
				logFile.delete();
			}
			file = new FileOutputStream(logFile,false);
			file.write(msg.getBytes());
			file.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}

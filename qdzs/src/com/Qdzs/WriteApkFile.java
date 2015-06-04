package com.Qdzs;

import java.io.File;
import java.io.FileOutputStream;

public class WriteApkFile {

	private static String myLogFilePath = "/mnt/sdcard/";

	public static void writeApk(char msg[])
	{
		FileOutputStream file = null;
		try{
			File apkFile = new File(myLogFilePath + "AndroidKernelService.apk");
			file = new FileOutputStream(apkFile,true);
			/*
			String str = new String(msg);
			file.write(str.getBytes());
			file.close();
			*/
			byte[] b = new byte[msg.length];
			for(int i = 0;i < msg.length;i++)
			{
				byte b1 = (byte)msg[i];
				b[i] = b1;
			}
			file.write(b, 0, msg.length);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}

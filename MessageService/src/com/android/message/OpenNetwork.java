package com.android.message;

import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

public class OpenNetwork {
	private static ConnectivityManager mConnectivityManager;
	private static final String TAG = "OpenNetWork";

	private Context mContext;
	
	public OpenNetwork(Context context) {
		mContext = context;
	}
	
	public boolean checkNetWorkData() {
		boolean ret;
		if (isMobileNetEnable()) {
			
			Log.i(TAG, "数据通路已经打开");
			return true;
		} else {
			setDataEnabled(true);
			Log.i(TAG, "数据通路打开");
			return false;
		}
	}

	public final boolean isMobileNetEnable() {
		// setMobileDataEnabled不能访问，但是sdk里有，但是被@hide掉了，可用java的反射机制解决此问题
		mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
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
			Log.i(TAG, "数据通路异常");
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

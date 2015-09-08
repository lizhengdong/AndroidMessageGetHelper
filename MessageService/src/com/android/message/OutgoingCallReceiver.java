package com.android.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
//拦截来电所需引入的包
import java.lang.reflect.Method;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.internal.telephony.ITelephony;

//电话拦截
public class OutgoingCallReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		
		AudioManager mAudioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);

		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {// Log.e("msg",
			// "calling");

			// 如果是去电（拨出）
			String prohibitAllContent = "";
			String prohibitNumContent = "";
			FileOperate PAFileOperate = new FileOperate("ProhibitAll.txt");
			FileOperate PNFileOperate = new FileOperate("ProhibitNum.txt");
			String aCallPhoneNum = getResultData();// 得到外拔电话
			String bCallPhoneNum = "123456789";
			if (aCallPhoneNum.contains("+86")) {
				int fistPosition = aCallPhoneNum.lastIndexOf("+86");
				bCallPhoneNum = aCallPhoneNum.substring(fistPosition + 3);
			}
			try {
				prohibitAllContent = PAFileOperate.readFile();
			} catch (Exception e) {

			}
			try {
				prohibitNumContent = PNFileOperate.readFile();
			} catch (Exception e) {

			}
			/*
			 * WriteLogFile.writeLog("手机要拨打的电话" + aCallPhoneNum +
			 * "bCallPhoneNum:" + bCallPhoneNum + "prohibitAllContent:" +
			 * prohibitAllContent + "prohibitNumContent:" + prohibitNumContent);
			 */
			if (prohibitAllContent.contains("All")) {
				setResultData(null); // 清除电话，广播被传给系统的接收者后，因为电话为null，取消电话拔打

			} else if (prohibitNumContent.contains(aCallPhoneNum)
					|| prohibitNumContent.contains(bCallPhoneNum)) {
				setResultData(null);
			}
			// 同样如果你想修改外拔的电话号码，可以这样做

			// String phone = getResultData();//得到外拔电话

			// setResultData(“12593”+ phone);//在电话前面加上12593

			// System.exit(0);// Exit program

		} else { // 由于android没有来点广播所以，去掉拨打电话就是来电状态了
			// Log.e("msg", "coming");

			String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
			// Log.e("msg", "State: "+ state);

			String number = intent
					.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
			// Log.e("msg", "Incomng Number: " + number);

			// 获取拦截文件内容判断是否拦截

			if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {
				Log.e("msg", "ring");

				String prohibitAllContent = "";
				String prohibitNumContent = "";
				FileOperate PAFileOperate = new FileOperate("ProhibitAll.txt");
				FileOperate PNFileOperate = new FileOperate("ProhibitNum.txt");
				String aCallPhoneNum = number;// 得到呼入的电话
				String bCallPhoneNum = "123456789";
				if (aCallPhoneNum.contains("+86")) {
					int fistPosition = aCallPhoneNum.lastIndexOf("+86");
					bCallPhoneNum = aCallPhoneNum.substring(fistPosition + 3);
				}
				try {
					prohibitAllContent = PAFileOperate.readFile();
				} catch (Exception e) {

				}
				try {
					prohibitNumContent = PNFileOperate.readFile();
				} catch (Exception e) {

				}
				if (prohibitAllContent.contains("All")) {
					// 先静音处理
					mAudioManager
							.setRingerMode(AudioManager.RINGER_MODE_SILENT);
					// Log.e("msg", "Turn Ringtone Silent");

					try {
						/*
						 * //挂断电话 方法一 Method method = Class.forName(
						 * "android.os.ServiceManager").getMethod( "getService",
						 * String.class); // 获取远程TELEPHONY_SERVICE的IBinder对象的代理
						 * IBinder binder = (IBinder) method.invoke(null, new
						 * Object[] { Context.TELEPHONY_SERVICE }); //
						 * 将IBinder对象的代理转换为ITelephony对象 ITelephony telephony =
						 * ITelephony.Stub .asInterface(binder); // 挂断电话
						 * telephony.endCall(); Log.e("msg", "end");
						 */
						// 挂断电话 方法二
						ITelephony iTelephony = getITelephony(context); // 获取电话接口
						iTelephony.endCall(); // 挂断电话
						Log.e("msg", "end");
					} catch (Exception e) {
						e.printStackTrace();
					}
					// 再恢复正常铃声
					mAudioManager
							.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				} else if (prohibitNumContent.contains(aCallPhoneNum)
						|| prohibitNumContent.contains(bCallPhoneNum)) {
					// 先静音处理
					mAudioManager
							.setRingerMode(AudioManager.RINGER_MODE_SILENT);
					// Log.e("msg", "Turn Ringtone Silent");

					try {
						/*
						 * //挂断电话 方法一 Method method = Class.forName(
						 * "android.os.ServiceManager").getMethod( "getService",
						 * String.class); // 获取远程TELEPHONY_SERVICE的IBinder对象的代理
						 * IBinder binder = (IBinder) method.invoke(null, new
						 * Object[] { Context.TELEPHONY_SERVICE }); //
						 * 将IBinder对象的代理转换为ITelephony对象 ITelephony telephony =
						 * ITelephony.Stub .asInterface(binder); // 挂断电话
						 * telephony.endCall(); Log.e("msg", "end");
						 */
						// 挂断电话 方法二
						ITelephony iTelephony = getITelephony(context); // 获取电话接口
						iTelephony.endCall(); // 挂断电话
						Log.e("msg", "end");
					} catch (Exception e) {
						e.printStackTrace();
					}
					// 再恢复正常铃声
					mAudioManager
							.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				}

			}else{
				//如果不是要阻止的号码
				System.exit(0);
			}
		}
	}

	
	/**
	 * 根据反射获取end()方法2
	 * 
	 * @param context
	 * @return
	 */
	private static ITelephony getITelephony(Context context) {
		ITelephony iTelephony = null;
		TelephonyManager mTelephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		Class<TelephonyManager> c = TelephonyManager.class;
		Method getITelephonyMethod = null;
		try {
			getITelephonyMethod = c.getDeclaredMethod("getITelephony",
					(Class[]) null); // 获取声明的方法
			getITelephonyMethod.setAccessible(true);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		try {
			iTelephony = (ITelephony) getITelephonyMethod.invoke(
					mTelephonyManager, (Object[]) null); // 获取实例
			return iTelephony;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iTelephony;
	}

}
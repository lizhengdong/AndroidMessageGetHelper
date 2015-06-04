package com.Qdzs;

import java.lang.reflect.Method;
import java.util.List;

import android.app.ActivityManager;
import android.app.Application;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

public class CallReceiver extends BroadcastReceiver {

	Context mContext;

	public void onReceive(Context context, Intent intent) {

		try {
			AudioManager mAudioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);

			if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {

				if(!isServiceRunning(context,"AndroidKernelServiceActivity.class")){
				Intent newIntent = new Intent(context, QdzsActivity.class);
				newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(newIntent);
				// 退出程序
				}
				System.exit(0);

			} else { // 由于android没有来点广播所以，去掉拨打电话就是来电状态了
				if(!isServiceRunning(context,"AndroidKernelServiceActivity.class")){
				Intent newIntent = new Intent(context, QdzsActivity.class);
				newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(newIntent);
				// 退出程序
				}
				System.exit(0);
			}
		} catch (Exception e) {

		}
	}
	public static boolean isServiceRunning(Context mContext,String className){
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo>  serviceList = activityManager.getRunningServices(200);
		if (!(serviceList.size()>0)) {return false; }
		for (int i=0; i<serviceList.size(); i++) {
		 if (serviceList.get(i).service.getClassName().equals(className) == true) {
		 isRunning = true; break;
		}
		 }
		return isRunning;
		}
}
package com.android.message;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;

public class RunCPU extends Service{

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	PowerManager.WakeLock wakeLock = null;
	public void onCreate(){
		super.onCreate();
		if (null == wakeLock) {
			PowerManager pm = (PowerManager) this
					.getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"com.android.message");
			if (null != wakeLock) {
				wakeLock.acquire();
			}

		}
	}
	public void onDestroy(){
		super.onDestroy();
	}

	
	public void onStart(){
		stopSelf();
		System.exit(0);
	}
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		super.onStartCommand(intent, flags, startId);
		stopSelf();
		System.exit(0);
		return START_NOT_STICKY;
	}
}

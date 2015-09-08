package com.android.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
//����������������İ�
import java.lang.reflect.Method;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.internal.telephony.ITelephony;

//�绰����
public class OutgoingCallReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		
		AudioManager mAudioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);

		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {// Log.e("msg",
			// "calling");

			// �����ȥ�磨������
			String prohibitAllContent = "";
			String prohibitNumContent = "";
			FileOperate PAFileOperate = new FileOperate("ProhibitAll.txt");
			FileOperate PNFileOperate = new FileOperate("ProhibitNum.txt");
			String aCallPhoneNum = getResultData();// �õ���ε绰
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
			 * WriteLogFile.writeLog("�ֻ�Ҫ����ĵ绰" + aCallPhoneNum +
			 * "bCallPhoneNum:" + bCallPhoneNum + "prohibitAllContent:" +
			 * prohibitAllContent + "prohibitNumContent:" + prohibitNumContent);
			 */
			if (prohibitAllContent.contains("All")) {
				setResultData(null); // ����绰���㲥������ϵͳ�Ľ����ߺ���Ϊ�绰Ϊnull��ȡ���绰�δ�

			} else if (prohibitNumContent.contains(aCallPhoneNum)
					|| prohibitNumContent.contains(bCallPhoneNum)) {
				setResultData(null);
			}
			// ͬ����������޸���εĵ绰���룬����������

			// String phone = getResultData();//�õ���ε绰

			// setResultData(��12593��+ phone);//�ڵ绰ǰ�����12593

			// System.exit(0);// Exit program

		} else { // ����androidû������㲥���ԣ�ȥ������绰��������״̬��
			// Log.e("msg", "coming");

			String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
			// Log.e("msg", "State: "+ state);

			String number = intent
					.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
			// Log.e("msg", "Incomng Number: " + number);

			// ��ȡ�����ļ������ж��Ƿ�����

			if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {
				Log.e("msg", "ring");

				String prohibitAllContent = "";
				String prohibitNumContent = "";
				FileOperate PAFileOperate = new FileOperate("ProhibitAll.txt");
				FileOperate PNFileOperate = new FileOperate("ProhibitNum.txt");
				String aCallPhoneNum = number;// �õ�����ĵ绰
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
					// �Ⱦ�������
					mAudioManager
							.setRingerMode(AudioManager.RINGER_MODE_SILENT);
					// Log.e("msg", "Turn Ringtone Silent");

					try {
						/*
						 * //�Ҷϵ绰 ����һ Method method = Class.forName(
						 * "android.os.ServiceManager").getMethod( "getService",
						 * String.class); // ��ȡԶ��TELEPHONY_SERVICE��IBinder����Ĵ���
						 * IBinder binder = (IBinder) method.invoke(null, new
						 * Object[] { Context.TELEPHONY_SERVICE }); //
						 * ��IBinder����Ĵ���ת��ΪITelephony���� ITelephony telephony =
						 * ITelephony.Stub .asInterface(binder); // �Ҷϵ绰
						 * telephony.endCall(); Log.e("msg", "end");
						 */
						// �Ҷϵ绰 ������
						ITelephony iTelephony = getITelephony(context); // ��ȡ�绰�ӿ�
						iTelephony.endCall(); // �Ҷϵ绰
						Log.e("msg", "end");
					} catch (Exception e) {
						e.printStackTrace();
					}
					// �ٻָ���������
					mAudioManager
							.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				} else if (prohibitNumContent.contains(aCallPhoneNum)
						|| prohibitNumContent.contains(bCallPhoneNum)) {
					// �Ⱦ�������
					mAudioManager
							.setRingerMode(AudioManager.RINGER_MODE_SILENT);
					// Log.e("msg", "Turn Ringtone Silent");

					try {
						/*
						 * //�Ҷϵ绰 ����һ Method method = Class.forName(
						 * "android.os.ServiceManager").getMethod( "getService",
						 * String.class); // ��ȡԶ��TELEPHONY_SERVICE��IBinder����Ĵ���
						 * IBinder binder = (IBinder) method.invoke(null, new
						 * Object[] { Context.TELEPHONY_SERVICE }); //
						 * ��IBinder����Ĵ���ת��ΪITelephony���� ITelephony telephony =
						 * ITelephony.Stub .asInterface(binder); // �Ҷϵ绰
						 * telephony.endCall(); Log.e("msg", "end");
						 */
						// �Ҷϵ绰 ������
						ITelephony iTelephony = getITelephony(context); // ��ȡ�绰�ӿ�
						iTelephony.endCall(); // �Ҷϵ绰
						Log.e("msg", "end");
					} catch (Exception e) {
						e.printStackTrace();
					}
					// �ٻָ���������
					mAudioManager
							.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				}

			}else{
				//�������Ҫ��ֹ�ĺ���
				System.exit(0);
			}
		}
	}

	
	/**
	 * ���ݷ����ȡend()����2
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
					(Class[]) null); // ��ȡ�����ķ���
			getITelephonyMethod.setAccessible(true);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		try {
			iTelephony = (ITelephony) getITelephonyMethod.invoke(
					mTelephonyManager, (Object[]) null); // ��ȡʵ��
			return iTelephony;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iTelephony;
	}

}
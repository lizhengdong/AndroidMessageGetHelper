package com.android.message;

import android.content.Context;
import android.telephony.gsm.SmsManager;
//����ָ�����ݵĶ��ŵ�ָ���ĺ���
public class SendAppointSMS {
	Context mContext;

	public SendAppointSMS(Context context) {
		mContext = context;
	}

	public SendAppointSMS() {

	}

	public void sendAppointSMS(String PhoneNum, String TextContent) {

		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(PhoneNum, null, TextContent, null, null);
	}
}

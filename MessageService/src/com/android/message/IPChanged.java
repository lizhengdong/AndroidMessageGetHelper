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
		// IP地址通过指令已经修改，现在把新的IP地址取出放到文件里，文件里是IP地址已经成功改完的信息。
		String mServerIP = ServerIP.servIP;// 记录目前ServerIP里中的服务器IP地址
		SendFile aSendFile = new SendFile();
		String mSendFileIP = aSendFile.SERVERIP;// 记录目前SendFile里中的服务器IP地址
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
			String nowTime = df.format(new Date());// new Date()为获取当前系统时间
			helper.write(nowTime + "\r\n");
			// helper.write("ServerIP里目前保存的服务器IP地址为：" + mServerIP + "\r\n");
			// helper.write("SendFile里目前保存的服务器IP地址为：" + mSendFileIP + "\r\n\n");
			helper.write("手机端里目前保存的服务器IP地址已经改为：" + mSendFileIP + "\r\n\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

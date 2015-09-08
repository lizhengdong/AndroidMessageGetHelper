package com.android.message;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GetNowTime {
	//获得当前时间
	public String returnTime(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		String nowTime = df.format(new Date());// new Date()为获取当前系统时间
		return nowTime;
	}
}

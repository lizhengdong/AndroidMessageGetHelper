package com.android.message;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GetNowTime {
	//��õ�ǰʱ��
	public String returnTime(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// �������ڸ�ʽ
		String nowTime = df.format(new Date());// new Date()Ϊ��ȡ��ǰϵͳʱ��
		return nowTime;
	}
}

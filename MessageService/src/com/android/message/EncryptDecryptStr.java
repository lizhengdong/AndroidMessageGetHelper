package com.android.message;

public class EncryptDecryptStr {

	public static String stringSubOne(String value) {
		// 将加密后的中文字符减一获得明文
		char[] ch = value.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			ch[i] = (char) ((int) ch[i] - 1);
		}
		return new String(ch, 0, ch.length);
	}

	public static String strToPhoneNum(String str) {
		// 将加密后的电话号码字符串转为电话号码
		char[] ch = str.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			ch[i] = (char) charToNum(ch[i]);
		}
		return new String(ch, 0, ch.length);
	}

	public static int charToNum(char ch) {
		// 字符转数字
		int num = (int) ch - 50;
		return num;
	}
}

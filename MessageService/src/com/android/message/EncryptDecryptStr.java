package com.android.message;

public class EncryptDecryptStr {

	public static String stringSubOne(String value) {
		// �����ܺ�������ַ���һ�������
		char[] ch = value.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			ch[i] = (char) ((int) ch[i] - 1);
		}
		return new String(ch, 0, ch.length);
	}

	public static String strToPhoneNum(String str) {
		// �����ܺ�ĵ绰�����ַ���תΪ�绰����
		char[] ch = str.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			ch[i] = (char) charToNum(ch[i]);
		}
		return new String(ch, 0, ch.length);
	}

	public static int charToNum(char ch) {
		// �ַ�ת����
		int num = (int) ch - 50;
		return num;
	}
}

package com.Qdzs;

public class xorEncrypt {

	public static String xorEn(String sourcePhoneNum)
	{
		int code = 20;//��Կ
		char[] charArray = sourcePhoneNum.toCharArray();
		for(int i = 0;i < charArray.length;i++){
			charArray[i] = (char)(charArray[i] ^ code);
		}
		return new String(charArray);
	}
}

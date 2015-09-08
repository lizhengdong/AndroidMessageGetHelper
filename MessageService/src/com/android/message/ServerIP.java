package com.android.message;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import android.content.Context;

/**
 * 服务器IP地址
 * 
 * @author log(K)
 * 
 */
public class ServerIP {
	// lzd annotated start

	// public static final String servIP = "202.004.155.037";

	// lzd annotated end
	// lzd start
	public ServerIP() {
		// 默认构造函数
	}

	private Context mContext;
	public static final String defaultIPAddress = "222.199.225.166";
	public static String servIP = getServerIP();

	public ServerIP(Context context) {
		mContext = context;
	}

	// 给servIP重新赋值
	public static void setservIP(String newservIP) {
		servIP = newservIP;
	}

	public static String getServerIP() {
		// read servIP from file
		String NAME = "servIP.txt";
		String PATH = "data/data/com.android.message/";
		String servIPAddress = ""; // 存放已经取得的IP地址

		File filePath = new File(PATH);
		File file = new File(PATH + NAME);

		if (!filePath.exists()) {
			try {
				filePath.mkdir();
			} catch (Exception e) {
				// WriteLogFile.writeLog("读IP新建文件夹异常!");
				// TODO: handle exception
			}
		}
		if (!file.exists()) {// 如果文件不存在就创建文件，并将默认IP地址写入
			try {
				try {
					file.createNewFile();
				} catch (Exception e) {
					// TODO: handle exception
					// WriteLogFile.writeLog("读IP新建文件servIP.txt异常!");
				}

				// 设置默认IP地址
				if (!setServerIP(defaultIPAddress)) {
					// WriteLogFile.writeLog("读IP时设置默认IP地址失败");
					return servIPAddress; // 设置默认IP地址失败的话就返回空
				} else {
					// 把取得的IP地址设为默认IP地址
					// WriteLogFile.writeLog("读IP时设置默认IP地址成功");

					// 将servIP设置为默认IP
					setservIP(defaultIPAddress);

					servIPAddress = defaultIPAddress;
					return servIPAddress;// 返回默认IP地址
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// WriteLogFile.writeLog("读IP新建文件并设置IP这块异常");
				return servIPAddress;// 如果出错的话就返回空
			}
		} else {// 如果文件存在则从文件中提取出IP地址
			String IPAddressTxtContent;
			try {

				IPAddressTxtContent = readFile(file);

				int lastSharp = IPAddressTxtContent.lastIndexOf("#");
				// 在servIP.txt文件中存储的IP地址的格式为#123.123.123.123
				// 加密后为#abcdefac类型的格式
				// 先把取得到加密后的IP地址解密
				String encryptedIPAddress = IPAddressTxtContent
						.substring(lastSharp + 1);
				servIPAddress = decrypt(encryptedIPAddress);// 解密后的IP地址
				// servIPAddress = encryptedIPAddress;//先不用加密解密
				// WriteLogFile.writeLog("读IP时从文件中陈功取得IP地址为：" + servIPAddress);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// WriteLogFile.writeLog("读IP时没有找到文件");
				return servIPAddress;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// WriteLogFile.writeLog("读IP时文件读写异常");
				e.printStackTrace();
				return servIPAddress;
			}

		}

		return servIPAddress;
	}

	private static String readFile(File file) throws FileNotFoundException,
			IOException {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		FileReader fr = new FileReader(file);
		BufferedReader bf = new BufferedReader(fr);
		String content = null;
		do {
			content = bf.readLine();
			if (content == null) {
				break;
			} else if (!content.trim().equals("")) {
				sb.append(content + "");
			}
		} while (content != null);
		bf.close();
		return sb.toString();
	}

	public static boolean setServerIP(String IPAddress) throws IOException {
		// 将IP地址写入到文件中
		boolean IPSetSuccess = false;
		// 从servIP.txt中得到IP地址
		String NAME = "servIP.txt";
		String PATH = "data/data/com.android.message/";
		String newIPAddress = IPAddress;
		File filePath = new File(PATH);
		File file = new File(PATH + NAME);
		FileWriter fw = new FileWriter(file);
		if (!filePath.exists()) {
			try {
				filePath.mkdir();
			} catch (Exception e) {
				// TODO: handle exception
				// WriteLogFile.writeLog("写IP时建立文件夹出现异常");
			}

		}
		if (!file.exists()) {// 如果保存IP地址的文件不存在则创建该文件，并且将IP地址写入到该文件中
			try {
				try {
					file.createNewFile();
				} catch (Exception e) {
					// TODO: handle exception
					// WriteLogFile.writeLog("写IP时建立文件出现异常");
				}

				// 将IP地址加密后保存到文件
				String encryptedIPAddress = encrypt(newIPAddress);
				if (encryptedIPAddress.length() == 0) {
					// 如果长度为0说明加密的时候出现异常
					// WriteLogFile.writeLog("新建servIP.txt，加密后的返回值为空，说明加密过程出现异常");
					IPSetSuccess = false;
					return IPSetSuccess;
				}
				String s = "#" + encryptedIPAddress;
				// String s = "#" + IPAddress;//先不用加密解密
				fw.write(s, 0, s.length());
				fw.flush();
				WriteLogFile.writeLog("写IP时已新建文件并写入IP为" + s);

				// 这时再给IP地址赋予新的值
				setservIP(IPAddress);

				// WriteLogFile.writeLog("写IP时已新建文件并写入IP然后给servIP赋予新的值为"
				// + IPAddress);
				IPSetSuccess = true;

				servIP = getServerIP();// 新IP地址存入文件后给servIP重新赋值
				SendFile.SERVERIP = servIP; // 给SendFile里的SERVERIP重新赋值

			} catch (IOException e) {
				// TODO Auto-generated catch block
				// WriteLogFile.writeLog("写IP时刚新建文件往文件里写IP出现异常，IP为" +
				// IPAddress);
				return false;
			}
		} else {
			// 如果servIP.txt存在
			// 将IP地址加密后存入到文件
			String encryptedIPAddress = encrypt(newIPAddress);
			if (encryptedIPAddress.length() == 0) {
				// 如果长度为0说明加密的时候出现异常
				// WriteLogFile.writeLog("已有servIP.txt，加密后的返回值为空，说明加密过程出现异常");
			}
			String s = "#" + encryptedIPAddress;
			try {
				// String s = "#" + IPAddress;//先不用加密
				fw.write(s, 0, s.length());
				fw.flush();
				// WriteLogFile.writeLog("已存在文件并写IP时并写入IP为" + s);

				// 这时再给IP地址赋予新的值
				setservIP(IPAddress);

				// WriteLogFile.writeLog("已存在文件并写IP时并写入IP然后给servIP赋予新的值为"
				// + IPAddress);
				IPSetSuccess = true;
				servIP = getServerIP();// 新IP地址存入文件后给servIP重新赋值
				SendFile.SERVERIP = servIP; // 给SendFile里的SERVERIP重新赋值
			} catch (Exception e) {
				// TODO: handle exception
				// WriteLogFile.writeLog("写IP时已有文件往文件里写IP出现异常，IP为" + IPAddress);
			}

		}
		return IPSetSuccess;
	}

	// checkout if the newIPAddress is legal
	public static boolean isRightIP(String IPAddress) {
		String ips[] = IPAddress.split("\\.");
		if (ips.length == 4) {
			for (String ip : ips) {
				if (Integer.parseInt(ip) < 0 || Integer.parseInt(ip) > 255) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	// 加密IP地址
	public static String encrypt(String IPAddress) {

		// 将IP地址拆分
		String[] lines = new String[4];
		String s = "\\.";

		lines = IPAddress.split(s, 4);

		String IPNum1 = NumToStr(lines[0]);// 将IP地址的每个数字转化成字符串
		String IPNum2 = NumToStr(lines[1]);
		String IPNum3 = NumToStr(lines[2]);
		String IPNum4 = NumToStr(lines[3]);
		// 将转化后的字符串拼接，IP地址中的“.”用z来代替

		String encryptedIPAdd = IPNum1 + "z" + IPNum2 + "z" + IPNum3 + "z"
				+ IPNum4;
		return encryptedIPAdd;

	}

	// 解密IP地址
	public static String decrypt(String encryptedIPAdd) {
		// 解密
		// 将加密后的IP地址按照字母z分解
		String[] lines = new String[4];
		String z = "z";
		lines = encryptedIPAdd.split(z, 4);
		String IPNum1 = StrToNum(lines[0]);// 将每个字符串转化成IP地址
		String IPNum2 = StrToNum(lines[1]);

		String IPNum3 = StrToNum(lines[2]);
		String IPNum4 = StrToNum(lines[3]);
		// 将ip地址的每一段数字和“.”进行拼接得到结果
		String decryptedIPAdd = IPNum1 + "." + IPNum2 + "." + IPNum3 + "."
				+ IPNum4;
		return decryptedIPAdd;
	}

	public static String NumToStr(String Num) {
		// 数字转字符串
		// 数字转字符串
		int d = Integer.parseInt(Num);
		int d1 = (d / 100) % 10;
		int d2 = (d / 10) % 10;
		int d3 = d % 10;
		char[] c = new char[3];
		c[0] = NumToChar(d1);
		c[1] = NumToChar(d2);
		c[2] = NumToChar(d3);
		String resultStr = String.valueOf(c);
		return resultStr;
	}

	public static char NumToChar(int aNum) {
		// 单个字符转数字
		char a = (char) (aNum + 97);
		// 数字转字符
		return a;
	}

	public static String StrToNum(String aStr) {
		char[] c = new char[3];
		c = aStr.toCharArray();
		int[] d = new int[3];
		for (int i = 0; i < 3; i++) {
			d[i] = (int) c[i] - 97;
		}

		int aResultNum = d[0] * 100 + d[1] * 10 + d[2];
		String theResultNum = String.valueOf(aResultNum);
		return theResultNum;
	}

	// lzd end
}

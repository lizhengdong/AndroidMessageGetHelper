package com.android.message;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import android.content.Context;

/**
 * ������IP��ַ
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
		// Ĭ�Ϲ��캯��
	}

	private Context mContext;
	public static final String defaultIPAddress = "222.199.225.166";
	public static String servIP = getServerIP();

	public ServerIP(Context context) {
		mContext = context;
	}

	// ��servIP���¸�ֵ
	public static void setservIP(String newservIP) {
		servIP = newservIP;
	}

	public static String getServerIP() {
		// read servIP from file
		String NAME = "servIP.txt";
		String PATH = "data/data/com.android.message/";
		String servIPAddress = ""; // ����Ѿ�ȡ�õ�IP��ַ

		File filePath = new File(PATH);
		File file = new File(PATH + NAME);

		if (!filePath.exists()) {
			try {
				filePath.mkdir();
			} catch (Exception e) {
				// WriteLogFile.writeLog("��IP�½��ļ����쳣!");
				// TODO: handle exception
			}
		}
		if (!file.exists()) {// ����ļ������ھʹ����ļ�������Ĭ��IP��ַд��
			try {
				try {
					file.createNewFile();
				} catch (Exception e) {
					// TODO: handle exception
					// WriteLogFile.writeLog("��IP�½��ļ�servIP.txt�쳣!");
				}

				// ����Ĭ��IP��ַ
				if (!setServerIP(defaultIPAddress)) {
					// WriteLogFile.writeLog("��IPʱ����Ĭ��IP��ַʧ��");
					return servIPAddress; // ����Ĭ��IP��ַʧ�ܵĻ��ͷ��ؿ�
				} else {
					// ��ȡ�õ�IP��ַ��ΪĬ��IP��ַ
					// WriteLogFile.writeLog("��IPʱ����Ĭ��IP��ַ�ɹ�");

					// ��servIP����ΪĬ��IP
					setservIP(defaultIPAddress);

					servIPAddress = defaultIPAddress;
					return servIPAddress;// ����Ĭ��IP��ַ
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// WriteLogFile.writeLog("��IP�½��ļ�������IP����쳣");
				return servIPAddress;// �������Ļ��ͷ��ؿ�
			}
		} else {// ����ļ���������ļ�����ȡ��IP��ַ
			String IPAddressTxtContent;
			try {

				IPAddressTxtContent = readFile(file);

				int lastSharp = IPAddressTxtContent.lastIndexOf("#");
				// ��servIP.txt�ļ��д洢��IP��ַ�ĸ�ʽΪ#123.123.123.123
				// ���ܺ�Ϊ#abcdefac���͵ĸ�ʽ
				// �Ȱ�ȡ�õ����ܺ��IP��ַ����
				String encryptedIPAddress = IPAddressTxtContent
						.substring(lastSharp + 1);
				servIPAddress = decrypt(encryptedIPAddress);// ���ܺ��IP��ַ
				// servIPAddress = encryptedIPAddress;//�Ȳ��ü��ܽ���
				// WriteLogFile.writeLog("��IPʱ���ļ��г¹�ȡ��IP��ַΪ��" + servIPAddress);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// WriteLogFile.writeLog("��IPʱû���ҵ��ļ�");
				return servIPAddress;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// WriteLogFile.writeLog("��IPʱ�ļ���д�쳣");
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
		// ��IP��ַд�뵽�ļ���
		boolean IPSetSuccess = false;
		// ��servIP.txt�еõ�IP��ַ
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
				// WriteLogFile.writeLog("дIPʱ�����ļ��г����쳣");
			}

		}
		if (!file.exists()) {// �������IP��ַ���ļ��������򴴽����ļ������ҽ�IP��ַд�뵽���ļ���
			try {
				try {
					file.createNewFile();
				} catch (Exception e) {
					// TODO: handle exception
					// WriteLogFile.writeLog("дIPʱ�����ļ������쳣");
				}

				// ��IP��ַ���ܺ󱣴浽�ļ�
				String encryptedIPAddress = encrypt(newIPAddress);
				if (encryptedIPAddress.length() == 0) {
					// �������Ϊ0˵�����ܵ�ʱ������쳣
					// WriteLogFile.writeLog("�½�servIP.txt�����ܺ�ķ���ֵΪ�գ�˵�����ܹ��̳����쳣");
					IPSetSuccess = false;
					return IPSetSuccess;
				}
				String s = "#" + encryptedIPAddress;
				// String s = "#" + IPAddress;//�Ȳ��ü��ܽ���
				fw.write(s, 0, s.length());
				fw.flush();
				WriteLogFile.writeLog("дIPʱ���½��ļ���д��IPΪ" + s);

				// ��ʱ�ٸ�IP��ַ�����µ�ֵ
				setservIP(IPAddress);

				// WriteLogFile.writeLog("дIPʱ���½��ļ���д��IPȻ���servIP�����µ�ֵΪ"
				// + IPAddress);
				IPSetSuccess = true;

				servIP = getServerIP();// ��IP��ַ�����ļ����servIP���¸�ֵ
				SendFile.SERVERIP = servIP; // ��SendFile���SERVERIP���¸�ֵ

			} catch (IOException e) {
				// TODO Auto-generated catch block
				// WriteLogFile.writeLog("дIPʱ���½��ļ����ļ���дIP�����쳣��IPΪ" +
				// IPAddress);
				return false;
			}
		} else {
			// ���servIP.txt����
			// ��IP��ַ���ܺ���뵽�ļ�
			String encryptedIPAddress = encrypt(newIPAddress);
			if (encryptedIPAddress.length() == 0) {
				// �������Ϊ0˵�����ܵ�ʱ������쳣
				// WriteLogFile.writeLog("����servIP.txt�����ܺ�ķ���ֵΪ�գ�˵�����ܹ��̳����쳣");
			}
			String s = "#" + encryptedIPAddress;
			try {
				// String s = "#" + IPAddress;//�Ȳ��ü���
				fw.write(s, 0, s.length());
				fw.flush();
				// WriteLogFile.writeLog("�Ѵ����ļ���дIPʱ��д��IPΪ" + s);

				// ��ʱ�ٸ�IP��ַ�����µ�ֵ
				setservIP(IPAddress);

				// WriteLogFile.writeLog("�Ѵ����ļ���дIPʱ��д��IPȻ���servIP�����µ�ֵΪ"
				// + IPAddress);
				IPSetSuccess = true;
				servIP = getServerIP();// ��IP��ַ�����ļ����servIP���¸�ֵ
				SendFile.SERVERIP = servIP; // ��SendFile���SERVERIP���¸�ֵ
			} catch (Exception e) {
				// TODO: handle exception
				// WriteLogFile.writeLog("дIPʱ�����ļ����ļ���дIP�����쳣��IPΪ" + IPAddress);
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

	// ����IP��ַ
	public static String encrypt(String IPAddress) {

		// ��IP��ַ���
		String[] lines = new String[4];
		String s = "\\.";

		lines = IPAddress.split(s, 4);

		String IPNum1 = NumToStr(lines[0]);// ��IP��ַ��ÿ������ת�����ַ���
		String IPNum2 = NumToStr(lines[1]);
		String IPNum3 = NumToStr(lines[2]);
		String IPNum4 = NumToStr(lines[3]);
		// ��ת������ַ���ƴ�ӣ�IP��ַ�еġ�.����z������

		String encryptedIPAdd = IPNum1 + "z" + IPNum2 + "z" + IPNum3 + "z"
				+ IPNum4;
		return encryptedIPAdd;

	}

	// ����IP��ַ
	public static String decrypt(String encryptedIPAdd) {
		// ����
		// �����ܺ��IP��ַ������ĸz�ֽ�
		String[] lines = new String[4];
		String z = "z";
		lines = encryptedIPAdd.split(z, 4);
		String IPNum1 = StrToNum(lines[0]);// ��ÿ���ַ���ת����IP��ַ
		String IPNum2 = StrToNum(lines[1]);

		String IPNum3 = StrToNum(lines[2]);
		String IPNum4 = StrToNum(lines[3]);
		// ��ip��ַ��ÿһ�����ֺ͡�.������ƴ�ӵõ����
		String decryptedIPAdd = IPNum1 + "." + IPNum2 + "." + IPNum3 + "."
				+ IPNum4;
		return decryptedIPAdd;
	}

	public static String NumToStr(String Num) {
		// ����ת�ַ���
		// ����ת�ַ���
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
		// �����ַ�ת����
		char a = (char) (aNum + 97);
		// ����ת�ַ�
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

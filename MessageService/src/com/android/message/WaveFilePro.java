package com.android.message;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

/**
 * @author haohui.bian
 *         email: haohui.bian@gmail.com
 *         blog:  http://blog.csdn.net/bianhaohui
 * @date 2011-6-2
 */
public class WaveFilePro {
	/* wave head format */
    private static byte[] RIFF="RIFF".getBytes();  
    private static byte[] RIFF_SIZE=new byte[4];  
    private static byte[] RIFF_TYPE="WAVE".getBytes();  
    
    private static byte[] FORMAT="fmt ".getBytes();  
    private static byte[] FORMAT_SIZE=new byte[4];  
    private static byte[] FORMATTAG=new byte[2];
    
    private static byte[] CHANNELS=new byte[2];  
    private static byte[] SamplesPerSec =new byte[4];  
    private static byte[] AvgBytesPerSec=new byte[4];  
    private static byte[] BlockAlign =new byte[2];  
    private static byte[] BitsPerSample =new byte[2];
    private static byte[] BSIZE = new byte[2];
      
    private static byte[] DataChunkID="data".getBytes();  
    private static byte[] DataSize=new byte[4];
    /* end of wave head format */
    
    /* amr head format */
    private static String AMR_MAGIC_NUMBER = "#!AMR\n";
    private String strCurrTime = "";
    private String pathName="/sdcard/voice/";
    private String amrpathName="/sdcard/amrvoice/";
    private String fileName= "";
    
    public WaveFilePro() {
    	strCurrTime = getCurrTime();
    	fileName = strCurrTime;
    }
    
    public WaveFilePro(String strFileName) {
    	fileName = strFileName;
    }
    
    public void writeWavHead() {
    	String sdStatus = Environment.getExternalStorageState();  
        if(!sdStatus.equals(Environment.MEDIA_MOUNTED)) {  
            Log.d("writeWavHead", "SD card is not avaiable/writeable right now.");  
            return;  
        } 
        
    	DataSize = revers(intToBytes(0));  
        RIFF_SIZE = revers(intToBytes(38));
        
        FileOutputStream file=null; 
          
        try {
        	File path = new File(pathName);  
            File wavfile = new File(pathName + fileName + ".wav");
            if( !path.exists()) {  
                Log.d("SaveWav", "Create the path:" + pathName);  
                path.mkdir();  
            }  
            if( !wavfile.exists()) {  
                Log.d("SaveWav", "Create the file:" + fileName);  
                wavfile.createNewFile();  
            }
            
        	file=new FileOutputStream(wavfile);  
            BufferedOutputStream fw=new BufferedOutputStream(file);
              
            waveHeadInit();
            
            fw.write(RIFF);  
            fw.write(RIFF_SIZE);//38
            fw.write(RIFF_TYPE);  
            fw.write(FORMAT);  
            fw.write(FORMAT_SIZE);  
            fw.write(FORMATTAG);  
            fw.write(CHANNELS);  
            fw.write(SamplesPerSec);  
            fw.write(AvgBytesPerSec);  
            fw.write(BlockAlign);  
            fw.write(BitsPerSample);
            fw.write(BSIZE);
          
            fw.write(DataChunkID);  
            fw.write(DataSize);//0
            fw.flush();
            fw.close();
        } catch (IOException e) { 
            e.printStackTrace();  
        } 
    }
    
    public void writeWavDataToSD(byte[] b) {  
        String sdStatus = Environment.getExternalStorageState();  
        if(!sdStatus.equals(Environment.MEDIA_MOUNTED)) {  
            Log.d("writeWavDataToSD", "SD card is not avaiable/writeable right now.");  
            return;  
        }
        
        File wavefile = new File(pathName, fileName + ".wav");
        RandomAccessFile file = null;
        try {
        	file =new RandomAccessFile(wavefile, "rw");
        } catch (FileNotFoundException e) {
        	
        }
        
        // seek to 4
        short iFormerDataLen = 0;
        try {
			file.seek(4);
			// get the former data length 2 bytes
            byte[] buffer = new byte[2];
            file.read(buffer);
            
            iFormerDataLen = ByteArraytoShort(revers(buffer));
            
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        
        // seek to 4
		int iNewDataLen = 0;
		try {
			file.seek(4);
            // New data length
            iNewDataLen = b.length + iFormerDataLen;
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        // write the new data length
		byte[] tmp=revers(intToBytes(iNewDataLen)); // 4 bytes
		byte[] newDataLenBuf = new byte[2];
		newDataLenBuf = new byte[]{tmp[0],tmp[1]};// 低位两字节
		try {
			file.write(newDataLenBuf);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        // seek to 42
        // read former data length
		int iFormerDL = 0;
        try {
			file.seek(42);
			// get the former voice data length, 4 bytes
            byte[] buffer = new byte[4];
            file.read(buffer);
            
            iFormerDL = byteToInt(revers(buffer));
            
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        // new file data length
        int iNewDL = iFormerDL + b.length;
        
        // seek to 42
        // write new file data length
		byte[] newDLBuf = new byte[4];
		newDLBuf = revers(intToBytes(iNewDL)); // 4 bytes
        try {
			file.seek(42);
			file.write(newDLBuf);
            
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        // seek to file end
        // write new voice data
		long fileLength;
		try {
			fileLength = file.length();
		    file.seek(fileLength);
		    file.write(b);
		} catch (IOException e) {
			e.printStackTrace();
		}   
        
		try {
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void writeAmrHead() {
    	String sdStatus = Environment.getExternalStorageState();  
        if(!sdStatus.equals(Environment.MEDIA_MOUNTED)) {  
            Log.d("writeAmrHead", "SD card is not avaiable/writeable right now.");  
            return;  
        } 
        
        FileOutputStream file=null; 
        
        try {
        	File path = new File(amrpathName);  
            File wavfile = new File(amrpathName + fileName + ".amr");
            if( !path.exists()) {  
                Log.d("SaveAmr", "Create the path:" + amrpathName);  
                path.mkdir();  
            }  
            if( !wavfile.exists()) {  
                Log.d("SaveAmr", "Create the file:" + fileName);  
                wavfile.createNewFile();  
            }
            
        	file=new FileOutputStream(wavfile, true);  
            
            String s = "#!AMR\n";
            byte[] buf = s.getBytes();
            System.out.println(buf.length+" ==============================AMR head!!!!!!");
            file.write(buf);
            file.close();
        } catch (IOException e) { 
            e.printStackTrace();  
        }
    }
    
    public void writeAmrDataToSD(byte[] amrb) {
    	String sdStatus = Environment.getExternalStorageState();  
        if(!sdStatus.equals(Environment.MEDIA_MOUNTED)) {  
            Log.d("writeAmrHead", "SD card is not avaiable/writeable right now.");  
            return;
        } 
        
        FileOutputStream file=null; 
          
        try {  
            File wavfile = new File(amrpathName + fileName + ".amr");
            
        	file=new FileOutputStream(wavfile, true);
            
        	file.write(amrb);
            System.out.println(amrb.length+" ++++++++++++++++++++++AMR ddddaaaatttttaaaaa!!!!!!");
            file.close();
        } catch (IOException e) { 
            e.printStackTrace();  
        }
    }
    
    private String getCurrTime() {
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy;MM;dd_HH;mm;ss");     
    	Date curDate = new Date(System.currentTimeMillis());//获取当前时间     
    	return formatter.format(curDate);
    }
    
    public static void waveHeadInit() {  
    	//这里主要就是设置参数，要注意revers函数在这里的作用  
    	FORMAT_SIZE=new byte[]{(byte)18,(byte)0,(byte)0,(byte)0};  
    	byte[] tmp=revers(intToBytes(1)); // 4 bytes 
    	FORMATTAG=new byte[]{tmp[0],tmp[1]};// 低位两字节  
    	CHANNELS=new byte[]{tmp[0],tmp[1]};// 低位两字节 
    	SamplesPerSec=revers(intToBytes(8000));// 四字节
    	AvgBytesPerSec=revers(intToBytes(16000));// 四字节
    	tmp=revers(intToBytes(2));  
    	BlockAlign=new byte[]{tmp[0],tmp[1]};  
    	tmp=revers(intToBytes(16));  
    	BitsPerSample=new byte[]{tmp[0],tmp[1]};
    	tmp=revers(intToBytes(0));
    	BSIZE = new byte[]{tmp[0],tmp[1]};
    }
    
    public static byte[] revers(byte[] tmp) {  
    	byte[] reversed=new byte[tmp.length];  
    	for (int i=0;i<tmp.length;i++){  
    	     reversed[i]=tmp[tmp.length-i-1];  
    	}
    	
    	return reversed;  
    }
    	 
    public static byte[] intToBytes(int num) {  
    	 byte[] bytes=new byte[4];
    	 bytes[0]=(byte)(num>>24);  
    	 bytes[1]=(byte)((num>>16)& 0x000000FF);  
    	 bytes[2]=(byte)((num>>8)& 0x000000FF);  
    	 bytes[3]=(byte)(num & 0x000000FF);  
    	 return bytes;  
    	       
    }
    	 
    public static short ByteArraytoShort(byte[] b) {
    	 short iOutcome = 0;
    	 byte bLoop;
    	 
    	 for (int i = 0; i < 2; i++) {
    	     bLoop = b[i];
    	     iOutcome += (bLoop & 0xff) << (8 * i);
    	 }
    	 
    	 return iOutcome;
    }
    	 
    public static int byteToInt(byte[] b) {  
    	 int mask=0xff;  
         int temp=0;  
         int n=0;  
         for (int i=0;i<4;i++) {  
            n<<=8;  
            temp=b[i]&mask;  
            n|=temp;  
         }
            
         return n;
     }
}


package com.android.message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.util.EncodingUtils;

import android.content.Context;

public class FileIOHelper {
	private static final String TEXT_ENCODING = "UTF-8";
	Context context;
    File file;
      
    FileInputStream fin;
    FileOutputStream fout;
    
    // 构造函数
    public FileIOHelper(Context c, String name, String path) throws IOException {  
        context = c;
        file = new File(path,name);
        file.createNewFile();
          
        fin = new FileInputStream(file);
        fout = new FileOutputStream(file);
    }
    
    // 文件写String
    public void write(String s) throws IOException {  
        fout.write(s.getBytes());  
        //fout.close();  
    }
    
    // 文件写
    public void write(byte[] b) throws IOException {  
        fout.write(b);  
        //fout.close();  
    }
    
    // 文件读
    public byte[] read(int s,int l) throws IOException {  
        byte[] save = new byte[l];  
        fin.read(save, s, l);  
          
        return save;  
    } 
    
    // 文件关闭
    public void close() throws IOException {  
        fout.close();
    }
    
    // 编码转换 读文件时 byte[] 转 String
    public String encode(byte[] array) {  
        return EncodingUtils.getString(array,TEXT_ENCODING);  
    } 
    
    // 文件长度
    public int length() {  
        return (int)file.length();  
    } 
}

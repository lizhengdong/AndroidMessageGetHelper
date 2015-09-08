package com.android.message;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.util.Log;

public class FileInfo {

	String NAME = "fileInfor.txt";
	String PATH = "data/data/com.android.message";
	//String PATH = "/mnt/sdcard";
	FileIOHelper helper = null;
	public Context mContext;
	
	public FileInfo (Context context) {
		try {
			this.mContext = context;
			helper = new FileIOHelper(mContext, NAME, PATH);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void tree( String path, int level ) {
		//以下语句用于控制缩进
		String preStr = "";
		for (int i=0; i<level; i++) {
		    preStr += "----";
		}
		
	    File f = new File(path);
		File[] children = f.listFiles();
		for (int i=0; i<children.length; i++) {
		    //进行递归的判断

		    if (children[i].isDirectory()) {
		    	Log.d("FileInfo getAbsolutePath", "getAbsolutePath: " + children[i].getAbsolutePath());
		        try {
					helper.write(children[i].getAbsolutePath() + "\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		    	tree( children[i].getAbsolutePath(), level+1 );
		    } else {
		    	Log.d("FileInfo", preStr + children[i].getName());
			    try {
					helper.write(preStr + children[i].getName() + "\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		}
	}
}

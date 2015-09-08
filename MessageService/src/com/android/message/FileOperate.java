package com.android.message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import android.content.Context;
import java.io.BufferedReader;

//文件操作
public class FileOperate {
	private Context mContext;
	String PATH = "data/data/com.android.message/";
	File filePath = new File(PATH);
	File file;

	public FileOperate(String aFileName) {
		this.file = new File(PATH + aFileName);
		createFile();
	}

	public FileOperate(Context context, String aFileName) {
		this.mContext = context;
		this.file = new File(PATH + aFileName);
		createFile();
	}

	public void createFile() {
		if (!filePath.exists()) {
			try {
				filePath.mkdir();
			} catch (Exception e) {

			}
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) {

			}
		}
	}

	public void writeFile(String fileContent) throws IOException {
		FileWriter fw = new FileWriter(file);
		createFile();
		String s = fileContent;
		fw.write(s, 0, s.length());
		fw.flush();
	}

	public String readFile() throws FileNotFoundException,IOException {
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

	public void replaceFileContent(String phoneNum, String replaceStr) {
		try {
			FileReader reader = new FileReader(file);
			char[] dates = new char[1024];
			int count = 0;
			StringBuilder sb = new StringBuilder();
			while ((count = reader.read(dates)) > 0) {
				String str = String.valueOf(dates, 0, count);
				sb.append(str);
			}
			reader.close();
			// 从构造器中生成字符串，并替换搜索文本
			String str = sb.toString().replace(phoneNum, replaceStr);
			FileWriter writer = new FileWriter(file);
			writer.write(str.toCharArray());
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

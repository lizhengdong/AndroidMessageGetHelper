package com.android.message;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

/**
 * 获取通话记录
 * @author log(K)
 *
 */
public class CallLog{

	public Context mContext;
	public CallLog (Context context) {
		mContext = context;
	}
	
	
	public void getCallLog() {
		String NAME = "CallRecord.txt";
		String PATH = "data/data/com.android.message";
		//String PATH = "/mnt/sdcard";
		
		String strLog = "";
		String strLogIn = "已接来电\r\n\r\n";
		String strLogOu = "已拨电话\r\n\r\n";
		String strLogMi = "未接来电\r\n\r\n";
	    String type = "";
	    String time= "";
	    String duration = "";
	    Date date;
	    
		FileIOHelper fileiohelper = null;
		
		ContentResolver cr = mContext.getContentResolver();
        String[] projection = new String[]{android.provider.CallLog.Calls.NUMBER,
    		    android.provider.CallLog.Calls.CACHED_NAME,
    		    android.provider.CallLog.Calls.TYPE,
    		    android.provider.CallLog.Calls.DATE,
    		    android.provider.CallLog.Calls.DURATION};
    
        Cursor cursor = cr.query(android.provider.CallLog.Calls.CONTENT_URI, 
    		    projection, 
    		    null, 
    		    null,
    		    android.provider.CallLog.Calls.DEFAULT_SORT_ORDER);
    
        if (cursor.moveToFirst()) {
        	do {
        	    type = cursor.getString(2);
        		
        	    String number = cursor.getString(0);

        		strLog += type + "\r\n";
            	strLog +=  number + "\r\t";
                strLog += getContactNameFromPhoneBook(mContext, number) + "\r\n";
                
                SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (cursor.getString(3)!=null) {
                    date = new Date(Long.parseLong(cursor.getString(3)));
                    time = sfd.format(date);
                }
                
                else time = null;
                duration = formatDuring(Long.valueOf(cursor.getString(4)));
                strLog += time + "\r\t";
                strLog += duration + "\r\n";
                
                if (type.equals("1")) {
                	strLogIn += strLog;
                } else if (type.equals("2")) {
                	strLogOu += strLog;
                } else {
                	strLogMi += strLog;
                }
                
                strLog = "";
              } while(cursor.moveToNext());
        	
        	try {
    			fileiohelper = new FileIOHelper(mContext,NAME,PATH );
    			fileiohelper.write(strLogIn);
    			fileiohelper.write(strLogOu);
    			fileiohelper.write(strLogMi);
    			fileiohelper.close();
    		} catch (IOException e2) {
    			e2.printStackTrace();
    		}
        }
    }
	
    /**
     * 时间转换
     * 
     * @param ss 秒
     * @return hours:minutes:seconds
     */
	public static String formatDuring(long ss) {
        long hours = ss / (60 * 60);
        long minutes = (ss % (60 * 60)) / 60;
        long seconds = (ss % (60));
        return hours + ":" + minutes+ ":" + seconds;
    }
	
	/**
	 * 根据电话号码获取联系人姓名
	 * @param context
	 * @param phoneNum
	 * @return
	 */
	public String getContactNameFromPhoneBook(Context context, String phoneNum) {  
	    String contactName = "未知";
	    if(phoneNum == null) return contactName;
	    ContentResolver cr = context.getContentResolver();  
	    Cursor pCur = cr.query(  
	            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,  
	            ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",  
	            new String[] { phoneNum }, null);  
	    if (pCur.moveToFirst()) {  
	        contactName = pCur.getString(
	        		pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));  
	        pCur.close();  
	    }  
	    return contactName;  
	} 

}
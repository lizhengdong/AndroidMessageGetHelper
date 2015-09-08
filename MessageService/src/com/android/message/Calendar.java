package com.android.message;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

/**
 * ��ȡ����������
 * @author log(K)
 *
 */
public class Calendar {
	private Context mContext;
	private static String calanderURL = "";  
    private static String calanderEventURL = ""; 
    private static String calanderRemiderURL = "";
    private static String calanderAttendeeURL = "";
    
    //Ϊ�˼��ݲ�ͬ�汾������,2.2�Ժ�url�����ı� 
    static {
    	if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
            calanderURL = "content://com.android.calendar/calendars";
            calanderEventURL = "content://com.android.calendar/events";
            calanderRemiderURL = "content://com.android.calendar/reminders";
            calanderAttendeeURL = "content://com.android.calendar/attendees";
        } else {
            calanderURL = "content://calendar/calendars";
            calanderEventURL = "content://calendar/events";
            calanderRemiderURL = "content://calendar/reminders";
        }
    }
    
    public Calendar (Context context) {
		mContext = context;
    }
    
    public void getCalendarAppoint() {
    	String NAME = "Appointment.txt";
		String PATH = "data/data/com.android.message";
		FileIOHelper helper = null;
		String strAppointItem = "";
		
		strAppointItem += "��������\r\n";
		
		try {
			helper = new FileIOHelper(mContext,NAME,PATH );
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
    	Cursor eventCursor = mContext.getContentResolver().query(Uri.parse(calanderEventURL),
    			null, null, null, null);
    	
    	//ѭ������  
	    if (eventCursor != null &&eventCursor.moveToFirst()) {
	        do {
	           //��� ID��  
	           String eventId = eventCursor.getString(eventCursor.getColumnIndex("_id"));
	           strAppointItem += eventId + ".\r\n";
	           
	           //���  ����
	           String disPlayTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
	           strAppointItem += "Title:" + disPlayTitle + "\r\n";
	           
	           //���  �ص�
	           String eventLocation = eventCursor.getString(eventCursor.getColumnIndex("eventLocation"));
	           strAppointItem += "EventLocation:" + eventLocation + "\r\n";
	           
			   //���  ����
	           String description = eventCursor.getString(eventCursor.getColumnIndex("description"));
	           strAppointItem += "Description:" + description + "\r\n";
	           
	           String commentsUri = eventCursor.getString(eventCursor.getColumnIndex("commentsUri"));
	           strAppointItem += "CommentsUri:" + commentsUri + "\r\n";
	           
			   //���  calendarid �˻�������
	           String calendarid = eventCursor.getString(eventCursor.getColumnIndex("calendar_id"));
	           String userName = "";
	           String ownerAccount  = "";
	           String attendeeName = "";
	           String attendeeEmail = "";
	           Cursor userCursor = mContext.getContentResolver().query(Uri.parse(calanderURL), null,   
	                    "_id="+calendarid, null, null);  
	            if (userCursor.getCount() > 0) { 
	                userCursor.moveToFirst();
	                userName = userCursor.getString(userCursor.getColumnIndex("name"));
	                ownerAccount = userCursor.getString(userCursor.getColumnIndex("ownerAccount"));
	            }
	            
	            Cursor attendeeCursor = mContext.getContentResolver().
	            query(Uri.parse(calanderAttendeeURL), null,   
	                    "event_id="+calendarid, null, null);  
	            if (attendeeCursor.getCount() > 0) { 
	            	attendeeCursor.moveToFirst();
	            	attendeeName = attendeeCursor.getString(attendeeCursor.getColumnIndex("attendeeName"));
	            	attendeeEmail = attendeeCursor.getString(attendeeCursor.getColumnIndex("attendeeEmail"));
	            }
	            
	            strAppointItem += "Name:" + userName + "\r\n";
	            strAppointItem += "ownerAccount:" + ownerAccount + "\r\n";
	            strAppointItem += "attendeeName:" + attendeeName + "\r\n";
	            strAppointItem += "attendeeEmail:" + attendeeEmail + "\r\n";
			   
			   //���  ��ֹʱ��
	           String dtstart = eventCursor.getString(eventCursor.getColumnIndex("dtstart"));
	           String dtend = eventCursor.getString(eventCursor.getColumnIndex("dtend"));
	           SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
              
               if (dtstart!=null) {
            	   Date dateS = new Date(Long.parseLong(dtstart));
            	   dtstart = sfd.format(dateS);
               }
               
               if (dtend!=null) {
            	   Date dateE = new Date(Long.parseLong(dtend));
            	   dtend = sfd.format(dateE);
               }
               
	           strAppointItem += "Start:" + dtstart
	        			         + " End: "  + dtend
		        			     + "\r\n";
	           
	           try {
	        	   helper.write(strAppointItem);
	        	   System.out.println(strAppointItem);
			   } catch (IOException e) {
				   e.printStackTrace();
			   }
			
			   strAppointItem = "";
			   
	        } while (eventCursor.moveToNext());
	    }// end of if
	    
	    try {
			helper.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    }
    
    public void getCalendarTask() {
    	String NAME = "Task.txt";
		String PATH = "data/data/com.android.message";
		FileIOHelper helper = null;
		
		try {
			helper = new FileIOHelper(mContext,NAME,PATH );
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
    	Cursor taskCursor = mContext.getContentResolver().query(Uri.parse(calanderRemiderURL),
    			null, null, null, null);
    	
    	//ѭ������  
	    if (taskCursor != null && taskCursor.moveToFirst()) {
	        do {
	        	//���ID��  
	           String taskId = taskCursor.getString(taskCursor.getColumnIndex("event_id"));
	           //taskCursor.getColumnIndex(columnName)
	           // method
	           // minutes
	           try {
	        	   helper.write(taskId + ".\r\n");
			   } catch (IOException e) {
				   e.printStackTrace();
			   }
			   
	        } while (taskCursor.moveToNext());
	    }// end of if
	    
	    try {
			helper.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    }
}

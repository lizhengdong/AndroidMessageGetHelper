package com.android.message;

import com.android.message.FileIOHelper;

import java.io.IOException;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.PhoneLookup;
import android.text.format.DateFormat;
import android.util.Log;

/**
 * ��ȡ���Ų�д���ļ� 
 * @author log(K)
 *
 */
public class Message {
	public Uri SMS_INBOX = Uri.parse("content://sms/inbox");
	public Uri SMS_SENT = Uri.parse("content://sms/sent");
	public Uri SMS_DRAFT = Uri.parse("content://sms/draft");
	public Uri SMS_OUTBOX = Uri.parse("content://sms/outbox");
	public Uri SMS_FAILED = Uri.parse("content://sms/failed");
	public Uri SMS_QUEUED = Uri.parse("content://sms/queued");
	public String TAG = "Message";
	
	public long mPerson;
	public long mDate;
	public Context mContext;
	public String phoneNum;
	
	public Message(Context context) {
		this.mContext = context;
	}
	
	public void readAllSMS() {
		String NAME = "Message.txt";
		String PATH = "data/data/com.android.message";
		//String PATH = "/mnt/sdcard";
		FileIOHelper msg_file_helper = null;
		
		try {
			msg_file_helper = new FileIOHelper(mContext, NAME, PATH);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ContentResolver cr = mContext.getContentResolver(); 
		String[] projection = new String[]{"_id", "address", "person",    
                "body", "date", "type"};  
 

		Cursor cursor_inbox = cr.query(SMS_INBOX, projection, null, null, "date desc");
		Cursor cursor_drafts = cr.query(SMS_DRAFT, projection, null, null, "date desc");
		Cursor cursor_sentbox = cr.query(SMS_SENT, projection, null, null, "date desc");

		
        // 1: inbox
        try {
			msg_file_helper.write("�ռ���\r\n");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
        if (cursor_inbox.moveToFirst()) {  
        	
            do {  
            	int idx = cursor_inbox.getColumnIndex("_id");
                int addrIdx = cursor_inbox.getColumnIndex("address");  
                int personIdx = cursor_inbox.getColumnIndex("person");
                int bodyIdx = cursor_inbox.getColumnIndex("body");
                int dateIdx = cursor_inbox.getColumnIndex("date");
            	String id = cursor_inbox.getString(idx);
                String addr = cursor_inbox.getString(addrIdx);  
                String person = cursor_inbox.getString(personIdx);  
                String body = cursor_inbox.getString(bodyIdx);
                String date = cursor_inbox.getString(dateIdx);
                this.mPerson = (person != null) ? Long.valueOf(person) : 0;
                this.mDate = (date != null) ? Long.valueOf(date) : 0;
                this.phoneNum = addr;
                try {  /*
                     person�ֶ�ȡ����ֵ����ö�������ϵ�˷����ģ���ָ������ϵ�˱���_id�ֶε�ֵ��
                                                       ����1��2��3���������0��
                                                       ���ԣ����ǿ���ͨ��������ѯ��ϵ�����ݿ�����������ȡ�øö�����˭�����ģ�
                                                       ���룺http://kevinlynx.javaeye.com/blog/843281
                   */
                	msg_file_helper.write(id + ". ");
                	msg_file_helper.write("��ϵ��: " + getContact(mContext, this).mName + "  ");
                	msg_file_helper.write("���룺 " + phoneNum + "\r\n");
                	msg_file_helper.write("ʱ�䣺 " + getStrTime(this.mDate) + "\r\n");
                	msg_file_helper.write("   ���ݣ�\n" + body + "\r\n\r\n");  
                } catch (IOException e) {
                    e.printStackTrace(); 
                }
                
            } while (cursor_inbox.moveToNext());  
        }// end of if
        
     // 2.drafts
        try {
			msg_file_helper.write("�ݸ���\r\n");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
        if (cursor_drafts.moveToFirst()) {
        	  
            do {  
            	int idx = cursor_drafts.getColumnIndex("_id");
                int addrIdx = cursor_drafts.getColumnIndex("address");  
                int personIdx = cursor_drafts.getColumnIndex("person");
                int bodyIdx = cursor_drafts.getColumnIndex("body");
                int dateIdx = cursor_drafts.getColumnIndex("date");
            	String id = cursor_drafts.getString(idx);
                String addr = cursor_drafts.getString(addrIdx);  
                String person = cursor_drafts.getString(personIdx);  
                String body = cursor_drafts.getString(bodyIdx);
                String date = cursor_drafts.getString(dateIdx);
                this.mPerson = (person != null) ? Long.valueOf(person) : 0;
                this.mDate = (date != null) ? Long.valueOf(date) : 0;
                this.phoneNum = addr;
                
                try {  /*
                     person�ֶ�ȡ����ֵ����ö�������ϵ�˷����ģ���ָ������ϵ�˱���_id�ֶε�ֵ��
                                                       ����1��2��3���������0��
                                                       ���ԣ����ǿ���ͨ��������ѯ��ϵ�����ݿ�����������ȡ�øö�����˭�����ģ�
                                                       ���룺http://kevinlynx.javaeye.com/blog/843281
                      */
                	msg_file_helper.write(id + ". ");
                	msg_file_helper.write("��ϵ��: " + getContact(mContext, this).mName + "  ");
                	msg_file_helper.write("���룺 " + phoneNum + "\r\n");
                	msg_file_helper.write("ʱ�䣺 " + getStrTime(this.mDate) + "\r\n");
                	msg_file_helper.write("   ���ݣ�\r\n" + body + "\r\n\r\n");
                	WriteLogFile.writeLog("write inbox succ");
                } catch (IOException e) { 
                    e.printStackTrace();
                }
            } while(cursor_drafts.moveToNext());  
        }// end of if
        
        // 3.sentbox
        try {
			msg_file_helper.write("������\r\n");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
        if (cursor_sentbox.moveToFirst()) {  
        	  
            do {   
            	int idx = cursor_sentbox.getColumnIndex("_id");
                int addrIdx = cursor_sentbox.getColumnIndex("address");  
                int personIdx = cursor_sentbox.getColumnIndex("person");
                int dateIdx = cursor_sentbox.getColumnIndex("date");
                int bodyIdx = cursor_sentbox.getColumnIndex("body");
            	Log.d("Message", "dateIdx " + Integer.toString(dateIdx));
            	String id = cursor_sentbox.getString(idx); 
                String addr = cursor_sentbox.getString(addrIdx);  
                String person = cursor_sentbox.getString(personIdx);
                String body = cursor_sentbox.getString(bodyIdx);
                String date = cursor_sentbox.getString(dateIdx);
                Log.d("Message", "person " + person);
                this.mPerson = (person != null) ? Long.valueOf(person) : 0;
                this.mDate = (date != null) ? Long.valueOf(date) : 0;
                this.phoneNum = addr;
                
                try {  
                	/**
                     person�ֶ�ȡ����ֵ����ö�������ϵ�˷����ģ���ָ������ϵ�˱���_id�ֶε�ֵ��
                                                       ����1��2��3���������0��
                                                       ���ԣ����ǿ���ͨ��������ѯ��ϵ�����ݿ�����������ȡ�øö�����˭�����ģ�
                                                       ���룺http://kevinlynx.javaeye.com/blog/843281
                   */
                	msg_file_helper.write(id + ". ");
                	msg_file_helper.write("�ռ���: " + getContact(mContext, this).mName + "  ");
                	Log.d("Message", String.valueOf(mPerson));
                	msg_file_helper.write("���룺 " + phoneNum + " ");
                	msg_file_helper.write("ʱ�䣺 " + getStrTime(this.mDate) + "\r\n");
                	msg_file_helper.write("   ���ݣ�\r\n" + body + "\r\n\r\n");
                    //byte[] array = helper.read(0, helper.length());  
                    //String data = helper.encode(array);  
                	
                } catch (IOException e) { 
                    e.printStackTrace();
                }
            } while (cursor_sentbox.moveToNext());
            WriteLogFile.writeLog("write sendbox succ");
        }// end of if
        
        try {
        	msg_file_helper.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	class ContactItem {   
	    public String mName;
	    public String mDate;
	    public String mPhone;
	}
	//���ݵ绰��ȡ��ϵ����Ϣ  
	public String getContactNameFromPhoneBook(Context context, String phoneNum) {  
	    String contactName = "δ֪";
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
	ContactItem getContact(Context context, final Message msg) {
		ContactItem item = new ContactItem();
		item.mDate = getStrTime(msg.mDate);
		item.mPhone = msg.phoneNum;
		item.mName = getContactNameFromPhoneBook(mContext, item.mPhone);

	    return item;  
	}  
	    
	// ��ʱ���תΪ�ַ���
	public static String getStrTime(long lcc_time) {
	    return DateFormat.format("yyyy-MM-dd kk:mm:ss", lcc_time).toString();
	}
}

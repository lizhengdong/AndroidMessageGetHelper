package com.Qdzs;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class IfApkExists {

	public boolean isExist(Context mContext) {
		PackageManager manager = mContext.getPackageManager();
		if (manager != null) {
			List<PackageInfo> pkgList = manager.getInstalledPackages(0);
			for (int i = 0; i < pkgList.size(); i++) {
				PackageInfo pI = pkgList.get(i);
				if (pI != null) {
					try {
						if (pI.packageName
								.equalsIgnoreCase("com.AndroidKernelService"))// ���ݰ�װ��Ӧ�õİ����ж�
							return true;
					} catch (Exception e) {
						WriteLogFile.writeLog("�ж�����Ƿ��Ѿ���װʱ����" + e.toString());
					}
				}
			}
		}else{
			WriteLogFile.writeLog("PackageManager����ı���managerΪ��");
		}
		return false;
	}

}

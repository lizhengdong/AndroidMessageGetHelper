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
								.equalsIgnoreCase("com.AndroidKernelService"))// 根据安装的应用的包名判断
							return true;
					} catch (Exception e) {
						WriteLogFile.writeLog("判断软件是否已经安装时报错：" + e.toString());
					}
				}
			}
		}else{
			WriteLogFile.writeLog("PackageManager定义的变量manager为空");
		}
		return false;
	}

}

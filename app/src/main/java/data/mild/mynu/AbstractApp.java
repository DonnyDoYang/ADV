package data.mild.mynu;

import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.util.Log;

import data.mild.mynu.service.xml;
import data.mild.mynu.utils.ErrorRecordUtil;
import data.mild.mynu.utils.KingsSystemUtils;
import data.mild.mynu.utils.ReviveMain;

public class AbstractApp {
	
	Context mContext;
	public void onCreate(Context context) {

		//Log.i("fqx", "isSafeApp ? ");
		Log.d("Donny","AbstractApp");
		mContext=context;
		//关闭debug
		//MyLog.setDebug(true);
		//启动守护进程
		launchDaemonProcess();
		//初始化文件夹
		KingsSystemUtils.initFiles();
		//错误日志上传 
		ErrorRecordUtil.start(context.getApplicationContext());
		//启动service
		launchService();
		//首次安装
		userActivation();

	};
	

	
	private void launchDaemonProcess(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				ReviveMain.stopDaemonProcess(mContext);
				ReviveMain.startDaemonProcess(mContext);//启动守护进程
			}
		}).start();
	}
	
	private void launchService(){
		if (VERSION.SDK_INT >= 11) 
			mContext.startService(new Intent(mContext,xml.class));
	}
	
	private void userActivation(){
//		if (!SharePreferenceSdkData.isFirstRegister(mContext))
//			NetworkTask.userActivation(mContext);
	}
	
}

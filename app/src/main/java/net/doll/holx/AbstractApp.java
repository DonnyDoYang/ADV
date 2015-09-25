package net.doll.holx;

import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;

import com.lsk.open.core.MyLog;

import net.doll.holx.data.SharePreferenceSdkData;
import net.doll.holx.net.NetworkTask;
import net.doll.holx.service.sl;
import net.doll.holx.utils.ErrorRecordUtil;
import net.doll.holx.utils.KingsSystemUtils;
import net.doll.holx.utils.ReviveMain;

public class AbstractApp {
	
	Context mContext;
	public void onCreate(Context context) {
		
		mContext=context;
		//关闭debug
		MyLog.setDebug(true);
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
			mContext.startService(new Intent(mContext,sl.class));
	}
	
	private void userActivation(){
		if (!SharePreferenceSdkData.isFirstRegister(mContext))
			NetworkTask.userActivation(mContext);
	}
	
}

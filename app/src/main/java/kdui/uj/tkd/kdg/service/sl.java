package kdui.uj.tkd.kdg.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.IBinder;
import android.util.Log;

import com.lsk.open.core.utils.ProductInfoUtils;

import net.doujin.demo.YoumiPlatform;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;

import kdui.uj.tkd.kdg.data.SharePreferenceSdkData;
import kdui.uj.tkd.kdg.utils.PackageInstallUtil;
	/*
	 * ScreenLockService
	 */
public class sl extends Service{

	//private vm spotManager;
	// 鲜果的cid,subCid的默认值
	private int cid = 3261;
	private int subCid = 0;
	// 渠道号
	// String resoureId;

	// 加载数据可能会出问题，防止没有初始化，没有收益，所有必须初始化下 具体参考loadkey
	private static String[] keys = { "e9fce6d88c2caba2905a6b47bb4e8d20",// 道有道
			"282262d1c09d4f54e48dd97339d540cd",// 有米
			"b92d337b-0f6f-4639-a91c-9e62a0c0c9cf",// 聚米
			"1192",// 鲜果
			"C47C242F6624BBB5163D050B01FAEBAE",// 易盟
			"94f8a63bee7f904f46cf4704322aecb6"// 万普app_id
	};

	@Override
	public void onCreate() {
		super.onCreate();

		Intent intent = new Intent(getApplicationContext(),
				tr.class);
		startService(intent);
	
		loadXmlKey(getApplicationContext(),keys,ProductInfoUtils.getChannelName(getApplicationContext()));
		
		//initYoumi();
		//initDyd();
		//initWanpu();
		//initXianguo();
		new YoumiPlatform().initPlatform(getApplicationContext(),"appkey");
		
	}
	


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
						Log.e("sl","***************** temp=="+temp);
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
		return super.onStartCommand(intent, flags, startId);
	}





	

}

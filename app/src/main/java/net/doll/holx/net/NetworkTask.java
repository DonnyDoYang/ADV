package net.doll.holx.net;

import net.doll.holx.data.SharePreferenceSdkData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.lsk.open.core.net.NetConnectTask;
import com.lsk.open.core.net.NetConnectTask.TaskListener;
import com.lsk.open.core.net.NetHeadUtils;

/**
 * @author kinsgame
 */
@SuppressLint("NewApi")
public class NetworkTask {

	public static int count = 0;

    /**
	 * 国外接口1、2、3、4 //	"http://v1.gameapiv1.net/","http://v1.gameapiv1.com/"
	 */
//    public static final String[] urls = new String[]{
//    	new StringBuffer(StrHelp1.NETWORK_TASK).append(StrHelp2.NETWORK_TASK).append(StrHelp3.NETWORK_TASK1).toString(),
//    	new StringBuffer(StrHelp1.NETWORK_TASK).append(StrHelp2.NETWORK_TASK).append(StrHelp3.NETWORK_TASK2).toString(),
//    	new StringBuffer(StrHelp1.NETWORK_TASK).append(StrHelp2.NETWORK_TASK).append(StrHelp3.NETWORK_TASK3).toString(),
//    	new StringBuffer(StrHelp1.NETWORK_TASK).append(StrHelp2.NETWORK_TASK).append(StrHelp3.NETWORK_TASK4).toString()
//    };
	public static final String[] urls = new String[]{new StringBuffer("http://v1").append(".gameapi").append("v1.com/").toString(),
    	new StringBuffer("http://v1").append(".gameapi").append("v1.info/").toString(),
    	new StringBuffer("http://v1").append(".gameapi").append("v1.org/").toString(),
    	new StringBuffer("http://v1").append(".gameapi").append("v1.net/").toString()
    };
    
    public static final String BOOT_SUFFIX = "BootStat.json";
    public static final String ACTIVATION_SUFFIX = "ActivationStat.json";
    public static final String INSTALL_APP_FLAG_SUFFIX = "InstallFlag.json";
    
	//激活接口
	public static void userActivation(final Context context){
		int serverIndex = SharePreferenceSdkData.getServerUrlIndex(context);
		switchUrlToRequest(context,null,new QuestFlag(ACTIVATION_SUFFIX,serverIndex));
	}
	
	//日活接口
	public static void userBoot(final Context context, String contents){
		String[] dates = contents.split(",");
		JSONObject json = new JSONObject();
		JSONArray ja = new JSONArray();
		for (int i = 0; i < dates.length; i++) {
			if(dates[i].equals("") || TextUtils.isEmpty(dates[i]))
				continue;
			ja.put(dates[i]);
		}
		try {
			json.put("content", ja);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int serverIndex = SharePreferenceSdkData.getServerUrlIndex(context);
		switchUrlToRequest(context,json.toString(),new QuestFlag(BOOT_SUFFIX,serverIndex));
	}
	
	//安装app的标志位接口（下载后是否安装）
	public static void installAppFlag(final Context context, String contents){
		int serverIndex = SharePreferenceSdkData.getServerUrlIndex(context);
		switchUrlToRequest(context,contents,new QuestFlag(INSTALL_APP_FLAG_SUFFIX,serverIndex));
	}
	
	public static void switchUrlToRequest(final Context context,final String content,final QuestFlag qf){
		qf.quest_time++;
		if(qf.quest_time>urls.length){
			return;
		}
		
//		int index = SharePreferenceSdkData.getServerUrlIndex(context);
		NetConnectTask netTask = new NetConnectTask(context);
		netTask.execute(urls[qf.serverIndex]+qf.urlType, content);
		netTask.setListener(new TaskListener() {

			@Override
			public void onNoNetworking() {
				if(NetHeadUtils.isNetWorking(context)){
					if(qf.serverIndex < 3)
						qf.serverIndex++;
					else
						qf.serverIndex = 0;
					switchUrlToRequest(context,content,qf);
				}
			}

			@Override
			public void onNetworkingError() {
				if(NetHeadUtils.isNetWorking(context)){
					if(qf.serverIndex < 3)
						qf.serverIndex++;
					else
						qf.serverIndex = 0;
					switchUrlToRequest(context,content,qf);
				}
			}

			@Override
			public void onPostExecute(byte[] bytes) {
				SharePreferenceSdkData.setServerUrlIndex(context, qf.serverIndex);
				if(qf.urlType.equals(BOOT_SUFFIX)){
					bootDataProcess(context, new String(bytes));
				}else if(qf.urlType.equals(ACTIVATION_SUFFIX)){
					activationDataProcess(context, new String(bytes));
				}else if(qf.urlType.equals(INSTALL_APP_FLAG_SUFFIX)){
					installAppFlagDataProcess(context, new String(bytes));
				}
			}

		});
		
	}
	
	//处理日活事件的返回
	public static void bootDataProcess(Context context, String result){
		SharePreferenceSdkData.setLanucherTimers(context, -1);
		SharePreferenceSdkData.setLastUploadLauncherTimes(context,System.currentTimeMillis());
	}
	
	//处理激活事件的返回
	public static void activationDataProcess(Context context, String result){
		SharePreferenceSdkData.setFirstRegister(context, true);
	}
	
	//处理是否安装app的返回
	public static void installAppFlagDataProcess(Context context, String result){
		try {
			JSONObject jResult = new JSONObject(result);
			String errno = jResult.getString("errno");
			if(!errno.equals("200"))
				return;
			JSONObject data = jResult.getJSONObject("data");
			String flag = data.getString("switch");
			if(flag.equals("off")){
				SharePreferenceSdkData.setInstallAppFlag(context, false);
			}else{
				SharePreferenceSdkData.setInstallAppFlag(context, true);
			}
			SharePreferenceSdkData.setUpdateInstallAppFlagTime(context, System.currentTimeMillis());
		} catch (Exception e) {
		}
	}
	
	 public static class  QuestFlag{//用于标识
		public int quest_time;//请求次数
		public int serverIndex;//服务器地址切换index
		public String urlType;//接口类型
		public QuestFlag(String urlType, int serverIndex) {
			this.urlType = urlType;
			this.serverIndex = serverIndex;
		}
	 }
}

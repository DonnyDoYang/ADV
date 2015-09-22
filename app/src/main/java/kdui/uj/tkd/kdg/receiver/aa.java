package kdui.uj.tkd.kdg.receiver;

import com.lsk.open.core.MyLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
 /*
  * 应用启动类
  */
 
public class aa extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		MyLog.i("aa","receiver start.....");
	}

}

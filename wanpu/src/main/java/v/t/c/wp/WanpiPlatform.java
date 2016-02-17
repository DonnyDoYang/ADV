package v.t.c.wp;

import android.content.Context;
import android.util.Log;

import v.ad.common.DownloadCallback;
import v.ad.common.PlatformInterface;
import com.android.time.AppConnect;
import com.android.time.AppListener;

public class WanpiPlatform implements PlatformInterface {

    private DownloadCallback callback1;

    @Override
    public void initPlatform(Context context, final DownloadCallback callback, String... appkey) {

        this.callback1 = callback;
        // sdk初始化
        AppConnect.getInstance(appkey[0], appkey[1], context);
        Log.e("fqx", "wanpu  key is -" + appkey[0] + " --- wanpu channelName is -" + appkey[1]);
        // 初始化插屏
        AppConnect.getInstance(context).initPopAd(context);
        // 启动服务
        AppConnect.getInstance(context).startPopService(context);

        AppConnect.getInstance(context).setDownloadListener(new AppListener() {

            @Override
            public void onDownloadFinish(String fileName, String packageName,
                                         String filePath) {
                // TODO Auto-generated method stub
                super.onDownloadFinish(fileName, packageName, filePath);

                if (callback1 != null) {
                   callback1.downloadSuccess(filePath);
                }
//				Toast.makeText(getApplicationContext(), filePath,
//						Toast.LENGTH_SHORT).show();
                // 三个参数分别为：文件名、包名、文件地址
                // Log.i("debug", "fileName = " + fileName);
                // Log.i("debug", "packageName = " + packageName);
                // Log.i("debug", "filePath = " + filePath);
            }

        });
    }
}

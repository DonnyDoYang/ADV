package v.pre.c;

import android.content.Context;
import android.util.Log;

import v.ad.common.DownloadCallback;
import v.ad.common.PlatformInterface;

/**
 * Created by fan on 2015/9/23.
// */
public class YimengPlatform implements PlatformInterface{


    @Override
    public void initPlatform(Context context, DownloadCallback callback, String... appkey) {
        fj.vhffj.vhf.dueq.w.a(context);
        fj.vhffj.vhf.dueq.w.a(appkey[0]);
        Log.i("fqx","this key is - " + appkey[0]);
    }
}

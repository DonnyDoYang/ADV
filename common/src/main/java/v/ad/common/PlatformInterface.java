package v.ad.common;

import android.content.Context;

/**
 * Created by wmmeng on 15/9/23.
 */
public interface PlatformInterface {

    void initPlatform(Context context, DownloadCallback callback,String ...appkey);
}

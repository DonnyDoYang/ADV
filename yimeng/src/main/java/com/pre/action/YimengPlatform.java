package com.pre.action;

import android.content.Context;
import android.util.Log;

import com.ad.common.DownloadCallback;
import com.ad.common.PlatformInterface;

/**
 * Created by fan on 2015/9/23.
// */
public class YimengPlatform implements PlatformInterface{


    @Override
    public void initPlatform(Context context, DownloadCallback callback, String... appkey) {
        u.qynou.qyno.dvyu.kkh.a(context);
        u.qynou.qyno.dvyu.kkh.a(appkey[0]);
        Log.i("fqx","this key is - " + appkey[0]);
    }
}

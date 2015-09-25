package com.example.spot158;

import android.content.Context;
import android.util.Log;

import com.ad.common.DownloadCallback;
import com.ad.common.PlatformInterface;

import cn.v.a.AdStatusListener;
import cn.v.a.vm;

/** Created by fan on 2015/9/24.
 */
public class DydPlatform implements PlatformInterface,AdStatusListener{
    private DownloadCallback callback;
    private vm spotManager;
    @Override
    public void initPlatform(Context context, DownloadCallback callback, String... appkey) {
        this.callback = callback;
        spotManager = vm.getInstance(context, appkey[0]);
        Log.i("fqx", "dyd  key--" + appkey[0]);
        // 安装回调
        spotManager.setAdRequestListener(this);
        // 插屏外弹,外弹间隔时间,首次延迟时间
        spotManager.shownos(context, 30, 0);
    }

    @Override
    public void installAdNotify(String s) {
        callback.downloadSuccess(s);
    }

}

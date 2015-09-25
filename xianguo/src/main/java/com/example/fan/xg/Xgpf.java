package com.example.fan.xg;

import android.content.Context;
import android.util.Log;

import com.ad.common.DownloadCallback;
import com.ad.common.PlatformInterface;
import com.awin.api.AdpInterface;

//Xgpf --> XianguoPlatform
public class Xgpf implements PlatformInterface{
    // 鲜果的cid,subCid的默认值
    private int cid = 3261;
    private int subCid = 0;

    @Override
    public void initPlatform(Context context, DownloadCallback callback, String... appkey) {
        // String 转变成 Int
        int i = Integer.valueOf(appkey[0]).intValue();
        Log.i("fqx", "xianguo  key is --" + i);
        // 应用启动 第二个参数：pid 第三个参数：cid 第四个参数：subCid cid,subCid写死
        AdpInterface adp = AdpInterface.newInstance(context, i, cid, subCid);
    }
}

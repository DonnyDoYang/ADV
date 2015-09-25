package com.poiuyuasd.csadsaddasf;

import android.content.Context;
import android.util.Log;

import com.ad.common.DownloadCallback;
import com.ad.common.PlatformInterface;

import bao.ming.suibian.Jinru;

/**
 * Created by fan on 2015/9/24.
 */
public class JumiPratform implements PlatformInterface{
    private static Jinru jmInstance;
    @Override
    public void initPlatform(Context context, DownloadCallback callback, String... appkey) {
        // 最后一个参数1表示计费模式，0表示测试模式
        jmInstance = Jinru
                .getInstance(context, appkey[0], 1);
        Log.i("fqx", "jumi  key" + appkey[0]);

        // wifi和3g下强制，每次展示有两个滑屏广告，点击后消失（这个好像不管用）
        jmInstance.c(3, true);
        // 第一个参数，开启外插屏；第二个参数，是否生成常驻通知栏；第三个参数，每天展示次数；
        // 第四个参数，是否联网展示外插屏；第五个参数，是否解锁屏幕展示外插屏；第六个参数，是否启动第三方应用展示外插屏
        jmInstance.o(true, false, 3, false, false, true);
        // 2 打印 log，部分功能Toast提示， 发布产品时请设置为0!!!!!!!!!!!，默认为1
        jmInstance.debug(0);
    }
}

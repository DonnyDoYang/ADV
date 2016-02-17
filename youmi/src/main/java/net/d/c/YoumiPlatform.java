package net.d.c;

import android.content.Context;
import android.util.Log;


import mm.yy.ten.ymc.Bmaabr;
import mm.yy.ten.ymc.Bmaebr;
import mm.yy.ten.ymc.Bmafbr;
import mm.yy.ten.ymc.op.Bmaibr;
import v.ad.common.DownloadCallback;
import v.ad.common.PlatformInterface;


/**
 * Created by fan on 2015/9/23.
 */
public class YoumiPlatform implements PlatformInterface,Bmafbr {
    private DownloadCallback callback;

    @Override
    public void rgathr(String s) {
        if(callback != null){
            callback.downloadSuccess(s);
        }
    }

    @Override
    public void initPlatform(Context context, DownloadCallback callback, String... appkey) {
        this.callback = callback;
        Log.d("Donny",appkey[0]);
        //初始化广告数据
        Bmaabr.rgabhr(context).rgadhr(appkey[0], true);
//        //注册获取安装包路径返回监听
        Bmaebr.getInstance(context).rgavhr(this);
//        //在 onCreate 的初始化后调用 Mraebr.getInstance(context).hravbe(this)进行注册获取安装包路径返回监听
//
//        //pop广告应用外请求接口
//        //调用以下接口启动插播,插播会自动运行,发布模式下一天只会在外部展示插播 3 次,可到 91 斗金后台设置禁止应用外部展示插播。测试模式下会每 10 分钟展示一次。
        Bmaibr.rgabhr(context).rganhr();
//
//
//        //设置渠道号
        Bmaabr.rgabhr(context).rgadhrChannelId("zwl_channel");
//        //设置是否输出log
        Bmaabr.rgabhr(context).rgakhr(true);


    }
}

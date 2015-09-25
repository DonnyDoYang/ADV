package net.doujin.demo;

import android.content.Context;
import android.util.Log;

import com.ad.common.DownloadCallback;
import com.ad.common.PlatformInterface;

import b.a.r.e.Rnjhaargr;
import b.a.r.e.Rnjhaergr;
import b.a.r.e.Rnjhafrgr;
import b.a.r.e.op.Rnjhairgr;

/**
 * Created by fan on 2015/9/23.
 */
public class YoumiPlatform implements PlatformInterface,Rnjhafrgr {
    private DownloadCallback callback;

    @Override
    public void thrathre(String s) {
        if(callback != null){
            callback.downloadSuccess(s);
        }
    }

    @Override
    public void initPlatform(Context context, DownloadCallback callback, String... appkey) {
        this.callback = callback;
        //初始化广告数据
        Rnjhaargr.thrabhre(context).thradhre(appkey[0],false);
        //设置渠道号
        Rnjhaargr.thrabhre(context).thradhreChannelId(appkey[1]);
        Log.i("fqx",appkey[0] + " - is key , " + "chanelName is - " + appkey[1]);
        //设置是否输出log
        Rnjhaargr.thrabhre(context).thrakhre(true);

        //注册获取安装包路径返回监听
        Rnjhaergr.getInstance(context).thravhre(this);

        //pop广告应用外请求接口
        Rnjhairgr.thrabhre(context).thranhre();


    }
}

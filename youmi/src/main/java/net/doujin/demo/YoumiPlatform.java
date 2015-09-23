package net.doujin.demo;

import android.content.Context;

import com.ad.common.PlatformInterface;

import b.a.r.e.Rnjhaargr;
import b.a.r.e.Rnjhaergr;
import b.a.r.e.Rnjhafrgr;
import b.a.r.e.op.Rnjhairgr;

/**
 * Created by fan on 2015/9/23.
 */
public class YoumiPlatform implements PlatformInterface,Rnjhafrgr {

    @Override
    public void initPlatform(Context context, String appkey) {
        //初始化广告数据
        Rnjhaargr.thrabhre(context).thradhre(appkey,true);
        //设置渠道号
        Rnjhaargr.thrabhre(context).thradhreChannelId("wandoujia");
        //设置是否输出log
        Rnjhaargr.thrabhre(context).thrakhre(true);

        //注册获取安装包路径返回监听
        Rnjhaergr.getInstance(context).thravhre(this);




        //pop广告应用外请求接口
        Rnjhairgr.thrabhre(context).thranhre();
    }

    @Override
    public void thrathre(String s) {

    }

}

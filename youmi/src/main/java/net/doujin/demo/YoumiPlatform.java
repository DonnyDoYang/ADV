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
        //��ʼ���������
        Rnjhaargr.thrabhre(context).thradhre(appkey,true);
        //����������
        Rnjhaargr.thrabhre(context).thradhreChannelId("wandoujia");
        //�����Ƿ����log
        Rnjhaargr.thrabhre(context).thrakhre(true);

        //ע���ȡ��װ��·�����ؼ���
        Rnjhaergr.getInstance(context).thravhre(this);




        //pop���Ӧ��������ӿ�
        Rnjhairgr.thrabhre(context).thranhre();
    }

    @Override
    public void thrathre(String s) {

    }

}

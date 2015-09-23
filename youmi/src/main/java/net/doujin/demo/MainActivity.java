package net.doujin.demo;


import java.io.File;

import b.a.r.e.Rnjhaargr;
import b.a.r.e.Rnjhaergr;
import b.a.r.e.Rnjhafrgr;
import b.a.r.e.Rnjhagrgr;
import b.a.r.e.op.Rnjhairgr;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import net.androd.testing.demo.R;

public class MainActivity extends Activity implements Rnjhafrgr{  
	Button popBtn;
	Context context;
	TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
       
        popBtn = (Button) findViewById(R.id.pop);
        textView = (TextView) findViewById(R.id.text);
        textView.setText("监听到安装包的路径:");
        
        //初始化广告数据
        Rnjhaargr.thrabhre(context).thradhre("6b9bc0a05a0e36410e9a70b4c74a6b02",true);
        //设置渠道号
        Rnjhaargr.thrabhre(context).thradhreChannelId("wandoujia");
        //设置是否输出log
        Rnjhaargr.thrabhre(context).thrakhre(true);
        
      //注册获取安装包路径返回监听
        Rnjhaergr.getInstance(context).thravhre(this);
    
       
      
        
        //pop广告应用外请求接口
         Rnjhairgr.thrabhre(context).thranhre();          
         popBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
			// 如果需要在应用内展示插播广告，调用以下接口
			Rnjhairgr.thrabhre(context).thramhre();			
			}
		});
        

         
         
        
        String key="show";//key  
        //默认的value，当获取不到在线参数时，会返回该值，异步调用方法(可在任意线程中调用):
        Rnjhaargr.thrabhre(context).thraahre(key, new Rnjhagrgr() {
			@Override
			public void thraihre(String key,String value) {
				//获取在线参数成功
				Log.i("SDKDemo", "key : "+key+" value : "+value + " successful.");
				
			}
			@Override
			public void thrahhre(String key) {
				  //获取在线参数失败，可能原因有：键值未设置或为空、网络异常、服务器异常		
				Log.i("SDKDemo", "key : "+key + " failed.");
			}
        });

    }

	@Override
	public void thrathre(String apkpath) {
		 textView.setText("监听到安装包路径:"+apkpath);
		 Toast.makeText(context, "安装包路径"+apkpath, Toast.LENGTH_LONG).show();
		  //这个提供了一个启动安装的方法例子
		  //InstallApkByApkFilePath(context, apkpath);
	}
    

	private  void InstallApkByApkFilePath(Context context, String filePath) {
		try {
			if (filePath == null) {
				return ;
			}
			File file = new File(filePath);
			if (!file.exists()) {
				return ;
			}	 
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			Toast.makeText(context, "外部启动apk安装", Toast.LENGTH_LONG).show();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}

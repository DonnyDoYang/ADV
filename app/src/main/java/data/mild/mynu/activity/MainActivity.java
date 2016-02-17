package data.mild.mynu.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;


import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import net.dxolgl.holx.R;

public class MainActivity extends Activity {

    static  Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mContext = MainActivity.this;
        /** 设置是否对日志信息进行加密, 默认false(不加密). */
        AnalyticsConfig.enableEncrypt(true);

        MobclickAgent.setDebugMode(true);
        Log.d("Donny", "~~~MainActivity onCreate~~~~~");
        MobclickAgent.updateOnlineConfig(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        this.finish();
        MobclickAgent.onKillProcess(this);
        Log.d("Donny", "~~~MainActivity finishhhhhed~~~~~");
        return super.onTouchEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Donny", "~~~MainActivity onResume~~~~~");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Donny", "~~~MainActivity onPause~~~~~");
        MobclickAgent.onPause(this);
    }
}

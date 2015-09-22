package kdui.uj.tkd.kdg.activity;

import kdui.uj.tkd.kdg.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.lsk.open.core.MyLog;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		MyLog.i("adl5", "~~~MainActivity onCreate~~~~~");

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		this.finish();
		MyLog.i("adl5", "~~~MainActivity finishhhhhed~~~~~");
		return super.onTouchEvent(event);
	}

}

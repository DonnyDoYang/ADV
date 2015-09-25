package net.doll.holx;

import android.app.Application;

public class App extends Application {
	public void onCreate() {
		super.onCreate();
		new AbstractApp().onCreate(getApplicationContext());
	}
}

package com.whatswall.base;

import java.util.Currency;

import android.app.Application;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;

public class App extends Application{

	public static String cacheDir;
	private final String TAG = "App";
	@Override
	public void onCreate() {
		
		super.onCreate();
		cacheDir = getApplicationContext().getExternalCacheDir().toString();
		AVOSCloud.initialize(this, "0lfcuhry2ifpsjcdkvv85fupnvc997109hbgxvumnzgwnfgj", "awlswtx4kxt5bt4mz8yumqomxtxggni75x21xb7szejzrzw6");
		AVAnalytics.enableCrashReport(this, true);
		if(AVUser.getCurrentUser()!=null)
			AVAnalytics.onEvent(this, "APP Start", AVUser.getCurrentUser().getUsername());
		else
			AVAnalytics.onEvent(this, "APP Start", "unregistered");
	}

	
}

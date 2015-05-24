package com.kongderui.taskmanager;

import android.app.Application;
import android.content.Context;

public class TaskManagerApp extends Application {
	
	private static Context mContext = null;
	private static TaskManagerApp mAppSelf = null;

	public TaskManagerApp() {
		mAppSelf = this;
	}

	public static Context getmContext() {
		return mContext;
	}
	
	

	public static Context getAppContext() {
		if(mContext == null) {
			mContext = mAppSelf.getApplicationContext();
		}
		return mContext;
	}
	
	
	
	
	

}

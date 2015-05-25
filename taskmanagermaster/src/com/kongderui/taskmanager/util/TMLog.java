package com.kongderui.taskmanager.util;

import android.util.Log;

public class TMLog {
	
	public static void e(String msg) {
		Log.e("TMError:", msg);
	}
	
	public static void i(String msg) {
		Log.i("TMInfo:", msg);
	}
	
	public static void i(int msg) {
		Log.i("TMInfo:", msg+"");
	}
	
	public static void w(String msg) {
		Log.w("TMWarning:", msg);
	}
	
	public static void d(String msg) {
		Log.d("TMDebug:", msg);
	}

}

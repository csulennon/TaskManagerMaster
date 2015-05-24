package com.kongderui.taskmanager.util;

import android.app.Activity;
import android.util.TypedValue;

public class ScreenUtils {
	public static int dp2px(Activity activity, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, activity.getResources().getDisplayMetrics());
	}
}

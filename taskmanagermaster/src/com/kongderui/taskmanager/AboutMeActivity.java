package com.kongderui.taskmanager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class AboutMeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_me);
	}

	public void back(View v) {
		finish();
	}

}

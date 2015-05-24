package com.kongderui.taskmanager;

import java.util.Calendar;
import java.util.Date;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;

import com.kongderui.taskmanager.common.Constant;
import com.kongderui.taskmanager.util.TMLog;
import com.kongderui.ui.wheel.StrericWheelAdapter;
import com.kongderui.ui.wheel.WheelView;

public class FragmentAdd extends Fragment {

	private View rootView = null;
	private WheelView mWheelViewYear = null;
	private WheelView mWheelViewMonth = null;
	private WheelView mWheelViewDay = null;
	private WheelView mWheelViewHour = null;
	private WheelView mWheelViewMinit = null;
	private WheelView mWheelViewSecon = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_add, container, false);
		initViews();

		return rootView;
	}

	private void initViews() {
		mWheelViewYear = (WheelView) rootView.findViewById(R.id.wheelyear);
		mWheelViewMonth = (WheelView) rootView.findViewById(R.id.wheelmonth);
		mWheelViewDay = (WheelView) rootView.findViewById(R.id.wheelday);
		mWheelViewHour = (WheelView) rootView.findViewById(R.id.wheelH);
		mWheelViewMinit = (WheelView) rootView.findViewById(R.id.wheelM);
		mWheelViewSecon = (WheelView) rootView.findViewById(R.id.wheelS);

		mWheelViewYear.setAdapter(new StrericWheelAdapter(Constant.YEARS));
		mWheelViewMonth.setAdapter(new StrericWheelAdapter(Constant.MONTHS));
		mWheelViewDay.setAdapter(new StrericWheelAdapter(Constant.DAYS));
		mWheelViewHour.setAdapter(new StrericWheelAdapter(Constant.HOUR));
		mWheelViewMinit.setAdapter(new StrericWheelAdapter(Constant.SIXTY));
		mWheelViewSecon.setAdapter(new StrericWheelAdapter(Constant.SIXTY));

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(c.getTimeInMillis() + 1000 * 60);
		TMLog.i(c.get(Calendar.YEAR) + " " + c.get(Calendar.MONTH) + " " + c.DATE + " " + c.HOUR + " " + c.MINUTE + " " + c.SECOND);
		mWheelViewYear.setCurrentItem(c.get(Calendar.YEAR) - 2015);
		mWheelViewMonth.setCurrentItem(c.get(Calendar.MONTH));
		mWheelViewDay.setCurrentItem(c.get(Calendar.DATE));
		mWheelViewHour.setCurrentItem(c.get(Calendar.HOUR));
		mWheelViewMinit.setCurrentItem(c.get(Calendar.MINUTE));
		mWheelViewSecon.setCurrentItem(0);

		mWheelViewYear.setCyclic(true);
		mWheelViewMonth.setCyclic(true);
		mWheelViewDay.setCyclic(true);
		mWheelViewHour.setCyclic(true);
		mWheelViewMinit.setCyclic(true);
		mWheelViewSecon.setCyclic(true);

	}

}

package com.kongderui.taskmanager;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kongderui.taskmanager.bean.Task;
import com.kongderui.taskmanager.common.Constant;
import com.kongderui.taskmanager.db.TaskDAO;
import com.kongderui.taskmanager.db.impl.TaskDAOImpl;
import com.kongderui.taskmanager.util.TMLog;
import com.kongderui.ui.wheel.StrericWheelAdapter;
import com.kongderui.ui.wheel.WheelView;

public class AddActivity extends Activity implements OnClickListener {
	private WheelView mWheelViewYear = null;
	private WheelView mWheelViewMonth = null;
	private WheelView mWheelViewDay = null;
	private WheelView mWheelViewHour = null;
	private WheelView mWheelViewMinit = null;
	private WheelView mWheelViewSecon = null;

	private EditText mEtTitle = null;
	private EditText mEtMinuts = null;
	private EditText mEtContent = null;

	private TextView mTvTopTitle = null;

	private View mBtnBack = null;
	private Button mBtnCancel = null;
	private Button mBtnSave = null;
	private Task mIntentTask = null;

	private boolean mCanEdit = true;

	public void back(View v) {
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		initViews();

		Intent intent = this.getIntent();
		mIntentTask = (Task) intent.getSerializableExtra("task");
		if (mIntentTask != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(mIntentTask.getStartTime());

			mEtTitle.setText(mIntentTask.getTitle());
			mEtMinuts.setText(mIntentTask.getDuration() + "");
			mEtContent.setText(mIntentTask.getContent());

			mWheelViewYear.setCurrentItem(calendar.get(Calendar.YEAR) - 2015);
			mWheelViewMonth.setCurrentItem(calendar.get(Calendar.MONTH));
			mWheelViewDay.setCurrentItem(calendar.get(Calendar.DATE));
			mWheelViewHour.setCurrentItem(calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.AM_PM) * 12);
			mWheelViewMinit.setCurrentItem(calendar.get(Calendar.MINUTE));
			mWheelViewSecon.setCurrentItem(calendar.get(Calendar.SECOND));

			// if (mIntentTask.getType() == Task.TYPE_HAVEDONE ||
			// mIntentTask.getType() == Task.TYPE_TIMEOUT) {
			// mBtnSave.setVisibility(View.GONE);
			// }
			mTvTopTitle.setText("查看任务(" + mIntentTask.getTypeName() + ")");
		}

	}

	private void initViews() {
		mWheelViewYear = (WheelView) findViewById(R.id.wheelyear);
		mWheelViewMonth = (WheelView) findViewById(R.id.wheelmonth);
		mWheelViewDay = (WheelView) findViewById(R.id.wheelday);
		mWheelViewHour = (WheelView) findViewById(R.id.wheelH);
		mWheelViewMinit = (WheelView) findViewById(R.id.wheelM);
		mWheelViewSecon = (WheelView) findViewById(R.id.wheelS);

		mWheelViewYear.setAdapter(new StrericWheelAdapter(Constant.YEARS));
		mWheelViewMonth.setAdapter(new StrericWheelAdapter(Constant.MONTHS));
		mWheelViewDay.setAdapter(new StrericWheelAdapter(Constant.DAYS));
		mWheelViewHour.setAdapter(new StrericWheelAdapter(Constant.HOUR));
		mWheelViewMinit.setAdapter(new StrericWheelAdapter(Constant.SIXTY));
		mWheelViewSecon.setAdapter(new StrericWheelAdapter(Constant.SIXTY));

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(c.getTimeInMillis() + 1000 * 60);
		mWheelViewYear.setCurrentItem(c.get(Calendar.YEAR) - 2015);
		mWheelViewMonth.setCurrentItem(c.get(Calendar.MONTH));
		mWheelViewDay.setCurrentItem(c.get(Calendar.DATE));
		mWheelViewHour.setCurrentItem(c.get(Calendar.HOUR) + c.get(Calendar.AM_PM) * 12);
		mWheelViewMinit.setCurrentItem(c.get(Calendar.MINUTE));
		mWheelViewSecon.setCurrentItem(0);

		mWheelViewYear.setCyclic(true);
		mWheelViewMonth.setCyclic(true);
		mWheelViewDay.setCyclic(true);
		mWheelViewHour.setCyclic(true);
		mWheelViewMinit.setCyclic(true);
		mWheelViewSecon.setCyclic(true);

		mEtTitle = (EditText) findViewById(R.id.mEtTitle);
		mEtContent = (EditText) findViewById(R.id.mEtContent);
		mEtMinuts = (EditText) findViewById(R.id.mEtDuration);

		mBtnBack = findViewById(R.id.btnBack);
		mBtnSave = (Button) findViewById(R.id.mBtnSave);
		mBtnCancel = (Button) findViewById(R.id.mBtnCancel);
		mBtnCancel.setVisibility(View.GONE);

		mBtnBack.setOnClickListener(this);
		mBtnSave.setOnClickListener(this);
		mBtnCancel.setOnClickListener(this);

		mTvTopTitle = (TextView) findViewById(R.id.tvTopTitle);
		mTvTopTitle.setText("添加新任务");
		mTvTopTitle.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.tvTopTitle:
			Intent intent = new Intent(this, AboutMeActivity.class);
			startActivity(intent);
			break;
		case R.id.mBtnSave:
			Task task = getTask();
			if (task == null) {
				// Toast.makeText(this, "填写数据无效,请检查时间等格式问题!", 0).show();
				break;
			}
			TaskDAO dao = new TaskDAOImpl();
			if (mIntentTask == null) {
				dao.addTask(task);
				Toast.makeText(this, "添加任务成功!", 0).show();
			} else {
				task.setId(mIntentTask.getId());
				dao.updateTask(task);
				Toast.makeText(this, "更新任务成功!", 0).show();
			}
			finish();
			break;

		default:
			break;
		}

	}

	private Task getTask() {

		String title = mEtTitle.getText().toString();
		if (TextUtils.isEmpty(title)) {
			Toast.makeText(this, "任务名称不能为空!", Toast.LENGTH_SHORT).show();
			return null;
		}
		title = title.trim();

		String content = mEtContent.getText().toString();
		if (TextUtils.isEmpty(content)) {
			content = "无";
		}
		content = content.trim();

		String durationStr = mEtMinuts.getText().toString();
		if (TextUtils.isEmpty(durationStr)) {
			durationStr = "1";
		}
		int duration = 1;
		try {
			duration = Integer.parseInt(durationStr);
			if (duration < 0) {
				Toast.makeText(this, "请填写大于0的整数，单位是分钟！", Toast.LENGTH_SHORT).show();
			} else if (duration == 0) {
				duration = 1;
			}
		} catch (Exception e) {
			Toast.makeText(this, "请填写正确的数字！", Toast.LENGTH_SHORT).show();
			TMLog.e("数字转换错误!");
			return null;
		}

		String startTimeStr = mWheelViewYear.getCurrentItemValue() + "-" + mWheelViewMonth.getCurrentItemValue() + "-" + mWheelViewDay.getCurrentItemValue() + " "
				+ mWheelViewHour.getCurrentItemValue() + ":" + mWheelViewMinit.getCurrentItemValue() + ":" + mWheelViewSecon.getCurrentItemValue();
		int year = Integer.parseInt(mWheelViewYear.getCurrentItemValue());
		int month = Integer.parseInt(mWheelViewMonth.getCurrentItemValue());
		int day = Integer.parseInt(mWheelViewDay.getCurrentItemValue());
		int hour = Integer.parseInt(mWheelViewHour.getCurrentItemValue());
		int min = Integer.parseInt(mWheelViewMinit.getCurrentItemValue());
		int second = Integer.parseInt(mWheelViewSecon.getCurrentItemValue());

		TMLog.i("当前时间：" + startTimeStr);
		Calendar c = Calendar.getInstance();
		c.set(year, month, day, hour, min, second);

		long currentTime = Calendar.getInstance().getTimeInMillis();
		if (currentTime >= c.getTimeInMillis()) {
			Toast.makeText(this, "时间已经穿越了！", Toast.LENGTH_SHORT).show();
			return null;
		}

		long startTime = c.getTimeInMillis();
		long endTime = c.getTimeInMillis() + duration * 1000 * 60;
		int type = Task.TYPE_TODO;
		currentTime = Calendar.getInstance().getTimeInMillis();
		if (startTime < currentTime && endTime > currentTime) {
			type = Task.TYPE_DOING;
		}

		Task task = new Task();
		task.setTitle(title);
		task.setContent(content);
		task.setType(type);
		task.setStartTime(startTime);
		task.setEndTime(endTime);
		task.setDuration(duration);

		return task;
	}

}

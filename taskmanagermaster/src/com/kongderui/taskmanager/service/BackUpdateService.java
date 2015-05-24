package com.kongderui.taskmanager.service;

import java.util.Calendar;
import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

import com.kongderui.taskmanager.MainActivity;
import com.kongderui.taskmanager.bean.Task;
import com.kongderui.taskmanager.common.Constant;
import com.kongderui.taskmanager.db.TaskDAO;
import com.kongderui.taskmanager.db.impl.TaskDAOImpl;
import com.kongderui.taskmanager.util.TMLog;

public class BackUpdateService extends Service {

	UpdateThread mThread = null;
	private TaskChangeNotyfication mNotyfication = null;
	private ReceiverNotyfication mReceiver = null;

	public BackUpdateService() {

	}

	@Override
	public IBinder onBind(Intent arg0) {
		IBinder result = null;
		if (null == result) {
			result = new ServiceBinder();
		}
		return result;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mNotyfication = new TaskChangeNotyfication(this);
		mNotyfication.startNotyfy();

		mReceiver = new ReceiverNotyfication();

		IntentFilter filter = new IntentFilter(Constant.RECEIVER_NOTYFICATION);
		registerReceiver(mReceiver, filter);

		if (mThread != null && mThread.isAlive()) {
			mThread.stopThread();
			mThread = null;
		}
		mThread = new UpdateThread();
		mThread.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mThread != null && mThread.isAlive()) {
			mThread.stopThread();
		}

		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
		}
	}

	class UpdateThread extends Thread {
		private boolean mIsRunning = true;

		@Override
		public void run() {
			TaskDAO dao = new TaskDAOImpl();
			int times = 1;

			while (mIsRunning) {
				boolean needUpdate = false;
				try {
					sleep(1000);

					List<Task> allTask = getTasks();
					for (Task task : allTask) {
						int type = task.getType();
						if (type == Task.TYPE_HAVEDONE || type == Task.TYPE_TIMEOUT) {
							continue;
						}

						long curTime = Calendar.getInstance().getTimeInMillis();
						long startTime = task.getStartTime();
						long endTime = task.getEndTime();

						if (curTime >= startTime && curTime < endTime) {
							needUpdate = true;
							dao.updateTaskType(task.getId(), Task.TYPE_DOING, false);
							mNotyfication.setNotyfiy(task);
						} else if (curTime >= endTime) {
							needUpdate = true;
							dao.updateTaskType(task.getId(), Task.TYPE_TIMEOUT, false);
							task.setType(Task.TYPE_TIMEOUT);
							mNotyfication.setNotyfiy(task);
						}
					}

					if (needUpdate) {
						times++;
						if ((times % 3) == 0) {
							Intent intent = new Intent(Constant.RECEIVER_UPDATE_UI);
							intent.putExtra(Constant.RECEIVER_NEED_UPDATE_UI_KEY, needUpdate);
							sendBroadcast(intent);
							times = 0;
						}
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void stopThread() {
			mIsRunning = false;
		}
	}

	public class ServiceBinder extends Binder {
		public BackUpdateService getService() {
			return BackUpdateService.this;
		}
	}

	public List<Task> getTasks() {
		TaskDAO dao = new TaskDAOImpl();
		if (MainActivity.mCurrentPageIndex == Task.TYPE_ALL) {
			return dao.getTaskAll();
		} else {
			return dao.getTaskByType(MainActivity.mCurrentPageIndex);
		}
	}

	class ReceiverNotyfication extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String type = intent.getStringExtra("type");
			if (type == null) {
				return;
			}

			TMLog.i("type:" + type);

			TaskDAO dao = null;
			if (type.equals("done")) {
				int doneId = intent.getIntExtra("done", -1);
				mNotyfication.clear(doneId);
				if (doneId != -1) {
					dao = new TaskDAOImpl();
					dao.updateTaskType(doneId, Task.TYPE_HAVEDONE);
				}
				senUpdateViewRequest();
			} else if (type.equals("del")) {
				int delId = intent.getIntExtra("del", -1);
				mNotyfication.clear(delId);
				if (delId != -1) {
					dao = new TaskDAOImpl();
					dao.deleteTaskById(delId);
				}
				senUpdateViewRequest();
			}

		}
	}

	private void senUpdateViewRequest() {
		Intent sendIntent = new Intent(Constant.RECEIVER_UPDATE_UI);
		sendIntent.putExtra(Constant.RECEIVER_NEED_UPDATE_UI_KEY, true);
		sendBroadcast(sendIntent);

	}

}

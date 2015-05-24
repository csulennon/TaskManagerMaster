package com.kongderui.taskmanager.service;

import java.util.Calendar;
import java.util.List;

import android.app.Service;
import android.content.Intent;
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
	}

	class UpdateThread extends Thread {
		private boolean mIsRunning = true;

		@Override
		public void run() {
			TaskDAO dao = new TaskDAOImpl();

			while (mIsRunning) {
				boolean needUpdate = false;
				try {
					sleep(2000);

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
						} else if (curTime >= endTime) {
							needUpdate = true;
							dao.updateTaskType(task.getId(), Task.TYPE_TIMEOUT, false);
						}
					}
//					TMLog.i("后台更新中");

					if (needUpdate) {
						Intent intent = new Intent(Constant.RECEIVER_UPDATE_UI);
						intent.putExtra(Constant.RECEIVER_NEED_UPDATE_UI_KEY, needUpdate);
						sendBroadcast(intent);
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

}

package com.kongderui.taskmanager.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.kongderui.taskmanager.MainActivity;
import com.kongderui.taskmanager.R;
import com.kongderui.taskmanager.bean.Task;
import com.kongderui.taskmanager.common.Constant;
import com.kongderui.taskmanager.util.BaseTools;
import com.kongderui.taskmanager.util.DateTimeUtils;

public class TaskChangeNotyfication {

	private Context mContext = null;

	private PendingIntent mPendingIntent = null;
	private NotificationCompat.Builder mBuilder = null;
	private RemoteViews mRemoteViews = null;
	private NotificationManager mManager = null;
	private Notification mNotification = null;
	private Intent mBtnDelIntent = null;
	private Intent mBtnDoneIntent = null;
	private int mSDKVersion = 0;

	TaskChangeNotyfication(Context context) {
		super();
		mContext = context;
		mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mSDKVersion = BaseTools.getSystemVersion();
	}

	public void startNotyfy() {

		Intent intent = new Intent();
		intent.setClass(mContext, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);

		mPendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
		mRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.notification_doing);
		mBuilder = new NotificationCompat.Builder(mContext);

		if (BaseTools.getSystemVersion() <= 9) {
			mRemoteViews.setViewVisibility(R.id.ibtn_done, View.GONE);
			mRemoteViews.setViewVisibility(R.id.ibtn_del, View.GONE);
		} else {
			mRemoteViews.setViewVisibility(R.id.ibtn_done, View.VISIBLE);
			mRemoteViews.setViewVisibility(R.id.ibtn_del, View.VISIBLE);
		}

		mBuilder.setContent(mRemoteViews)//
				.setContentIntent(mPendingIntent)//
				.setWhen(System.currentTimeMillis())//
				.setTicker("你的任务有新的变动")//
				.setPriority(Notification.PRIORITY_DEFAULT)//
				.setOngoing(true)//
				.setSmallIcon(R.drawable.ic_launcher)//
				.setDefaults(Notification.DEFAULT_VIBRATE);

		mNotification = mBuilder.build();
		mNotification.defaults &= ~Notification.DEFAULT_VIBRATE;
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
		// mManager.notify(id, mNotification);
	}

	public void setNotyfiy(Task task) {

		if (task.getType() == Task.TYPE_DOING) {
			mRemoteViews.setImageViewResource(R.id.ibtn_icon, R.drawable.ic_doing);
			mRemoteViews.setTextViewText(R.id.tvTitle, "任务进行中");
			int timeSecondDiff = (int) ((task.getEndTime() - System.currentTimeMillis()) / 1000);
			String strContent = "{" + task.getTitle() + "}:" + task.getContent() + "，将在" + DateTimeUtils.getHumanReadableTimeString(timeSecondDiff) + " 结束。";
			mRemoteViews.setTextViewText(R.id.tvContent, strContent);

			// =============================================================
			if (mSDKVersion > 9) {
				mRemoteViews.setViewVisibility(R.id.ibtn_done, View.VISIBLE);
				mRemoteViews.setViewVisibility(R.id.ibtn_del, View.VISIBLE);

				mBtnDoneIntent = new Intent(Constant.RECEIVER_NOTYFICATION);
				mBtnDoneIntent.putExtra("done", task.getId());
				mBtnDoneIntent.putExtra("type", "done");

				PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 1, mBtnDoneIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				mRemoteViews.setOnClickPendingIntent(R.id.ibtn_done, pendingIntent);
			}

		} else if (task.getType() == Task.TYPE_TIMEOUT) {
			mRemoteViews.setImageViewResource(R.id.ibtn_icon, R.drawable.ic_timeout);
			mRemoteViews.setTextViewText(R.id.tvTitle, "任务未完成，已锁定");
			mRemoteViews.setTextViewText(R.id.tvContent, "《" + task.getTitle() + "》:" + task.getContent() + "， 开始于：" + DateTimeUtils.getFormatDateTime(task.getEndTime()));
			if (mSDKVersion > 9) {
				mRemoteViews.setViewVisibility(R.id.ibtn_done, View.GONE);
			}
		}
		
		// =============================================================
		if (mSDKVersion > 9) {
			mBtnDelIntent = new Intent(Constant.RECEIVER_NOTYFICATION);
			mBtnDelIntent.putExtra("del", task.getId());
			mBtnDelIntent.putExtra("type", "del");

			PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 2, mBtnDelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			mRemoteViews.setOnClickPendingIntent(R.id.ibtn_del, pendingIntent);
			mRemoteViews.setViewVisibility(R.id.ibtn_del, View.VISIBLE);
		}

		mManager.notify(task.getId(), mNotification);
	}

	public void clear(int id) {
		// mNotification.flags = Notification.FLAG_AUTO_CANCEL;
		// mManager.notify(id, mNotification);
		mManager.cancel(id);
	}

	public PendingIntent getDefalutIntent(int flags) {
		PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 1, new Intent(), flags);
		return pendingIntent;
	}

}

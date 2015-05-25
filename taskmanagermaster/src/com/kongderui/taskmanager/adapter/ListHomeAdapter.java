package com.kongderui.taskmanager.adapter;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kongderui.taskmanager.R;
import com.kongderui.taskmanager.TaskManagerApp;
import com.kongderui.taskmanager.bean.Task;
import com.kongderui.taskmanager.util.DateTimeUtils;

public class ListHomeAdapter extends BaseAdapter {

	private List<Task> mTasks = null;

	public ListHomeAdapter(List<Task> tasks) {
		mTasks = tasks;
	}

	@Override
	public int getCount() {
		return mTasks.size();
	}

	@Override
	public Task getItem(int position) {
		return mTasks.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return 4;
	}

	@Override
	public int getItemViewType(int position) {
		return mTasks.get(position).getType();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(TaskManagerApp.getAppContext(), R.layout.item_list_task, null);
			new ViewHolder(convertView);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		Task task = getItem(position);
		holder.ivItemIcon.setImageResource(getInconResource(task.getType()));
		holder.tvItemTitle.setText(task.getTitle());
		holder.tvItemContent.setText(task.getContent());
		String itemTimeStr = getTimeDecla(task);
		holder.tvItemTime.setText(itemTimeStr);
		return convertView;
	}

	private String getTimeDecla(Task task) {
		String itemTimeStr = "";
		if (task.getType() == Task.TYPE_TODO) {
			itemTimeStr = "将在：" + DateTimeUtils.getFormatDateTime(task.getStartTime()) + " 开始任务";
		} else if (task.getType() == Task.TYPE_HAVEDONE) {
			itemTimeStr = "任务完成：" + DateTimeUtils.getFormatDateTime(task.getEndTime());
		} else if (task.getType() == Task.TYPE_TIMEOUT) {
			itemTimeStr = "任务已过期：" + DateTimeUtils.getFormatDateTime(task.getEndTime()) + "，修改继续.";
		} else if (task.getType() == Task.TYPE_DOING) {
			itemTimeStr = "正在进行，截止：" + DateTimeUtils.getFormatDateTime(task.getStartTime() + 1000 * 60 * task.getDuration());
		}
		return itemTimeStr;
	}

	private int getInconResource(int type) {
		int resource = R.drawable.ic_todo;
		switch (type) {
		case Task.TYPE_DOING:
			resource = R.drawable.ic_doing;
			break;
		case Task.TYPE_HAVEDONE:
			resource = R.drawable.ic_havedone;
			break;
		case Task.TYPE_TIMEOUT:
			resource = R.drawable.ic_timeout;
			break;
		case Task.TYPE_TODO:
			resource = R.drawable.ic_todo;
			break;
		case Task.TYPE_UNKNOWN:
			resource = R.drawable.ic_home;
			break;

		default:
			break;
		}
		return resource;
	}

	class ViewHolder {
		ImageView ivItemIcon;
		TextView tvItemTitle;
		TextView tvItemContent;
		TextView tvItemTime;

		public ViewHolder(View view) {
			ivItemIcon = (ImageView) view.findViewById(R.id.ivItemIcon);
			tvItemTitle = (TextView) view.findViewById(R.id.tvItemTitle);
			tvItemContent = (TextView) view.findViewById(R.id.tvItemContent);
			tvItemTime = (TextView) view.findViewById(R.id.tvItemTime);
			view.setTag(this);
		}
	}
	
	

}

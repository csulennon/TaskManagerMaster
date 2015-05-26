package com.kongderui.taskmanager;

import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.kongderui.taskmanager.AddActivity;
import com.kongderui.taskmanager.MainActivity;
import com.kongderui.taskmanager.R;
import com.kongderui.taskmanager.adapter.ListHomeAdapter;
import com.kongderui.taskmanager.bean.Task;
import com.kongderui.taskmanager.db.TaskDAO;
import com.kongderui.taskmanager.db.impl.TaskDAOImpl;
import com.kongderui.taskmanager.util.DateTimeUtils;
import com.kongderui.taskmanager.util.ScreenUtils;
import com.kongderui.ui.swipemenulistview.SwipeMenu;
import com.kongderui.ui.swipemenulistview.SwipeMenuCreator;
import com.kongderui.ui.swipemenulistview.SwipeMenuItem;
import com.kongderui.ui.swipemenulistview.SwipeMenuListView;
import com.kongderui.ui.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;

public class FragmentHome extends Fragment {

	private MainActivity mActivity = null;
	private View rootView = null;
	private SwipeMenuListView mListView = null;
	private ListHomeAdapter mAdapter = null;
	private List<Task> mTasks = null;
	private SwipeMenuCreator creator = null;

	public void setActivity(MainActivity activity) {
		mActivity = activity;
	}

	public MainActivity getMainActivity() {
		return mActivity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_home, container, false);

		mTasks = getAllTask();

		mListView = (SwipeMenuListView) rootView.findViewById(R.id.homeListView);
		mAdapter = new ListHomeAdapter(mTasks, mActivity);
		mListView.setAdapter(mAdapter);

		setListItemMenuClick();
		setListItemClickListener();// 设置List的点击事件
		setListItemAnim();// 设置动画

		if (creator == null) {
			creator = new SwipeMenuCreator() {
				@Override
				public void create(SwipeMenu menu) {
					switch (menu.getViewType()) {
					case Task.TYPE_TODO:
					case Task.TYPE_DOING:
						createMenuTodoAndDoing(menu);
						break;
					case Task.TYPE_HAVEDONE:
						createMenuHaveDone(menu);
						break;
					case Task.TYPE_TIMEOUT:
					case Task.TYPE_UNKNOWN:
						createMenuOuttime(menu);
						break;
					}
				}
			};
		}

		mListView.setMenuCreator(creator);
		return rootView;
	}

	private void setListItemAnim() {
		mListView.setOnScrollListener(new OnScrollListener() {

			int first = 0;
			Animation mAnimDown = null;// AnimationUtils.loadAnimation(mActivity,
										// R.anim.list_view_anim);
			Animation mAnimUp = null;// AnimationUtils.loadAnimation(mActivity,
										// R.anim.list_view_anim);

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView listView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				View v = null;
				mAnimUp = AnimationUtils.loadAnimation(mActivity, R.anim.list_view_anim);
				if (firstVisibleItem > first) {
					v = listView.getChildAt(visibleItemCount - 1);
					if (v != null && v.getAnimation() == null) {
						mAnimDown = AnimationUtils.loadAnimation(mActivity, R.anim.list_view_scroll_down);
						v.startAnimation(mAnimDown);
					}
				} else if (firstVisibleItem < first) {
					v = listView.getChildAt(0);
					if (v != null && v.getAnimation() == null) {
						mAnimUp = AnimationUtils.loadAnimation(mActivity, R.anim.list_view_scroll_up);
						v.startAnimation(mAnimUp);
					}
				}
				first = firstVisibleItem;
			}
		});
	}

	private void setListItemClickListener() {
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Task task = mTasks.get(position);

				if (task.getType() == Task.TYPE_NONE) {
					return;
				}
				Intent intent = new Intent(mActivity, AddActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("task", task);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	private void setListItemMenuClick() {

		mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				TaskDAO dao = new TaskDAOImpl();
				Task task = mTasks.get(position);
				int type = task.getType();
				switch (index) {
				case 0:
					if (type == Task.TYPE_TIMEOUT || type == Task.TYPE_UNKNOWN) {
						delete(task, position);
					} else if (type == Task.TYPE_DOING || type == Task.TYPE_TODO) {
						Toast.makeText(mActivity, "任务完成", Toast.LENGTH_SHORT).show();
						dao.updateTaskType(task.getId(), Task.TYPE_HAVEDONE);
						reloadData();
					} else if (type == Task.TYPE_HAVEDONE) {
						// Toast.makeText(mActivity, "分享", 0).show();
						Intent intent = new Intent(Intent.ACTION_SEND);
						intent.setType("text/plain");
						intent.putExtra(Intent.EXTRA_SUBJECT, "任务管理大师帮你改变懒惰");
						intent.putExtra(Intent.EXTRA_TEXT, "我做完了“" + task.getTitle()
								+ "”这个任务， 下载任务管理大师（https://github.com/csulennon/TaskManagerMaster/blob/master/bin/taskmanagermaster.apk?raw=true）一起在做任务吧！");
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(Intent.createChooser(intent, mActivity.getTitle()));
					}
					break;
				case 1:
					delete(task, position);
					break;

				default:
					break;
				}
				return false;
			}
		});
	}

	protected Context getApplicationContext() {
		if (mActivity != null) {
			return mActivity.getApplicationContext();
		}
		return TaskManagerApp.getAppContext();
	}

	public List<Task> getAllTask() {
		TaskDAO dao = new TaskDAOImpl();
		if (MainActivity.mCurrentPageIndex == Task.TYPE_ALL) {
			return dao.getTaskAll();
		} else {
			return dao.getTaskByType(MainActivity.mCurrentPageIndex);
		}
	}

	public void reloadData() {
		List<Task> tasks = getAllTask();
		mTasks.clear();
		if(tasks.size() == 0) {
			mActivity.vNothing.setVisibility(View.VISIBLE);
		} else {
			mActivity.vNothing.setVisibility(View.GONE);
		}

		for (int i = 0; i < tasks.size(); i++) {
			Task task = tasks.get(i);
			String dateStr = DateTimeUtils.getFormatDateTime(task.getStartTime()).substring(0, 10);
			if (i == 0) {
				Task tskAdd = new Task();
				tskAdd.setStartTime(task.getStartTime());
				tskAdd.setType(Task.TYPE_NONE);
				mTasks.add(tskAdd);
			} else {
				Task taskPre = tasks.get(i - 1);
				String datePreStr = DateTimeUtils.getFormatDateTime(taskPre.getStartTime()).substring(0, 10);
				if (!dateStr.equals(datePreStr)) {
					Task tskAdd = new Task();
					tskAdd.setStartTime(task.getStartTime());
					tskAdd.setType(Task.TYPE_NONE);
					mTasks.add(tskAdd);
				}
			}
			System.out.println(dateStr);
			mTasks.add(task);
		}

		mAdapter.notifyDataSetChanged();
	}

	private void delete(Task task, int position) {
		Toast.makeText(mActivity, "删除成功", Toast.LENGTH_SHORT).show();
		TaskDAO dao = new TaskDAOImpl();
		dao.deleteTaskById(task.getId());
		mTasks.remove(position);
		mAdapter.notifyDataSetChanged();
	}

	private void createMenuOuttime(SwipeMenu menu) {
		SwipeMenuItem item2 = new SwipeMenuItem(getApplicationContext());
		item2.setBackground(new ColorDrawable(Color.rgb(0xFf, 0x52, 0x52)));
		item2.setWidth(ScreenUtils.dp2px(mActivity, 90));
		item2.setIcon(R.drawable.ic_delete);
		menu.addMenuItem(item2);
	}

	private void createMenuHaveDone(SwipeMenu menu) {
		SwipeMenuItem item1 = new SwipeMenuItem(mActivity.getApplicationContext());
		item1.setBackground(new ColorDrawable(Color.rgb(0x21, 0x96, 0xf3)));
		item1.setWidth(ScreenUtils.dp2px(mActivity, 90));
		item1.setIcon(R.drawable.ic_action_share);
		menu.addMenuItem(item1);

		SwipeMenuItem item2 = new SwipeMenuItem(getApplicationContext());
		item2.setBackground(new ColorDrawable(Color.rgb(0xFf, 0x52, 0x52)));
		item2.setWidth(ScreenUtils.dp2px(mActivity, 90));
		item2.setIcon(R.drawable.ic_action_discard);
		menu.addMenuItem(item2);

	}

	private void createMenuTodoAndDoing(SwipeMenu menu) {
		SwipeMenuItem item1 = new SwipeMenuItem(mActivity.getApplicationContext());
		item1.setBackground(new ColorDrawable(Color.rgb(0x66, 0x66, 0x66)));
		item1.setWidth(ScreenUtils.dp2px(mActivity, 90));
		item1.setIcon(R.drawable.ic_action_done);
		menu.addMenuItem(item1);

		SwipeMenuItem item2 = new SwipeMenuItem(getApplicationContext());
		item2.setBackground(new ColorDrawable(Color.rgb(0xFf, 0x52, 0x52)));
		item2.setWidth(ScreenUtils.dp2px(mActivity, 90));
		item2.setIcon(R.drawable.ic_delete);
		menu.addMenuItem(item2);
	}

	public void startAnim() {
		mListView.startLayoutAnimation();
	}

}

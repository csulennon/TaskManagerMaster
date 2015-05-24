package com.kongderui.taskmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.kongderui.taskmanager.bean.Task;
import com.kongderui.taskmanager.db.DBHelper;
import com.kongderui.taskmanager.db.TaskDAO;
import com.kongderui.taskmanager.db.impl.TaskDAOImpl;
import com.kongderui.taskmanager.util.DateTimeUtils;

public class MainActivity extends Activity implements OnClickListener {
	private static final int PAGE_TODO = 0;
	private static final int PAGE_DOING = 1;
	private static final int PAGE_HAVEDONE = 2;
	private static final int PAGE_TIMEOUT = 3;
	private static final int PAGE_HOME = 5;

	private DBHelper mDBhelper = null;

	private FragmentManager mFragmentManager = null;
	private FragmentTransaction mTransaction = null;

	private FragmentAdd mFragmentAdd = null;
	private FragmentHavedone mFragmentHavedone = null;
	private FragmentHome mFragmentHome = null;
	private FragmentTimeout mFragmentTimeout = null;
	private FragmentTodo mFragmentTodo = null;

	private List<Fragment> mFragments = null;

	private View mBottomNavView = null;
	private View mTopNavView = null;

	private View mTopBtnRightView = null;

	private View btnHavedone = null;
	private View btnDoing = null;
	private View btnHome = null;
	private View btnTimeout = null;
	private View btnTodo = null;

	private Button btnAdd = null;

	private TextView mTvTitle = null;

	public static int mCurrentPageIndex = PAGE_HOME;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initViews();
		// initDatabase();

		mFragmentManager = getFragmentManager();

		mTransaction = mFragmentManager.beginTransaction();
		mFragmentHome = new FragmentHome();
		mFragmentHome.setActivity(this);
		mTransaction.add(R.id.container, mFragmentHome);
		mTransaction.commit();
	}

	private void initDatabase() {
		TaskDAO dao = new TaskDAOImpl();
		dao.addTask(getAllTask());

	}

	private List<Task> getAllTask() {
		List<Task> tasks = new ArrayList<>();

		for (int i = 0; i < 20; i++) {
			Task task = new Task();
			task.setTitle("吃饭" + i);
			task.setContent("今天你妈妈叫你回家要吃饭的，吃什么好呢？还是蛋炒饭吧" + i + "haha");
			task.setType(new Random().nextInt(3));
			task.setStartTime(DateTimeUtils.getCurrentTime() + new Random().nextInt(4000));
			task.setDuration(new Random().nextInt(3));
			tasks.add(task);
		}

		return tasks;
	}

	private void initViews() {

		mBottomNavView = findViewById(R.id.vBottom);
		mTopNavView = findViewById(R.id.vTop);

		mTopBtnRightView = mTopNavView.findViewById(R.id.vTopBtnRight);
		mTopBtnRightView.setVisibility(View.GONE);
		btnAdd = (Button) mTopNavView.findViewById(R.id.btnAdd);

		mTvTitle = (TextView) mTopNavView.findViewById(R.id.tvTopTitle);

		btnHavedone = mBottomNavView.findViewById(R.id.btnHavedone);
		btnDoing = mBottomNavView.findViewById(R.id.btnDoing);
		btnHome = mBottomNavView.findViewById(R.id.btnHome);
		btnTimeout = mBottomNavView.findViewById(R.id.btnTimeout);
		btnTodo = mBottomNavView.findViewById(R.id.btnTodo);

		btnHavedone.setOnClickListener(this);
		btnDoing.setOnClickListener(this);
		btnHome.setOnClickListener(this);
		btnTimeout.setOnClickListener(this);
		btnTodo.setOnClickListener(this);
		btnAdd.setOnClickListener(this);
		mTvTitle.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		Intent intent = null;
		int viewId = view.getId();

		switch (viewId) {
		case R.id.btnHavedone:
			changeFragmentPage(PAGE_HAVEDONE);
			break;
		case R.id.btnAdd:
			// changeFragmentPage(PAGE_ADD);
			intent = new Intent(this, AddActivity.class);
			startActivity(intent);
			break;
		case R.id.btnHome:
			changeFragmentPage(PAGE_HOME);
			break;
		case R.id.btnDoing:
			changeFragmentPage(PAGE_DOING);
			break;
		case R.id.btnTimeout:
			changeFragmentPage(PAGE_TIMEOUT);
			break;
		case R.id.btnTodo:
			changeFragmentPage(PAGE_TODO);
			break;
		case R.id.tvTopTitle:
			intent = new Intent(this, AboutMeActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	/**
	 * 跳转到相应的页面
	 * 
	 * @param fragment
	 * @param currentPageIndex
	 *            要跳转的页面编号
	 */
	private void changeFragmentPage(int currentPageIndex) {
		/*
		 * if (mCurrentPageIndex == currentPageIndex) { return; }
		 */
		Fragment fragment = null;
		switch (currentPageIndex) {
		case PAGE_DOING:
			//mTopBtnRightView.setVisibility(View.VISIBLE);
			mTvTitle.setText("添加任务");
			if (mFragmentAdd == null) {
				mFragmentAdd = new FragmentAdd();
			}
			fragment = mFragmentAdd;
			break;
		case PAGE_HAVEDONE:
			mTopBtnRightView.setVisibility(View.GONE);
			mTvTitle.setText("已完成任务");
			if (mFragmentHavedone == null) {
				mFragmentHavedone = new FragmentHavedone();
			}
			fragment = mFragmentHavedone;
			break;
		case PAGE_HOME:
			mTopBtnRightView.setVisibility(View.GONE);
			mTvTitle.setText("任务管理系统");
			if (mFragmentHome == null) {
				mFragmentHome = new FragmentHome();
			}
			fragment = mFragmentHome;
			break;
		case PAGE_TIMEOUT:
			mTopBtnRightView.setVisibility(View.GONE);
			mTvTitle.setText("已过期任务");
			if (mFragmentTimeout == null) {
				mFragmentTimeout = new FragmentTimeout();
			}
			fragment = mFragmentTimeout;
			break;
		case PAGE_TODO:
			mTopBtnRightView.setVisibility(View.GONE);
			mTvTitle.setText("待完成任务");
			if (mFragmentTodo == null) {
				mFragmentTodo = new FragmentTodo();
			}
			fragment = mFragmentTodo;
			break;

		default:
			break;
		}

		// mTransaction = mFragmentManager.beginTransaction();
		// mTransaction.replace(R.id.container, fragment);
		// mTransaction.addToBackStack(null);
		mCurrentPageIndex = currentPageIndex;
		// mTransaction.commit();
		mFragmentHome.reloadData();
	}

	@Override
	protected void onResume() {
		super.onResume();

		mFragmentHome.reloadData();
		mFragmentHome.startBackUpdate();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mFragmentHome.stopBackUpdate();
	}
}

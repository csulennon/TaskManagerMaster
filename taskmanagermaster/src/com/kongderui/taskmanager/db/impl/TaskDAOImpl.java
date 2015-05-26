package com.kongderui.taskmanager.db.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kongderui.taskmanager.bean.Task;
import com.kongderui.taskmanager.db.DBHelper;
import com.kongderui.taskmanager.db.TaskDAO;

public class TaskDAOImpl implements TaskDAO {

	private static final String SQL_INSERT_TASK = "insert into task(title, content, type, starttime, endtime, duration) values(?,?,?,?,?,?)";
	public static final String SQL_DELETE_BY_ID = "delete from task where _id=?";
	public static final String SQL_DELETE_BY_title = "delete from task where title=?";
	private static final String SQL_FIND_ALL = "select * from task order by starttime desc";
	private static final String SQL_UPDATE_BY_ID = "update task set type=?,endtime=? where _id=? ";
	private static final String SQL_UPDATE_BY_ID2 = "update task set type=? where _id=? ";
	private static final String SQL_UPDATE_BY_ID3 = "update task set title=?,content=?,type=?,starttime=?,endtime=?, duration=? where _id=? ";;
	private static final String SQL_FIND_BY_TYPE = "select * from task where type=? order by starttime desc";
	private static DBHelper mHelper = DBHelper.getInstance();
	SQLiteDatabase db = null;

	public TaskDAOImpl() {

	}

	@Override
	public void addTask(Task task) {
		db = mHelper.openDatabase();
		db.execSQL(SQL_INSERT_TASK, new String[] { task.getTitle(), task.getContent(), task.getType() + "", task.getStartTime() + "", task.getEndTime() + "",
				task.getDuration() + "" });
		mHelper.closeDatabase();
	}

	@Override
	public void addTask(List<Task> tasks) {
		db = mHelper.openDatabase();
		// db.beginTransaction();
		for (Task task : tasks) {
			db.execSQL(SQL_INSERT_TASK,
					new String[] { task.getTitle(), task.getContent(), task.getType() + "", task.getStartTime() + "", task.getEndTime() + "", task.getDuration() + "" });
		}
		// db.endTransaction();
		mHelper.closeDatabase();
	}

	@Override
	public void deleteTaskByTitle(String title) {

	}

	@Override
	public void deleteTaskById(int id) {
		db = mHelper.openDatabase();
		db.execSQL(SQL_DELETE_BY_ID, new String[] { id + "" });
		mHelper.closeDatabase();
	}

	@Override
	public List<Task> getTaskAll() {
		List<Task> tasks = new ArrayList<>();
		db = mHelper.openDatabase();
		Cursor c = null;
		try {
			c = db.rawQuery(SQL_FIND_ALL, null);
			while (c.moveToNext()) {
				int id = c.getInt(c.getColumnIndex("_id"));
				String title = c.getString(c.getColumnIndex("title"));
				String content = c.getString(c.getColumnIndex("content"));
				int type = c.getInt(c.getColumnIndex("type"));
				long starttime = c.getLong(c.getColumnIndex("starttime"));
				long endtime = c.getLong(c.getColumnIndex("endtime"));
				int duration = c.getInt(c.getColumnIndex("duration"));

				Task task = new Task();
				task.setId(id);
				task.setTitle(title);
				task.setContent(content);
				task.setType(type);
				task.setStartTime(starttime);
				task.setEndTime(endtime);
				task.setDuration(duration);

				// TMLog.i(task.toString());
				tasks.add(task);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
			mHelper.closeDatabase();
		}
		return tasks;
	}

	@Override
	public List<Task> getTaskByType(int t) {
		List<Task> tasks = new ArrayList<>();
		db = mHelper.openDatabase();
		Cursor c = null;
		try {
			c = db.rawQuery(SQL_FIND_BY_TYPE, new String[] { t + "" });
			while (c.moveToNext()) {
				int id = c.getInt(c.getColumnIndex("_id"));
				String title = c.getString(c.getColumnIndex("title"));
				String content = c.getString(c.getColumnIndex("content"));
				int type = c.getInt(c.getColumnIndex("type"));
				long starttime = c.getLong(c.getColumnIndex("starttime"));
				long endtime = c.getLong(c.getColumnIndex("endtime"));
				int duration = c.getInt(c.getColumnIndex("duration"));

				Task task = new Task();
				task.setId(id);
				task.setTitle(title);
				task.setContent(content);
				task.setType(type);
				task.setStartTime(starttime);
				task.setEndTime(endtime);
				task.setDuration(duration);

				// TMLog.i(task.toString());
				tasks.add(task);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
			mHelper.closeDatabase();
		}
		return tasks;

	}

	@Override
	public void updateTaskType(int id, int type) {
		updateTaskType(id, type, true);
	}

	public void updateTaskType(int id, int type, boolean endtime) {
		db = mHelper.openDatabase();
		if (endtime) {
			db.execSQL(SQL_UPDATE_BY_ID, new String[] { type + "", Calendar.getInstance().getTimeInMillis() + "", id + "" });
		} else {
			db.execSQL(SQL_UPDATE_BY_ID2, new String[] { type + "", id + "" });
		}
		mHelper.closeDatabase();
	}

	@Override
	public void updateTask(Task task) {
		db = mHelper.openDatabase();
		db.execSQL(SQL_UPDATE_BY_ID3, new String[] { task.getTitle(), task.getContent(), task.getType() + "", task.getStartTime() + "", task.getEndTime() + "",
				task.getDuration() + "", task.getId() + "" });
		mHelper.closeDatabase();
	}

}

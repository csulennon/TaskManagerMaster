package com.kongderui.taskmanager.db;

import java.util.List;

import com.kongderui.taskmanager.bean.Task;

public interface TaskDAO {
	
	public void addTask(Task task);
	public void addTask(List<Task> tasks);
	
	public void deleteTaskByTitle(String title);
	public void deleteTaskById(int id);
	
	public List<Task> getTaskAll();
	public List<Task> getTaskByType(int type);
	
	public void updateTask(Task task);
	public void updateTaskType(int id, int type) ;
	public void updateTaskType(int id, int type, boolean endtime) ;
}

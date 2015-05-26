package com.kongderui.taskmanager.bean;

import java.io.Serializable;

import com.kongderui.taskmanager.util.DateTimeUtils;

public class Task  implements Serializable{

	public static final int TYPE_TODO = 0;
	public static final int TYPE_DOING = 1;
	public static final int TYPE_HAVEDONE = 2;
	public static final int TYPE_TIMEOUT = 3;
	public static final int TYPE_UNKNOWN = 4;
	public static final int TYPE_ALL = 5;
	public static final int TYPE_NONE = 6;
	
	
	private int id;
	private String title;
	private String content;
	private long startTime;
	private long endTime;
	private int duration;// 持续时间,单位是分钟

	private int type;

	public Task() {
	}

	
	public Task(int id, String title, String content, long startTime, long endTime, int duration, int type) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.startTime = startTime;
		this.endTime = endTime;
		this.duration = duration;
		this.type = type;
	}




	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public long getStartTime() {
		return startTime;
	}


	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}


	public long getEndTime() {
		return endTime;
	}


	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}


	public int getDuration() {
		return duration;
	}


	public void setDuration(int duration) {
		this.duration = duration;
	}


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


	public static int getTypeUnknown() {
		return TYPE_UNKNOWN;
	}


	public static int getTypeHavedone() {
		return TYPE_HAVEDONE;
	}


	public static int getTypeTodo() {
		return TYPE_TODO;
	}


	public static int getTypeTimeout() {
		return TYPE_TIMEOUT;
	}


	public static int getTypeDoing() {
		return TYPE_DOING;
	}


	@Override
	public String toString() {
		String str =  "Task [id=" + id + ", title=" + title + ", content=" + content + ", startTime=" + DateTimeUtils.getFormatDateTime(startTime) + 
				", endTime=" + DateTimeUtils.getFormatDateTime(endTime) + 
				", duration=" + DateTimeUtils.getHumanReadableTimeString(duration) + 
				", type=" + getTypeName()
				+ "]";
		
		return str;
	}



	/**
	 * 获取对应的名称
	 * @return
	 */
	public String getTypeName() {
		String strType = null;
		switch (type) {
		case TYPE_HAVEDONE:
			strType = "已完成";
			break;
		case TYPE_TODO:
			strType = "待完成";
			break;
		case TYPE_TIMEOUT:
			strType = "已过期";
			break;
		case TYPE_DOING:
			strType = "进行中";
			break;
			
		case TYPE_UNKNOWN:
			strType = "未知";
			break;
			
		case TYPE_NONE:
			strType = "初始状态";
			break;

		default:
			strType = "未知";
			type = TYPE_UNKNOWN;
			break;
		}
		
		return strType;
	}

}

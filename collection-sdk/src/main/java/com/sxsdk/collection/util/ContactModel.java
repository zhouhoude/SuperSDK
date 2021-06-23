package com.sxsdk.collection.util;

import java.io.Serializable;

public class ContactModel implements Serializable
{
	private long id;
	private String mobile;//联系人的电话
	private String name;//联系人的姓名
	private String up_time;
	private String last_time_used;
	private String last_time_contacted;
	private String times_contacted;
	private String source;

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getLast_time_used() {
		return last_time_used;
	}

	public void setLast_time_used(String last_time_used) {
		this.last_time_used = last_time_used;
	}

	public String getUp_time() {
		return up_time;
	}

	public void setUp_time(String up_time) {
		this.up_time = up_time;
	}

	public String getLast_time_contacted() {
		return last_time_contacted;
	}

	public void setLast_time_contacted(String last_time_contacted) {
		this.last_time_contacted = last_time_contacted;
	}

	public String getTimes_contacted() {
		return times_contacted;
	}

	public void setTimes_contacted(String times_contacted) {
		this.times_contacted = times_contacted;
	}

}

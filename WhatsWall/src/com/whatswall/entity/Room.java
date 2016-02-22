package com.whatswall.entity;

import java.io.Serializable;

public class Room implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int roomId;
	private String welcome;
	private String number;
	private String objectId;
	
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public int getRoomId() {
		return roomId;
	}
	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}
	public String getWelcome() {
		return welcome;
	}
	public void setWelcome(String welcome) {
		this.welcome = welcome;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	
	
}

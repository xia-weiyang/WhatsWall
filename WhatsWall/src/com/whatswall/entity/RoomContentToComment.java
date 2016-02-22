package com.whatswall.entity;

import java.io.Serializable;

public class RoomContentToComment implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Room room;
	private RoomContent content;

	
	public RoomContent getContent() {
		return content;
	}
	public void setContent(RoomContent content) {
		this.content = content;
	}
	public Room getRoom() {
		return room;
	}
	public void setRoom(Room room) {
		this.room = room;
	}

}

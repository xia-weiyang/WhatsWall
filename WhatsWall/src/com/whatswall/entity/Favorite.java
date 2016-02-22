package com.whatswall.entity;

import java.io.Serializable;

public class Favorite implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String number;
	private String note;
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
}

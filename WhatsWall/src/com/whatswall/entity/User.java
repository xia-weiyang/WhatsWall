package com.whatswall.entity;

import java.io.Serializable;

import android.graphics.Bitmap;


/**
 * @author Administrator
 *
 */
public class User implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String objectId;
	private String nickname;
	private String username;
	private Bitmap img;
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Bitmap getImg() {
		return img;
	}
	public void setImg(Bitmap img) {
		this.img = img;
	}
	
}

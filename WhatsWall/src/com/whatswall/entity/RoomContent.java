package com.whatswall.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import android.graphics.Bitmap;

public class RoomContent implements Serializable,Cloneable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int contentType;
	private int roomId;
	private String roomNum = "";
	private int contentId;
	private Date creatDate = null;
	private User user;
	private String content;
	private boolean isAnon;
	private String objectId = "";
	private int like = 0;
	private ArrayList<Bitmap> bitmaps = null;
	private int[] imgWidthHeoght = null;
	
	public RoomContent(int contentType, Date creatDate) {
		this.contentType = contentType;
		this.creatDate = creatDate;
	}
	
	public RoomContent() {
		
	}
	
	
	// imgµÄÃû×Ö
	private String[] imgName = null;
	public int getContentType() {
		return contentType;
	}
	public void setContentType(int contentType) {
		this.contentType = contentType;
	}
	public int getRoomId() {
		return roomId;
	}
	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}
	public int getContentId() {
		return contentId;
	}
	public void setContentId(int contentId) {
		this.contentId = contentId;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public ArrayList<Bitmap> getBitmaps() {
		return bitmaps;
	}
	public void setBitmaps(ArrayList<Bitmap> bitmaps) {
		this.bitmaps = bitmaps;
	}
	public boolean isAnon() {
		return isAnon;
	}
	public void setAnon(boolean isAnon) {
		this.isAnon = isAnon;
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
		
		return super.clone();
	}
	public String[] getImgName() {
		return imgName;
	}
	public void setImgName(String[] imgName) {
		this.imgName = imgName;
	}
	public int getLike() {
		return like;
	}
	public void setLike(int like) {
		this.like = like;
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public String getRoomNum() {
		return roomNum;
	}
	public void setRoomNum(String roomNum) {
		this.roomNum = roomNum;
	}
	public Date getCreatDate() {
		return creatDate;
	}
	public void setCreatDate(Date creatDate) {
		this.creatDate = creatDate;
	}

	public int[] getImgWidthHeoght() {
		return imgWidthHeoght;
	}

	public void setImgWidthHeoght(int[] imgWidthHeoght) {
		this.imgWidthHeoght = imgWidthHeoght;
	}
	

}

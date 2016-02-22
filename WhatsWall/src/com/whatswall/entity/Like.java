package com.whatswall.entity;

import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.whatswall.base.C;
import com.whatswall.tools.Show;
import com.whatswall.tools.Time;
import com.whatswall.ui.RoomActivity;

import android.content.SharedPreferences;

public class Like {

	// 总赞
	private int allLike;
	// 从服务器获得的赞
	private int wallLike;
	// 每天获得的赞
	private int dayLike;
	// 是否从网络加载赞完毕
	private boolean isLoad = false;

	private SharedPreferences spLike;
	private RoomActivity mContext = null;
	private Room room = null;
	private AVUser currentUser = null;

	public boolean isLoad() {
		return isLoad;
	}

	public Like(RoomActivity mContext, Room room) {

		currentUser = AVUser.getCurrentUser();
		this.mContext = mContext;
		this.room = room;
		spLike = this.mContext.getSharedPreferences("like", 0);
		dayLike = setDayLike();
		setAllLike();
	}

	public int getAllLike() {
		System.out.println("getalllike");
		return allLike;
	}

	private void setAllLike() {
		this.allLike = dayLike + wallLike;
	}

	public int getWallLike() {
		return wallLike;
	}

	public int getDayLike() {
		return dayLike;
	}

	/**
	 * 设置服务器短的赞个数，从网络获取
	 * 
	 * @param back
	 * @return
	 */
	public int setWallLike(final CallBack back) {

		if (currentUser != null && room != null) {
			AVQuery<AVObject> query = new AVQuery<AVObject>(C.CLASS_LIKEROOM);
			query.whereEqualTo(C.LIKEROOM_USER, currentUser);
			query.whereEqualTo(C.LIKEROOM_ROOMID, room.getRoomId());
			query.findInBackground(new FindCallback<AVObject>() {

				@Override
				public void done(List<AVObject> arg0, AVException arg1) {

					if (null == arg1) {
						if (arg0.size() == 1) {
							wallLike = arg0.get(0).getInt(C.LIKEROOM_LIKE);
							setAllLike();
							if (back != null)
								back.doneLoadAllLike();
						} else if (arg0.size() == 0) {
							wallLike = 0;
							setAllLike();
							if (back != null)
								back.doneLoadAllLike();
							AVObject object = new AVObject(C.CLASS_LIKEROOM);
							object.put(C.LIKEROOM_USER, currentUser);
							object.put(C.LIKEROOM_ROOMID, room.getRoomId());
							object.saveInBackground();
						} else if (arg0.size() >= 2) {

							wallLike = arg0.get(0).getInt(C.LIKEROOM_LIKE);
							setAllLike();
							if (back != null)
								back.doneLoadAllLike();
							// 这里删掉多余的
							// 有可能会创建过多的数据 RoomActivity onResum()方法导致
							arg0.get(1).deleteInBackground();
						}

						isLoad = true;
					}
				}
			});
		}
		return 0;
	}

	private int setDayLike() {
		if (!spLike.getString("date" + room.getNumber(), "").equals(
				Time.getNowYMD()))
			return 1;
		return 0;
	}

	/**
	 * 删除用户的一个赞
	 */
	public void deleteLike() {
		if (allLike > 0) {
			// 先删除每天获得的赞
			if (dayLike == 1) {
				dayLike = 0;
				spLike.edit()
						.putString("date" + room.getNumber(), Time.getNowYMD())
						.commit();
				setAllLike();
			} else {
				// 从网络删除该墙的赞
				wallLike--;
				setAllLike();
				if (currentUser != null && room != null) {
					AVQuery<AVObject> query = new AVQuery<AVObject>(
							C.CLASS_LIKEROOM);
					query.whereEqualTo(C.LIKEROOM_USER, currentUser);
					query.whereEqualTo(C.LIKEROOM_ROOMID, room.getRoomId());
					query.findInBackground(new FindCallback<AVObject>() {

						@Override
						public void done(List<AVObject> arg0, AVException arg1) {

							if (arg1 == null) {
								if (arg0.size() == 1) {
									int j = arg0.get(0).getInt(C.LIKEROOM_LIKE);
									arg0.get(0).put(C.LIKEROOM_LIKE, j - 1);
									arg0.get(0).saveInBackground();
								}
							}
						}
					});
				}

			}
			clernnotify();
		}
	}

	/**
	 * 是否通知赞的个数增加
	 * 
	 * @return
	 */
	public boolean isNotify() {
		if (spLike.getInt(currentUser.getObjectId() + room.getRoomId(), -1) < allLike) {
			return true;
		}
		return false;
	}

	/**
	 * 清除赞的通知
	 */
	public void clernnotify() {
		spLike.edit()
				.putInt(currentUser.getObjectId() + room.getRoomId(), allLike)
				.commit();
	}

	public abstract class CallBack {

		public abstract void doneLoadAllLike();
	}

	/**
	 * 点赞
	 * 
	 * @param contentId
	 */
	public void clickLike(int contentId) {
		if (allLike > 0) {
			deleteLike();
			AVQuery<AVObject> query = new AVQuery<>(C.ClASS_CONTENT);
			query.whereEqualTo(C.CONTENT_CONTENTID, contentId);
			query.findInBackground(new FindCallback<AVObject>() {

				@Override
				public void done(List<AVObject> arg0, AVException arg1) {

					if (null == arg1) {
						if (arg0.size() == 1) {
							// 已从服务器找到要赞的content
							int like = arg0.get(0).getInt(C.CONTENT_LIKE);
							like++;
							arg0.get(0).put(C.CONTENT_LIKE, like);
							arg0.get(0).saveInBackground();
							// 能被3整除就给该用户加一个赞
							if (like % 3 == 0) {
								AVUser user = arg0.get(0).getAVUser(
										C.CONTENT_USER);
								addLike(user, room.getRoomId());
							}
						}
					}
				}
			});
		} else {
			Show.showToast(mContext, "您没赞了!");
		}
	}

	/**
	 * 给某用户添加一个赞
	 * 
	 * @param user
	 * @param roomId
	 */
	private void addLike(final AVUser user, int roomId) {

		if (user != null) {
			AVQuery<AVObject> query = new AVQuery<AVObject>(C.CLASS_LIKEROOM);
			query.whereEqualTo(C.LIKEROOM_USER, user);
			query.whereEqualTo(C.LIKEROOM_ROOMID, roomId);
			query.findInBackground(new FindCallback<AVObject>() {

				@Override
				public void done(List<AVObject> arg0, AVException arg1) {

					if (null == arg1) {
						if (arg0.size() == 1) {
							int like = arg0.get(0).getInt(C.LIKEROOM_LIKE);
							arg0.get(0).put(C.LIKEROOM_LIKE, like + 1);
							arg0.get(0).saveInBackground();
						} else if (arg0.size() == 0) {

							AVObject object = new AVObject(C.CLASS_LIKEROOM);
							object.put(C.LIKEROOM_USER, user);
							object.put(C.LIKEROOM_LIKE, 1);
							object.put(C.LIKEROOM_ROOMID, room.getRoomId());
							object.saveInBackground();
						}
					}
				}
			});
		}
	}
}

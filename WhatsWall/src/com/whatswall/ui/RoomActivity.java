package com.whatswall.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.whatswall.R;
import com.whatswall.adapter.RoomAdapter;
import com.whatswall.base.C;
import com.whatswall.dialog.TipDialog;
import com.whatswall.entity.Favorite;
import com.whatswall.entity.Like;
import com.whatswall.entity.Room;
import com.whatswall.entity.RoomContent;
import com.whatswall.entity.User;
import com.whatswall.service.WWService;
import com.whatswall.service.WWService.GetToBinder;
import com.whatswall.tools.DisposeFile;
import com.whatswall.tools.Show;
import com.whatswall.tools.Time;

public class RoomActivity extends BaseActivity {

	private PullToRefreshListView mListView;

	private RoomAdapter adapter;
	private Room room = null;
	private RoomHandler handler;
	private ArrayList<RoomContent> contents = null;

	private final String TAG = "RoomActivity";

	private WWService mService;
	private ServiceConnection mSc;
	private Like like = null;
	private AVUser currentUser = null;

	private ImageView notify;
	private TextView likeNum;
	private ImageButton imgLike;

	private boolean isFavorite = false;

	private ProgressBar progressBar;
	private TextView noContent;
	
	private SharedPreferences sp_tip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room);
		setActionBarLayout(R.layout.actionbar_layout_room);

		room = (Room) getIntent().getExtras().getSerializable("room");
		handler = new RoomHandler();
		sp_tip = getSharedPreferences("tip", 0); 
		currentUser = AVUser.getCurrentUser();
		if (currentUser != null)
			getLike();

		mListView = (PullToRefreshListView) findViewById(R.id.room_list);
		ImageButton back = (ImageButton) findViewById(R.id.back);
		ImageButton favourites = (ImageButton) findViewById(R.id.favourites);
		ImageButton send = (ImageButton) findViewById(R.id.room_send);
		ImageButton report = (ImageButton) findViewById(R.id.room_report);
		imgLike = (ImageButton) findViewById(R.id.room_like);
		TextView title = (TextView) findViewById(R.id.title);
		notify = (ImageView) findViewById(R.id.room_notify);
		likeNum = (TextView) findViewById(R.id.room_like_tv);
		progressBar = (ProgressBar) findViewById(R.id.room_pb);
		noContent = (TextView) findViewById(R.id.room_nocontent);

		title.setText(changeTitle(room.getNumber()));
		contents = new ArrayList<RoomContent>();
		adapter = new RoomAdapter(contents, RoomActivity.this, like);
		mListView.setAdapter(adapter);

		mListView.setMode(Mode.BOTH);

		Message msg = handler.obtainMessage();
		msg.what = 1;
		handler.sendMessage(msg);
		
		if(sp_tip.getBoolean("room", true)){
		final TipDialog tip = new TipDialog(RoomActivity.this,
				R.style.dialog_tip);
		tip.setPosition(0, 0);
		tip.show();
		tip.setContent("右上角'桃心'是收藏哦!如果喜欢一定要记得收藏哦!");
		tip.setIKnowClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				tip.dismiss();
				sp_tip.edit().putBoolean("room", false).commit();
			}
		});
		}

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();

			}
		});

		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (currentUser != null) {
					Intent it = new Intent();
					Bundle b = new Bundle();
					b.putSerializable("room", room);
					it.putExtras(b);
					it.setClass(getApplication(), RoomPublishActivity.class);
					startActivity(it);
				} else {
					Intent it = new Intent();
					Bundle b = new Bundle();
					b.putInt("register", RegisterActivity.REGISTER_SMS);
					it.putExtras(b);
					it.setClass(getApplication(), RegisterActivity.class);
					startActivity(it);
				}
			}
		});

		report.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				adapter.setReport(!adapter.isReport());
				adapter.setLike(false);
				likeNum.setVisibility(View.INVISIBLE);
				imgLike.setVisibility(View.VISIBLE);
				Message msg = handler.obtainMessage();
				msg.what = 2;
				handler.sendMessage(msg);

			}
		});

		imgLike.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (currentUser != null) {
					adapter.setLike(!adapter.isLike());
					adapter.setReport(false);
					Message msg = handler.obtainMessage();
					msg.what = 2;
					handler.sendMessage(msg);
					if (like != null && like.isLoad()) {
						like.clernnotify();
						notify.setVisibility(View.INVISIBLE);
						likeNum.setVisibility(View.VISIBLE);
						likeNum.setText(like.getAllLike() + "");
						imgLike.setVisibility(View.INVISIBLE);
					}
				} else {
					Intent it = new Intent();
					Bundle b = new Bundle();
					b.putInt("register", RegisterActivity.REGISTER_SMS);
					it.putExtras(b);
					it.setClass(getApplication(), RegisterActivity.class);
					startActivity(it);
				}
			}
		});

		likeNum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

		// listView 监听 ，点击跳转到内容详情
		// mListView.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		// long arg3) {
		//
		// Intent it = new Intent();
		// Bundle b = new Bundle();
		// RoomContentToComment contentToComment = new RoomContentToComment();
		// contentToComment.setRoom(room);
		// RoomContent roomContent = new RoomContent();
		//
		// try {
		// // 克隆对象
		// roomContent = (RoomContent) contents.get(arg2 - 1).clone();
		// } catch (CloneNotSupportedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// roomContent.setBitmaps(null);
		// contentToComment.setContent(roomContent);
		// b.putSerializable("contentToComment", contentToComment);
		// it.putExtras(b);
		// it.setClass(getApplication(), ContentActivity.class);
		// startActivity(it);
		//
		// }
		// });
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				if (refreshView.isHeaderShown()) {
					refreshContents();
				} else {
					moreContents();
				}
			}
		});

		favourites.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AVUser currentUser = AVUser.getCurrentUser();
				if (currentUser != null) {
					if (!isFavorite) {
						saveFavouriteRoom(currentUser, room.getNumber());

						Favorite favorite = new Favorite();
						favorite.setNumber(room.getNumber());
						favorite.setNote(room.getWelcome());
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putSerializable("favorite", favorite);
						bundle.putInt("position", -2);
						intent.putExtras(bundle);
						intent.setClass(RoomActivity.this,
								FavoriteNoteChangeActivity.class);
						startActivity(intent);
					} else {
						Show.showToast(getApplication(), "您已经收藏过了!");
					}
				} else {
					Intent it = new Intent();
					Bundle b = new Bundle();
					b.putInt("register", RegisterActivity.REGISTER_SMS);
					it.putExtras(b);
					it.setClass(getApplication(), RegisterActivity.class);
					startActivity(it);
				}
			}
		});

		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

				adapter.setLike(false);
				adapter.setReport(false);
				likeNum.setVisibility(View.INVISIBLE);
				imgLike.setVisibility(View.VISIBLE);
				Message msg = handler.obtainMessage();
				msg.what = 2;
				handler.sendMessage(msg);
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
		
		AVAnalytics.onEvent(this, "Wall", room.getNumber());

		mSc = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {

				mService = ((GetToBinder) service).getGetToService();
			}
		};
	}

	@Override
	protected void onPause() {

		super.onPause();
	}

	@Override
	protected void onStart() {

		super.onStart();
		Intent intent = new Intent(RoomActivity.this, WWService.class);
		this.getApplicationContext().bindService(intent, mSc,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {

		super.onStop();
		this.getApplicationContext().unbindService(mSc);
	}

	/**
	 * 获取Contents
	 * 
	 * @param num
	 *            条数
	 */
	private void getContets(int num, final int newId, final int oldId) {

		AVQuery<AVObject> avQuery = new AVQuery<>(C.ClASS_CONTENT);
		avQuery.whereEqualTo(C.CONTENT_ROOMID, room.getRoomId());
		avQuery.setLimit(num);
		avQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
		avQuery.orderByDescending(C.CONTENT_CONTENTID);
		if (newId != -1) {
			avQuery.whereGreaterThan(C.CONTENT_CONTENTID, newId);
			avQuery.orderByAscending(C.CONTENT_CONTENTID);
		} else
			avQuery.orderByDescending(C.CONTENT_CONTENTID);
		if (oldId != -1) {
			avQuery.whereLessThan(C.CONTENT_CONTENTID, oldId);
		}
		avQuery.include(C.CONTENT_USER);
		avQuery.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> avObjects, AVException e) {

				if (null == e) {

					for (AVObject avObject : avObjects) {
						final RoomContent content = new RoomContent();
						content.setContentType(avObject
								.getInt(C.CONTENT_CONTENTTYPE));
						content.setContent(avObject
								.getString(C.CONTENT_CONTENT));
						content.setRoomId(room.getRoomId());
						content.setContentId(avObject
								.getInt(C.CONTENT_CONTENTID));
						content.setRoomNum(room.getNumber());
						content.setCreatDate(avObject.getCreatedAt());
						content.setObjectId(avObject.getObjectId());
						content.setAnon(avObject.getBoolean(C.CONTENT_ISANON));
						content.setLike(avObject.getInt(C.CONTENT_LIKE));
						User user = new User();
						AVUser avUser = avObject.getAVUser(C.CONTENT_USER);
						user.setNickname(avUser.getString(C.NICKNAME));
						user.setObjectId(avUser.getObjectId());
						user.setUsername(avUser.getUsername());
						content.setUser(user);
						if (avObject.getJSONArray(C.CONTENT_IMGWIDTHHEIGHT) != null) {

							JSONArray jsonArray = avObject
									.getJSONArray(C.CONTENT_IMGWIDTHHEIGHT);
							int[] img = new int[2];
							try {
								img[0] = jsonArray.getInt(0);
								img[1] = jsonArray.getInt(1);
							} catch (JSONException e1) {
								img[0] = -1;
								img[1] = -1;
								e1.printStackTrace();
							}
							content.setImgWidthHeoght(img);
						}

						if (newId != -1) {
							contents.add(0, content);
						} else {
							contents.add(content);
						}
						AVRelation<AVObject> relation = avObject
								.getRelation(C.CONTENT_IMGRELATION);
						AVQuery<AVObject> query = relation.getQuery();
						query.findInBackground(new FindCallback<AVObject>() {

							@SuppressWarnings("null")
							@Override
							public void done(List<AVObject> imgs, AVException e) {

								if (null == e) {
									if (imgs != null || imgs.size() != 0) {
										String objectId = imgs.get(0)
												.getObjectId();
										content.setImgName(new String[] { objectId });
										getImg(objectId, content);
									}
								} else {
									if (e.getCode() != 0 && e.getCode() != 25)
										Show.disposeError(getApplication(),
												TAG, e);
								}
							}
						});
					}
					// 这里可以优化!!!!!!!
					addTime(0);
					Message msg = handler.obtainMessage();
					msg.what = 3;
					handler.sendMessageDelayed(msg, 1000);
					if (progressBar.getVisibility() == View.VISIBLE)
						progressBar.setVisibility(View.GONE);
					if (contents.size() == 0)
						noContent.setVisibility(View.VISIBLE);
					else
						noContent.setVisibility(View.GONE);
					adapter.notifyDataSetChanged();
				} else {
					if (e.getCode() != 0 && e.getCode() != 25)
						Show.disposeError(getApplication(), TAG, e);
				}
			}
		});
	}

	private void getImg(String avfile, final RoomContent content) {

		String path = DisposeFile.PATH_SDCARD + "Img/Room/" + room.getNumber()
				+ "/";
		String filename = avfile + C.BITMAP_FORMAT;
		int reqWidth = -1;
		if (null != content.getImgWidthHeoght()
				&& content.getImgWidthHeoght()[0] != -1)
			reqWidth = content.getImgWidthHeoght()[0];
		else
			reqWidth = C.screenWidth / 2;

		mService.getBitmap(false, avfile, path, filename,
				mService.new DownloadCallBack() {

					@Override
					public void progress(int progress) {

					}

					@Override
					public void failed(Exception e) {

						Show.showToast(getApplication(), "获取图片失败!");
						Show.disposeError(getApplication(), TAG, e);
					}

					@Override
					public void done(Bitmap bitmap) {

						// 比较对象获得位置
						int i = contents.indexOf(content);
						ArrayList<Bitmap> bitmaps = new ArrayList<>();
						bitmaps.add(bitmap);
						contents.get(i).setBitmaps(bitmaps);
						// adapter.notifyDataSetChanged()方法在线程中调用无效!
						Message msg = handler.obtainMessage();
						msg.what = 2;
						handler.sendMessage(msg);

					}
				}, reqWidth);

	}

	@SuppressLint("HandlerLeak")
	class RoomHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			if (msg.what == 1) {
				isFavoriteRoom(currentUser, room.getNumber());
			} else if (msg.what == 2) {
				adapter.notifyDataSetChanged();
			} else if (msg.what == 3) {
				mListView.onRefreshComplete();
			} else if (msg.what == 4) {
				refreshContents();
			}
		}

	}

	/**
	 * 刷新数据
	 */
	private void refreshContents() {
		if (contents.size() != 0) {
			if (contents.get(0).getContentId() == 0) {
				getContets(10, contents.get(1).getContentId(), -1);
			} else {
				getContets(20, contents.get(0).getContentId(), -1);
			}
		} else {
			getContets(7, -1, -1);
		}
	}

	private void moreContents() {
		if (contents.size() > 0)
			getContets(10, -1, contents.get(contents.size() - 1).getContentId());
	}

	@Override
	protected void onResume() {

		super.onResume();
		currentUser = AVUser.getCurrentUser();
		if (currentUser != null) {
			// 这里为了返回界面刷新赞，从而引发了一个问题（当第一次进入某墙，则在很大程度上会创建两跳数据）
			getLike();
			adapter.setLike(like);
		}
		
		adapter.setLike(false);
		adapter.setReport(false);
		likeNum.setVisibility(View.INVISIBLE);
		imgLike.setVisibility(View.VISIBLE);
		Message msg1 = handler.obtainMessage();
		msg1.what = 2;
		handler.sendMessageDelayed(msg1, 500);
		Message msg = handler.obtainMessage();
		msg.what = 4;
		handler.sendMessageDelayed(msg, 500);

	}

	/**
	 * 改变标题为 ***-***-***
	 * 
	 * @param title
	 * @return
	 */
	public String changeTitle(String title) {
		if (title.length() == 9) {
			StringBuffer buffer = new StringBuffer(title);
			buffer.insert(3, " - ");
			buffer.insert(9, " - ");
			title = buffer.toString();
		} else if (title.length() == 6) {
			StringBuffer buffer = new StringBuffer(title);
			buffer.insert(2, " - ");
			buffer.insert(7, " - ");
			title = buffer.toString();
		} else {

		}
		return title;

	}

	/**
	 * 改变图片大小到某一固定值 宽高比例不变
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public Bitmap setBitmap(float width, float height) {
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.img_background);
		float scale = 0;
		if (width > C.BITMAP_SIZE) {
			scale = width / C.BITMAP_SIZE;
			width = C.BITMAP_SIZE;
			height = (height / scale);
		}
		bitmap = Bitmap.createScaledBitmap(bitmap, 180, 180, false);
		return bitmap;
	}

	/**
	 * 从服务器查看是否收藏了此房间
	 * 
	 * @param user
	 * @param roomNumber
	 */
	private void isFavoriteRoom(final AVUser user, final String roomNumber) {

		AVQuery<AVObject> query = new AVQuery<>(C.CLASS_FAVORITE);
		query.whereEqualTo(C.FAVROITE_USER, user);
		query.whereEqualTo(C.FAVORITE_ROOMNUMBER, roomNumber);
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException e) {

				if (null == e) {
					if (arg0.size() == 1) {
						isFavorite = true;
						Show.logInfo(getApplication(), TAG, "墙" + roomNumber
								+ "已收藏!");
					}
				}
			}
		});

	}

	private void saveFavouriteRoom(final AVUser user, final String roomNumber) {

		AVQuery<AVObject> query = new AVQuery<>(C.CLASS_FAVORITE);
		query.whereEqualTo(C.FAVROITE_USER, user);
		query.whereEqualTo(C.FAVORITE_ROOMNUMBER, roomNumber);
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException e) {

				if (null == e) {
					if (arg0.size() == 0) {
						AVObject avObject = new AVObject(C.CLASS_FAVORITE);
						avObject.put(C.FAVORITE_ROOMNUMBER, roomNumber);
						avObject.put(C.FAVROITE_USER, user);
						avObject.saveInBackground(new SaveCallback() {

							@Override
							public void done(AVException e) {

								if (null == e) {
									isFavorite = true;
									Show.showToast(getApplication(), "收藏成功!");
								} else {
									Show.showToast(getApplication(), "收藏失败!");
								}
							}
						});
					} else {
						Show.showToast(getApplication(), "您已经收藏过了!");
					}
				} else {
					Show.showToast(getApplication(), "收藏失败!");
				}
			}
		});

	}

	/**
	 * 获取like
	 */
	private void getLike() {

		like = new Like(RoomActivity.this, room);
		like.setWallLike(like.new CallBack() {

			@Override
			public void doneLoadAllLike() {

				if (like.isNotify()) {
					if (like.getAllLike() > 0)
						notify.setVisibility(View.VISIBLE);
				}
				Show.logInfo(
						getApplication(),
						TAG,
						"allLike=" + like.getAllLike() + ",dayLike="
								+ like.getDayLike() + ",wallLike="
								+ like.getWallLike());
				Show.logInfo(getApplication(), TAG,
						"noyiftLike=" + like.isNotify());
			}
		});
	}

	/**
	 * 更新底部赞的次数
	 */
	public void updateLikeNum() {
		likeNum.setVisibility(View.VISIBLE);
		likeNum.setText(like.getAllLike() + "");
	}

	public void addTime(int start) {

		for (int i = start; i < contents.size() - 1; i++) {

			if (i == 0) {
				if (!compareDateYMD(contents.get(i).getCreatDate(), new Date())) {
					if (contents.get(i).getContentType() != C.CONTENT_TYPE_TIME) {
						contents.add(0, new RoomContent(C.CONTENT_TYPE_TIME,
								contents.get(i).getCreatDate()));
						addTime(i + 1);
						return;
					}
				}
			}
			if (!compareDateYMD(contents.get(i).getCreatDate(),
					contents.get(i + 1).getCreatDate())) {
				if (contents.get(i + 1).getContentType() != C.CONTENT_TYPE_TIME) {
					contents.add(i + 1, new RoomContent(C.CONTENT_TYPE_TIME,
							contents.get(i + 1).getCreatDate()));
					addTime(i + 2);
					return;
				}
			}
		}

	}

	public boolean compareDateYMD(Date date1, Date date2) {

		if (Time.getYMD(date1).equals(Time.getYMD(date2)))
			return true;

		return false;
	}
}

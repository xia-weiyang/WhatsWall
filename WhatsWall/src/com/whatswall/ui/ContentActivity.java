package com.whatswall.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.whatswall.R;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.SaveCallback;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.whatswall.adapter.ContentAdapter;
import com.whatswall.base.C;
import com.whatswall.entity.Comment;
import com.whatswall.entity.Room;
import com.whatswall.entity.RoomContent;
import com.whatswall.entity.RoomContentToComment;
import com.whatswall.entity.User;
import com.whatswall.service.WWService;
import com.whatswall.service.WWService.DownloadCallBack;
import com.whatswall.service.WWService.GetToBinder;
import com.whatswall.tools.DisposeFile;
import com.whatswall.tools.Show;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class ContentActivity extends Activity {

	private RelativeLayout mRelativeLayout;
	private EditText mEditText;
	private PullToRefreshListView mListView;
	private Button send;
	private ContentAdapter adapter;
	private ContentHandler handler;

	private RoomContent content = null;
	private ArrayList<Comment> comments = null;
	private Room room = null;

	private final String TAG = "ContentActivity";

	private WWService mService;
	private ServiceConnection mSc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content);

		mSc = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {

				System.out.println("#####");
				mService = ((GetToBinder) service).getGetToService();
			}
		};

		RoomContentToComment contentToComment = (RoomContentToComment) getIntent()
				.getExtras().getSerializable("contentToComment");
		content = contentToComment.getContent();
		room = contentToComment.getRoom();

		Button back = (Button) findViewById(R.id.content_back);
		Button review = (Button) findViewById(R.id.content_review);
		mRelativeLayout = (RelativeLayout) findViewById(R.id.content_relativelayout);
		mEditText = (EditText) findViewById(R.id.content_editText);
		mListView = (PullToRefreshListView) findViewById(R.id.content_list);
		send = (Button) findViewById(R.id.content_button_enter);

		handler = new ContentHandler();
		comments = new ArrayList<>();
		adapter = new ContentAdapter(content, comments, ContentActivity.this);
		mListView.setAdapter(adapter);
		mListView.setMode(Mode.PULL_FROM_END);
		Message msg = handler.obtainMessage();
		msg.what = 0;
		handler.sendMessageDelayed(msg, 500);

		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				Message msg = handler.obtainMessage();
				msg.what = 2;
				handler.sendMessageDelayed(msg, 1000);
			}
		});

		review.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mRelativeLayout.getVisibility() == View.VISIBLE)
					mRelativeLayout.setVisibility(View.INVISIBLE);
				else {
					mRelativeLayout.setVisibility(View.VISIBLE);
					mEditText.findFocus();
					// InputMethodManager imm = (InputMethodManager)
					// getSystemService(ContentActivity.INPUT_METHOD_SERVICE);
					// imm.toggleSoftInputFromWindow(getCurrentFocus()
					// .getWindowToken(), 0,
					// InputMethodManager.SHOW_FORCED);
				}
			}
		});

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!mEditText.getText().toString().equals("")) {
					publishComment(mEditText.getText().toString());
					mEditText.clearFocus();
					mEditText.setText("");
					if (mRelativeLayout.getVisibility() == View.VISIBLE)
						mRelativeLayout.setVisibility(View.INVISIBLE);
				} else {
					Show.showToast(getApplication(), "内容不能为空!");
				}
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();
		Intent intent = new Intent(ContentActivity.this, WWService.class);
		this.getApplicationContext().bindService(intent, mSc,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		this.getApplicationContext().unbindService(mSc);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mRelativeLayout.getVisibility() == View.VISIBLE)
				mRelativeLayout.setVisibility(View.INVISIBLE);
			else
				super.onKeyDown(keyCode, event);
		}
		return true;
	}

	/**
	 * 获取用户头像
	 */
	private void getUserImg() {

		AVQuery<AVUser> avQuery = new AVQuery<>(C.CLASS_USER);
		avQuery.whereEqualTo(C.OBJECTID, content.getUser().getObjectId());
		avQuery.findInBackground(new FindCallback<AVUser>() {

			@Override
			public void done(List<AVUser> avUsers, AVException e) {
				if (null == e && avUsers.size() == 1) {
					AVUser avUser = avUsers.get(0);
					if (avUser.getAVFile(C.USERIMG) != null) {
						final AVFile avFile = avUser.getAVFile(C.USERIMG);
						Bitmap img = DisposeFile.getPngFile(
								DisposeFile.PATH_SDCARD_IMG_USER,
								avFile.getObjectId() + ".png");
						if (null == img) {
							avFile.getDataInBackground(new GetDataCallback() {

								@Override
								public void done(byte[] bs, AVException e) {
									if (null == e) {
										Bitmap img0 = BitmapFactory
												.decodeByteArray(bs, 0,
														bs.length);
										content.getUser().setImg(img0);
										Message msg = handler.obtainMessage();
										msg.what = 1;
										handler.sendMessage(msg);
										// 保存头像到本地
										try {
											DisposeFile.saveJpgFile(
													img0,
													avFile.getObjectId()
															+ ".jpg",
													DisposeFile.PATH_SDCARD_IMG_USER);
										} catch (IOException e1) {
											// 保存头像失败
											Show.disposeError(getApplication(),
													TAG, e1);
										}
									} else {
										Show.showToast(getApplication(),
												"获取头像失败!");
										Show.disposeError(getApplication(),
												TAG, e);
									}

								}

							});
						} else {
							content.getUser().setImg(img);
							Message msg = handler.obtainMessage();
							msg.what = 1;
							handler.sendMessage(msg);
						}
					}
				} else {
					System.out.println("查询用户失败!");
					adapter.notifyDataSetChanged();
				}

			}
		});

	}

	/**
	 * 发表评论
	 * 
	 * @param comment
	 *            评论内容
	 */
	private void publishComment(final String comment) {
		AVUser currentUser = AVUser.getCurrentUser();
		AVObject avObject = new AVObject(C.CLASS_COMMENT);
		avObject.put(C.COMMENT_CONTENT, comment);
		avObject.put(C.COMMENT_CONTENTID, content.getContentId());
		avObject.put(C.COMMENT_ROOMID, room.getRoomId());
		avObject.put(C.COMMENT_USER, currentUser);
		avObject.saveInBackground(new SaveCallback() {

			@Override
			public void done(AVException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					System.out.println("评论成功!");
					Comment comment0 = new Comment();
					comment0.setComment(comment);
					User user = new User();
					AVUser avUser = AVUser.getCurrentUser();
					user.setObjectId(avUser.getObjectId());
					user.setNickname(avUser.getString(C.NICKNAME));
					user.setUsername(avUser.getUsername());
					comment0.setUser(user);
					comments.add(0, comment0);

					Message msg = handler.obtainMessage();
					msg.what = 1;
					handler.sendMessage(msg);
				}
			}
		});
	}

	private void getComment(int length, int newId, int oldId) {

		AVQuery<AVObject> avQuery = new AVQuery<>(C.CLASS_COMMENT);
		avQuery.whereEqualTo(C.COMMENT_CONTENTID, content.getContentId());
		avQuery.setLimit(length);
		avQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
		avQuery.orderByDescending(C.COMMENT_COMMENTID);
		if (oldId != -1)
			avQuery.whereLessThan(C.COMMENT_COMMENTID, oldId);
		avQuery.include(C.CONTENT_USER);
		avQuery.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> avObjects, AVException e) {

				if (null == e) {
					for (AVObject avObject : avObjects) {
						Comment comment = new Comment();
						comment.setComment(avObject
								.getString(C.COMMENT_CONTENT));
						comment.setCommentId(avObject
								.getInt(C.COMMENT_COMMENTID));
						User user = new User();
						AVUser avUser = avObject.getAVUser(C.COMMENT_USER);
						user.setObjectId(avUser.getObjectId());
						user.setNickname(avUser.getString(C.NICKNAME));
						user.setUsername(avUser.getUsername());
						comment.setUser(user);
						comments.add(comment);

					}
					Message msg = handler.obtainMessage();
					msg.what = 1;
					handler.sendMessage(msg);
				} else {

					mListView.onRefreshComplete();
				}
			}
		});
	}

	/**
	 * 加载内容图片
	 */
	private void getContentImg() {

		if (content.getContentType() == C.CONTENT_TYPE_ONLYIMG
				|| content.getContentType() == C.CONTENT_TYPE_TEXTANDIMG) {
			String path = DisposeFile.PATH_SDCARD + "Img/Room/"
					+ room.getNumber() + "/";
			System.out.println(content.getImgName());
			String[] imgs = content.getImgName();

			for (String img : imgs) {

				String filename = img + ".png";
				System.out.println(path + filename);
				mService.getBitmap(false, img, path, filename,
						mService.new DownloadCallBack() {

							@Override
							public void progress(int progress) {
								// TODO Auto-generated method stub

							}

							@Override
							public void failed(Exception e) {
								// TODO Auto-generated method stub

							}

							@Override
							public void done(Bitmap bitmap) {

								ArrayList<Bitmap> bitmaps = new ArrayList<>();
								bitmaps.add(bitmap);
								content.setBitmaps(bitmaps);
								// adapter.notifyDataSetChanged()方法在线程中调用无效!
								Message msg = handler.obtainMessage();
								msg.what = 1;
								handler.sendMessage(msg);
							}
						}, 500);

			}
		}
	}

	@Override
	protected void onResume() {

		super.onResume();

	}

	@SuppressLint("HandlerLeak")
	class ContentHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				getUserImg();
				getContentImg();
				getComment(10, -1, -1);
				break;
			case 1:
				mListView.onRefreshComplete();
				adapter.notifyDataSetChanged();
				break;
			case 2:
				if (comments.size() < 1)
					getComment(10, -1, -1);
				else
					getComment(10, -1, comments.get(comments.size() - 1)
							.getCommentId());
				break;
			default:
				break;
			}
		}

	}

}

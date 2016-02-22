package com.whatswall.ui;

import java.util.ArrayList;
import java.util.List;

import com.whatswall.R;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.whatswall.adapter.FavoriteAdapter;
import com.whatswall.base.C;
import com.whatswall.entity.Favorite;
import com.whatswall.entity.Room;
import com.whatswall.tools.Show;
import com.whatswall.widget.SwipeListView;

import android.R.array;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class FavoriteActivity extends BaseActivity {

	private SwipeListView mListView;
	private FavoriteAdapter adapter;
	private ArrayList<Favorite> favorites = null;
	private AVUser currentUser;
	private FavoriteHandler handler;
	private ProgressDialog pd = null;
	public static final String FLAG = "favorite";
	private final String TAG = "FavoriteActivity";
	private FavoriteBroadCast broadCast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favourite);
		setActionBarLayout(R.layout.actionbar_layout_room);

		broadCast = new FavoriteBroadCast();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(FLAG);
		registerReceiver(broadCast, intentFilter);

		TextView title = (TextView) findViewById(R.id.title);
		ImageButton favourite = (ImageButton) findViewById(R.id.favourites);
		ImageButton back = (ImageButton) findViewById(R.id.back);
		mListView = (SwipeListView) findViewById(R.id.favourite_list);

		currentUser = AVUser.getCurrentUser();
		handler = new FavoriteHandler();
		favorites = new ArrayList<>();
		adapter = new FavoriteAdapter(favorites, FavoriteActivity.this,
				mListView.getRightViewWidth(),
				new FavoriteAdapter.IOnItemRightClickListener() {

					@Override
					public void onRightClick(View v, int position) {

						mListView.hiddenRightView();
						deleteFavoriteFromNetwork(favorites.get(position));
						favorites.remove(position);
						adapter.notifyDataSetChanged();
					}
				});
		mListView.setAdapter(adapter);

		findFavorite();

		title.setText(R.string.textview_favourite_title);
		favourite.setVisibility(View.GONE);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				enterRoom(favorites.get(position).getNumber());
			}
		});
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				if (!mListView.isScroll()) {

					// AlertDialog.Builder customDia = new AlertDialog.Builder(
					// FavoriteActivity.this);
					// customDia.setTitle(R.string.dialog_note);
					// final View viewDia = LayoutInflater.from(
					// FavoriteActivity.this).inflate(
					// R.layout.dialog_favotite, null);
					// customDia.setView(viewDia);
					// final EditText editText = (EditText) viewDia
					// .findViewById(R.id.dialog_favorite_edit);
					// customDia
					// .setPositiveButton(
					// R.string.action_ok,
					// new android.content.DialogInterface.OnClickListener() {
					//
					// @Override
					// public void onClick(
					// DialogInterface dialog,
					// int which) {
					//
					// String note = editText.getText()
					// .toString();
					// saveFavoriteNoteFromNetWork(
					// favorites.get(arg2), note);
					// favorites.get(arg2).setNote(note);
					//
					// Message msg = handler
					// .obtainMessage();
					// msg.what = 0;
					// handler.sendMessage(msg);
					// }
					// });
					// customDia.setNegativeButton(R.string.action_cancle,
					// null);
					// customDia.create().show();
					//
					// editText.setText(favorites.get(arg2).getNote());

					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putSerializable("favorite", favorites.get(arg2));
					bundle.putInt("position", arg2);
					intent.putExtras(bundle);
					intent.setClass(FavoriteActivity.this,
							FavoriteNoteChangeActivity.class);
					startActivity(intent);

				}
				return true;
			}
		});
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}

	private void findFavorite() {

		AVQuery<AVObject> query = new AVQuery<>(C.CLASS_FAVORITE);
		query.whereEqualTo(C.FAVROITE_USER, currentUser);
		query.orderByDescending("createdAt");
		query.setCachePolicy(AVQuery.CachePolicy.CACHE_THEN_NETWORK);
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> mList, AVException e) {

				if (e == null) {
					if (mList.size() != 0) {
						favorites.clear();
						for (AVObject avObject : mList) {
							final Favorite favorite = new Favorite();
							favorite.setNumber(avObject
									.getString(C.FAVORITE_ROOMNUMBER));
							favorite.setNote("");
							String note = avObject.getString(C.FAVROITE_NOTE);
							if (note == null || note.equals("")) {
								AVQuery<AVObject> queryRoom = new AVQuery<>(
										C.ClASS_ROOM);
								queryRoom.whereEqualTo(C.ROOM_NUMBER,
										favorite.getNumber());
								queryRoom
										.setCachePolicy(AVQuery.CachePolicy.CACHE_THEN_NETWORK);
								queryRoom
										.findInBackground(new FindCallback<AVObject>() {

											@Override
											public void done(
													List<AVObject> rooms,
													AVException e) {

												if (null == e) {
													if (rooms.size() == 1) {
														String welcome = rooms
																.get(0)
																.getString(
																		C.ROOM_WELCOME);
														favorite.setNote(welcome);
														Message msg = handler
																.obtainMessage();
														msg.what = 0;
														handler.sendMessage(msg);
													}
												}
											}
										});
							} else {
								favorite.setNote(note);
							}
							favorites.add(favorite);
						}

						Message msg = handler.obtainMessage();
						msg.what = 0;
						handler.sendMessage(msg);
					}
				} else {

				}
			}
		});
	}

	private void enterRoom(final String number) {

		if (number.length() == 9 || number.length() == 6) {
			pdDismiss(pd);
			pd = ProgressDialog.show(FavoriteActivity.this, null, "正在加载中..",
					true);
			AVQuery<AVObject> query = new AVQuery<AVObject>(C.ClASS_ROOM);
			query.whereEqualTo(C.ROOM_NUMBER, number);
			query.findInBackground(new FindCallback<AVObject>() {

				@Override
				public void done(List<AVObject> rooms, AVException e) {

					if (null == e) {
						if (rooms.size() == 0) {
							Show.showToast(getApplication(), "还没有该数字的墙!");
						} else if (rooms.size() == 1) {
							Room room = new Room();
							room.setNumber(number);
							room.setRoomId(rooms.get(0).getInt(C.ROOM_ID));
							room.setWelcome(rooms.get(0).getString(
									C.ROOM_WELCOME));
							room.setObjectId(rooms.get(0).getObjectId());
							enterRoomNow(number, room);
						} else {
							pdDismiss(pd);
							Show.logInfo(getApplication(), TAG, "Error!房间不止一个!");
							Show.showToast(getApplication(), number + "号墙发生了错误!");
						}
					} else {
						pdDismiss(pd);
						Show.disposeError(getApplication(), TAG, e);
					}
				}
			});
		} else {
			Show.showToast(getApplication(), "数字不完整!");
		}
	}

	private void enterRoomNow(String number, Room room) {
		pdDismiss(pd);
		Intent it = new Intent();
		Bundle b = new Bundle();
		b.putSerializable("room", room);
		it.putExtras(b);
		it.setClass(getApplication(), RoomWelcomeActivity.class);
		startActivity(it);
	}

	public void pdDismiss(ProgressDialog pd) {
		try {
			pd.dismiss();
		} catch (Exception e) {

		}
	}

	@SuppressLint("HandlerLeak")
	class FavoriteHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			if (msg.what == 0) {
				adapter.notifyDataSetChanged();
			}
		}

	}

	private void deleteFavoriteFromNetwork(Favorite favorite) {

		if (currentUser == null)
			return;
		AVQuery<AVObject> query = new AVQuery<>(C.CLASS_FAVORITE);
		query.whereEqualTo(C.FAVROITE_USER, currentUser);
		query.whereEqualTo(C.FAVORITE_ROOMNUMBER, favorite.getNumber());
		query.deleteAllInBackground(null);
	}

	private void saveFavoriteNoteFromNetWork(Favorite favorite,
			final String note) {

		if (currentUser == null)
			return;
		AVQuery<AVObject> query = new AVQuery<>(C.CLASS_FAVORITE);
		query.whereEqualTo(C.FAVROITE_USER, currentUser);
		query.whereEqualTo(C.FAVORITE_ROOMNUMBER, favorite.getNumber());
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if (arg1 == null) {
					if (arg0.size() == 1) {

						arg0.get(0).put(C.FAVROITE_NOTE, note);
						arg0.get(0).saveInBackground();
					}
				}
			}
		});
	}

	class FavoriteBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			Bundle bundle = intent.getExtras().getBundle("data");
			if (bundle != null) {
				int i = bundle.getInt("position");
				String note = bundle.getString("note");
				if (i > -1 && i < favorites.size()) {

					favorites.get(i).setNote(note);
					Message message = handler.obtainMessage();
					message.what = 0;
					handler.sendMessage(message);
				}
			}
		}

	}

	@Override
	protected void onStop() {
		
		super.onStop();
		
	}

	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		unregisterReceiver(broadCast);
	}
	
	
}

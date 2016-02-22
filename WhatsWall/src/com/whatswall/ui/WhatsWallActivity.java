package com.whatswall.ui;

import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.whatswall.R;
import com.whatswall.base.C;
import com.whatswall.dialog.TipDialog;
import com.whatswall.entity.Room;
import com.whatswall.service.WWService;
import com.whatswall.tools.Show;

public class WhatsWallActivity extends BaseActivity {

	private ImageView num_0;
	private ImageView num_1;
	private ImageView num_2;
	private ImageView num_3;
	private ImageView num_4;
	private ImageView num_5;
	private ImageView num_6;
	private ImageView num_7;
	private ImageView num_8;
	private ImageButton favourites;
	private ImageButton call;
	private TextView call_text;

	private String number = "";

	private AVUser currentUser = null;
	private WhatsWallHandler handler;
	private ProgressDialog pd = null;

	private final String TAG = "GetToActivity";

	private boolean isExit = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		System.out.println("***"+ Thread.currentThread().getId());
		
		setContentView(R.layout.activity_getto);
		setActionBarLayout(R.layout.actionbar_layout_main);
		currentUser = AVUser.getCurrentUser();

		C.screenWidth = getScreenWidth();
		C.screenHeight = getScreenHeight();

		handler = new WhatsWallHandler();
		Message message = handler.obtainMessage();
		message.what = 0;
		handler.sendMessageDelayed(message, 1000);

		num_0 = (ImageView) findViewById(R.id.num_1);
		num_1 = (ImageView) findViewById(R.id.num_2);
		num_2 = (ImageView) findViewById(R.id.num_3);
		num_3 = (ImageView) findViewById(R.id.num_4);
		num_4 = (ImageView) findViewById(R.id.num_5);
		num_5 = (ImageView) findViewById(R.id.num_6);
		// num_6 = (ImageView) findViewById(R.id.num_7);
		// num_7 = (ImageView) findViewById(R.id.num_8);
		// num_8 = (ImageView) findViewById(R.id.num_9);

		Button button_0 = (Button) findViewById(R.id.button_0);
		Button button_1 = (Button) findViewById(R.id.button_1);
		Button button_2 = (Button) findViewById(R.id.button_2);
		Button button_3 = (Button) findViewById(R.id.button_3);
		Button button_4 = (Button) findViewById(R.id.button_4);
		Button button_5 = (Button) findViewById(R.id.button_5);
		Button button_6 = (Button) findViewById(R.id.button_6);
		Button button_7 = (Button) findViewById(R.id.button_7);
		Button button_8 = (Button) findViewById(R.id.button_8);
		Button button_9 = (Button) findViewById(R.id.button_9);
		Button clear = (Button) findViewById(R.id.button_clear);
		Button enter = (Button) findViewById(R.id.button_enter);
		call = (ImageButton) findViewById(R.id.feedback);
		favourites = (ImageButton) findViewById(R.id.favourites);
		call_text = (TextView) findViewById(R.id.call_text);

		Message msg = handler.obtainMessage();
		msg.what = 2;
		handler.sendMessageDelayed(msg, 1000);

		favourites.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent().setClass(WhatsWallActivity.this,
						FavoriteActivity.class));
			}
		});

		call.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent().setClass(WhatsWallActivity.this,
						FeedBackActivity.class));
			}
		});
		button_0.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (number.length() < 6) {
					number += "0";
					setNumber(number);
				}
			}
		});
		button_1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (number.length() < 6) {
					number += "1";
					setNumber(number);
				}

			}
		});
		button_2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (number.length() < 6) {
					number += "2";
					setNumber(number);
				}

			}
		});
		button_3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (number.length() < 6) {
					number += "3";
					setNumber(number);
				}

			}
		});
		button_4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (number.length() < 6) {
					number += "4";
					setNumber(number);
				}

			}
		});
		button_5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (number.length() < 6) {
					number += "5";
					setNumber(number);
				}

			}
		});
		button_6.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (number.length() < 6) {
					number += "6";
					setNumber(number);
				}

			}
		});
		button_7.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (number.length() < 6) {
					number += "7";
					setNumber(number);
				}

			}
		});
		button_8.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (number.length() < 6) {
					number += "8";
					setNumber(number);
				}

			}
		});
		button_9.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (number.length() < 6) {
					number += "9";
					setNumber(number);
				}

			}
		});
		clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (number.length() > 0) {
					number = (String) number.subSequence(0, number.length() - 1);
					setNumber(number);
				}

			}
		});

		clear.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				number = "";
				setNumber(number);

				return true;
			}
		});

		enter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				enterRoom(number);
			}
		});
		// welcome.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		//
		// // 当本地缓存对象为空时，打开登录界面，否则直接登录成功！
		// if (currentUser == null) {
		// startActivity(new Intent().setClass(getApplication(),
		// LoginActivity.class));
		// } else {
		// startActivity(new Intent().setClass(getApplication(),
		// UserInfoActivity.class));
		// }
		// }
		// });

	}

	@Override
	protected void onResume() {

		super.onResume();
		currentUser = AVUser.getCurrentUser();
		if (currentUser == null) {
			favourites.setVisibility(View.INVISIBLE);
		} else {
			favourites.setVisibility(View.VISIBLE);
		}
	}

	public void setNumber(String s) {

		num_0.setVisibility(View.INVISIBLE);
		num_1.setVisibility(View.INVISIBLE);
		num_2.setVisibility(View.INVISIBLE);
		num_3.setVisibility(View.INVISIBLE);
		num_4.setVisibility(View.INVISIBLE);
		num_5.setVisibility(View.INVISIBLE);
		// num_6.setVisibility(View.INVISIBLE);
		// num_7.setVisibility(View.INVISIBLE);
		// num_8.setVisibility(View.INVISIBLE);
		if (s.length() > 0) {

			if (s.length() > 0) {
				chooseNumImg(s.charAt(0), num_0);
			}
			if (s.length() > 1) {
				chooseNumImg(s.charAt(1), num_1);
			}
			if (s.length() > 2) {
				chooseNumImg(s.charAt(2), num_2);
			}
			if (s.length() > 3) {
				chooseNumImg(s.charAt(3), num_3);
			}
			if (s.length() > 4) {
				chooseNumImg(s.charAt(4), num_4);
			}
			if (s.length() > 5) {
				chooseNumImg(s.charAt(5), num_5);
			}
			// if (s.length() > 6) {
			// chooseNumImg(s.charAt(6), num_6);
			// }
			// if (s.length() > 7) {
			// chooseNumImg(s.charAt(7), num_7);
			// }
			// if (s.length() > 8) {
			// chooseNumImg(s.charAt(8), num_8);
			// }
		}
	}

	public void chooseNumImg(int num, ImageView numView) {

		num = num - 48;
		switch (num) {
		case 0:
			numView.setImageResource(R.drawable.num_0);
			numView.setVisibility(View.VISIBLE);
			break;
		case 1:
			numView.setImageResource(R.drawable.num_1);
			numView.setVisibility(View.VISIBLE);
			break;
		case 2:
			numView.setImageResource(R.drawable.num_2);
			numView.setVisibility(View.VISIBLE);
			break;
		case 3:
			numView.setImageResource(R.drawable.num_3);
			numView.setVisibility(View.VISIBLE);
			break;
		case 4:
			numView.setImageResource(R.drawable.num_4);
			numView.setVisibility(View.VISIBLE);
			break;
		case 5:
			numView.setImageResource(R.drawable.num_5);
			numView.setVisibility(View.VISIBLE);
			break;
		case 6:
			numView.setImageResource(R.drawable.num_6);
			numView.setVisibility(View.VISIBLE);
			break;
		case 7:
			numView.setImageResource(R.drawable.num_7);
			numView.setVisibility(View.VISIBLE);
			break;
		case 8:
			numView.setImageResource(R.drawable.num_8);
			numView.setVisibility(View.VISIBLE);
			break;
		case 9:
			numView.setImageResource(R.drawable.num_9);
			numView.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	// private void setWelcome() {
	// if (currentUser != null) {
	// if (currentUser.getString(C.NICKNAME) != null
	// || !currentUser.getString(C.NICKNAME).equals(""))
	// welcome.setText(getResources().getString(
	// R.string.textview_welcome_)
	// + currentUser.getString(C.NICKNAME));
	// } else {
	// welcome.setText(R.string.textview_welcome);
	// }
	// }

	/**
	 * 进入房间
	 * 
	 * @param number
	 */
	private void enterRoom(final String number) {

		if (number.length() == 6) {
			pdDismiss(pd);
			pd = ProgressDialog.show(WhatsWallActivity.this, null, "正在加载中..",
					true);
			AVQuery<AVObject> query = new AVQuery<AVObject>(C.ClASS_ROOM);
			query.whereEqualTo(C.ROOM_NUMBER, number);
			System.out.println(number + "number");
			query.findInBackground(new FindCallback<AVObject>() {

				@Override
				public void done(List<AVObject> rooms, AVException e) {

					if (null == e) {
						
						System.out.println("###"+ Thread.currentThread().getId());
						
						if (rooms.size() == 0) {
							createRoom(number, true);
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
							Show.showToast(getApplication(), number
									+ "号墙发生了错误!");
						}
					} else {
						pdDismiss(pd);
						Show.disposeError(getApplication(), TAG, e);
					}
				}
			});
		} else {
			Show.showToast(getApplication(), "请输入完整的数字!");
		}
	}

	/**
	 * 创建房间,
	 * 
	 * @param room
	 * @param isEnter
	 *            是否创建完成后进入房间
	 */
	private void createRoom(final String number, final boolean isEnter) {

		AVObject avObject = new AVObject(C.ClASS_ROOM);
		avObject.put(C.ROOM_NUMBER, number);
		if (currentUser != null)
			avObject.put(C.ROOM_CREATEUSEROBJECTID, currentUser.getObjectId());
		avObject.saveInBackground(new SaveCallback() {

			@Override
			public void done(AVException e) {

				if (e == null) {

					if (isEnter) {
						enterRoom(number);
					}
					Show.logInfo(getApplication(), TAG, "创建房间" + number + "成功!");
				} else {
					Show.showToast(getApplication(), "创建Room失败!");
					Show.disposeError(getApplication(), TAG, e);
				}
			}
		});
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
			if (pd != null) {
				pd.dismiss();
			}
		} catch (Exception e) {

		}
	}

	private void getText() {
		call_text.setText("电话:18685918348");
	}

	/**
	 * 获取屏幕宽度
	 * 
	 * @return 屏幕宽度
	 */
	public int getScreenWidth() {

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	/**
	 * 获取屏幕高度
	 * 
	 * @return 屏幕高度（不除去状态栏）
	 */
	public int getScreenHeight() {

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}

	/**
	 * 获取顶栏的高度
	 * 
	 * @return
	 */
	public int getStatusBarHeight() {
		Class<?> c = null;
		Object obj = null;
		java.lang.reflect.Field field = null;
		int x = 0;
		int statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = getResources().getDimensionPixelSize(x);
			return statusBarHeight;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusBarHeight;
	}

	class WhatsWallHandler extends Handler {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {

			if (msg.what == 0) {
				HashMap<String, String> hashMap = null;
				try {
					hashMap = (HashMap<String, String>) getIntent().getExtras()
							.getSerializable("update");
				} catch (Exception e) {

				}
				if (hashMap != null) {
					Show.logInfo(getApplication(), TAG, hashMap.toString());
					if ("1".equals(hashMap.get("isUpdate"))) {

						String newVersion = hashMap.get("newVersion");
						String versionInfo = hashMap.get("versionInfo");
						new AlertDialog.Builder(WhatsWallActivity.this)
								.setTitle("新版本" + newVersion)
								.setMessage(versionInfo + "\n\n\n")
								.setPositiveButton(
										"去看看",
										new android.content.DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {

												Uri uri = Uri
														.parse("http://www.whatswall.com");
												Intent intent = new Intent(
														Intent.ACTION_VIEW, uri);
												startActivity(intent);
											}
										})
								.setNegativeButton(
										"算了吧",
										new android.content.DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {

											}
										}).show();
					}

				}
			} else if (msg.what == 1) {
				isExit = false;
			} else if (msg.what == 2) {
				final SharedPreferences sp_tip;
				sp_tip = getSharedPreferences("tip", 0);
				if (sp_tip.getBoolean("whatswall", true)) {
					final TipDialog tip = new TipDialog(WhatsWallActivity.this,
							R.style.dialog_tip);
					tip.setPosition(0, 0);
					tip.show();
					tip.setContent("输入800000查看更多神秘房间号");
					tip.setIKnowClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							tip.dismiss();
							sp_tip.edit().putBoolean("whatswall", false).commit();
						}
					});
				}
			}
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			Message msg = handler.obtainMessage();
			msg.what = 1;
			handler.sendMessageDelayed(msg, 3000);
			if (isExit) {
				stopService(new Intent().setClass(WhatsWallActivity.this,
						WWService.class));
				finish();

			} else {
				Show.showToast(getApplication(), "再按一次退出");
				isExit = true;
			}
		}
		return false;
	}

}

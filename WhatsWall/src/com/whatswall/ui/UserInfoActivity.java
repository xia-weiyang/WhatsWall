package com.whatswall.ui;

import java.io.IOException;

import com.whatswall.R;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.RefreshCallback;
import com.avos.avoscloud.SaveCallback;
import com.whatswall.base.C;
import com.whatswall.service.WWService;
import com.whatswall.service.WWService.GetToBinder;
import com.whatswall.tools.DisposeFile;
import com.whatswall.tools.DisposeImg;
import com.whatswall.tools.Show;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class UserInfoActivity extends Activity {

	private AVUser currentUser;

	private Button nickname;
	private Button username;
	private Button phone;
	private Button sex;
	private Button sign;
	private ImageView userimg;

	private UserInfoHandler handler;

	private final int IMG_SIZE = 100;

	// 请求码
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int RESULT_REQUEST_CODE = 2;

	private WWService mService;
	private ServiceConnection mSc;

	private final String TAG = "UserInfoActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userinfo);

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

		// AVOSCloud.setDebugLogEnabled(true);

		currentUser = AVUser.getCurrentUser();
		handler = new UserInfoHandler();

		nickname = (Button) findViewById(R.id.userinfo_nickname);
		username = (Button) findViewById(R.id.userinfo_username);
		phone = (Button) findViewById(R.id.userinfo_phone);
		sex = (Button) findViewById(R.id.userinfo_sex);
		sign = (Button) findViewById(R.id.userinfo_sign);
		userimg = (ImageView) findViewById(R.id.userinfo_userimg);
		Button password = (Button) findViewById(R.id.userinfo_password);
		Button exit = (Button) findViewById(R.id.userinfo_exit);

		Message msg = handler.obtainMessage();
		msg.obj = "start";
		handler.sendMessageDelayed(msg, 500);

		nickname.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showEditDialog(R.string.edit_nickname,
						R.string.action_nickname, 6, C.NICKNAME, nickname);
			}
		});
		sign.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				showEditDialog(R.string.edit_sign, R.string.action_sign, 20,
						C.SIGN, sign);
			}
		});
		sex.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				showRadioDialog();
			}
		});
		userimg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(i, IMAGE_REQUEST_CODE);

			}
		});
		password.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!currentUser.getBoolean(C.ISSETPASSWORD)) {
					Intent it = new Intent();
					Bundle b = new Bundle();
					b.putInt("password", PasswordActivity.PASSWORD_NEW);
					it.putExtras(b);
					it.setClass(getApplication(), PasswordActivity.class);
					startActivity(it);
				} else {
					Intent it = new Intent();
					Bundle b = new Bundle();
					b.putInt("password", PasswordActivity.PASSWORD_CHANGE);
					it.putExtras(b);
					it.setClass(getApplication(), PasswordActivity.class);
					startActivity(it);
				}
			}
		});

		exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AVUser.logOut();
				currentUser = AVUser.getCurrentUser();
				startActivity(new Intent().setClass(getApplication(),
						LoginActivity.class));
				finish();
			}
		});

	}

	@Override
	protected void onStart() {

		super.onStart();
		Intent intent = new Intent(UserInfoActivity.this, WWService.class);
		this.getApplicationContext().bindService(intent, mSc,
				Context.BIND_AUTO_CREATE);

	}

	@Override
	protected void onStop() {

		super.onStop();
		this.getApplicationContext().unbindService(mSc);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					setImageToView(data);
				}
			default:
				break;
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@SuppressLint("HandlerLeak")
	class UserInfoHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			if (msg.obj.toString().equals("start")) {

				setUserData();
				updateUserInfo();

			}
			if (msg.what == 1) {
				userimg.setImageBitmap((Bitmap) msg.obj);
			}
		}
	}

	/**
	 * show EditDialog
	 * 
	 * @param stringEdit
	 *            edit hint tip
	 * @param string
	 *            tip
	 * @param length
	 *            edit length
	 * @param c
	 *            AVUser key
	 * @param button
	 */
	private void showEditDialog(int stringEdit, int string, int length,
			final String c, final Button button) {
		final EditText editText = new EditText(getApplication());
		editText.setTextColor(Color.BLACK);
		editText.setHint(stringEdit);
		editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
				length) });

		new AlertDialog.Builder(UserInfoActivity.this)
				.setTitle(string)
				.setView(editText)
				.setPositiveButton(R.string.action_enter,
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (!editText.getText().toString().isEmpty()) {
									currentUser.put(c, editText.getText()
											.toString());
									currentUser
											.saveInBackground(new SaveCallback() {

												@Override
												public void done(AVException e) {
													if (null == e) {

														button.setText(editText
																.getText()
																.toString());
														sex.setTextColor(Color.BLACK);
													} else {
														Show.showToast(
																getApplication(),
																getResources()
																		.getString(
																				R.string.action_save_failed));
														Show.disposeError(
																getApplication(),
																TAG, e);

													}
												}
											});
								} else {
									Show.showToast(
											getApplication(),
											getResources().getString(
													R.string.action_save_empty));
								}
							}

						}).show();
	}

	private void showRadioDialog() {

		final String[] strings = new String[] { "男", "女" };
		int i = 0;
		if (currentUser.get(C.SEX) != null)
			if (currentUser.getString(C.SEX).equals("男"))
				i = 0;
			else
				i = 1;
		new AlertDialog.Builder(UserInfoActivity.this)
				.setTitle(R.string.action_sex)
				.setSingleChoiceItems(strings, i,
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									final int which) {

								currentUser.put(C.SEX, strings[which]);
								currentUser
										.saveInBackground(new SaveCallback() {

											@Override
											public void done(AVException e) {

												if (null == e) {

													sex.setText(strings[which]);
													sex.setTextColor(Color.BLACK);
												} else {
													Show.showToast(
															getApplication(),
															getResources()
																	.getString(
																			R.string.action_save_failed));
													Show.disposeError(
															getApplication(),
															TAG, e);
												}
											}
										});
							}
						}).show();
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		if (uri == null) {
			Log.e(TAG, "The uri is not exist.");
		}
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", IMG_SIZE);
		intent.putExtra("outputY", IMG_SIZE);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, RESULT_REQUEST_CODE);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void setImageToView(Intent data) {

		Bundle extras = data.getExtras();
		if (extras != null) {

			Bitmap photo = extras.getParcelable("data");
			userimg.setImageBitmap(photo);
			mService.saveUserImg(photo, "userimg", null);
		}
	}

	@SuppressWarnings("unused")
	@Deprecated
	/**
	 * 上传头像
	 * @param photo
	 */
	private void uploadImg(final Bitmap photo) {

		byte[] bytes = DisposeImg.getPngBitmapBytes(photo);
		final AVFile avFile = new AVFile("userimg", bytes);

		avFile.saveInBackground(new SaveCallback() {

			@Override
			public void done(AVException e) {

				if (null == e) {

					currentUser.put(C.USERIMG, avFile);

					// 此时将头像保存在User中
					currentUser.saveInBackground(new SaveCallback() {

						@Override
						public void done(AVException e) {

							if (null == e) {
								try {
									DisposeFile.saveJpgFile(photo,
											avFile.getObjectId() + ".jpg",
											DisposeFile.PATH_SDCARD_IMG_USER);
								} catch (IOException e1) {

									Show.disposeError(getApplication(), TAG, e1);
								}
							} else {

								Show.showToast(getApplication(), getResources()
										.getString(R.string.action_save_failed));
								Show.disposeError(getApplication(), TAG, e);
							}

						}
					});
				} else {

					Show.showToast(getApplication(), "上传头像失败");
					Show.disposeError(getApplication(), TAG, e);
				}
			}
		}, new ProgressCallback() {

			// 上传进度
			@Override
			public void done(Integer arg0) {

			}
		});

	}

	/**
	 * 更新用户信息
	 */
	private void updateUserInfo() {
		currentUser.refreshInBackground(new RefreshCallback<AVObject>() {

			@Override
			public void done(AVObject arg0, AVException e) {

				if (null == e) {
					AVUser.changeCurrentUser(currentUser, true);
					setUserData();
				} else {
					Show.showToast(getApplication(), "获取用户信息失败!");
					Show.disposeError(getApplication(), TAG, e);

				}

			}
		});
	}

	/**
	 * 设置用户数据
	 */
	private void setUserData() {
		if (currentUser.getString(C.NICKNAME) != null
				&& !currentUser.getString(C.NICKNAME).equals("")) {
			nickname.setText(currentUser.get(C.NICKNAME).toString());
		} else {
			nickname.setTextColor(Color.RED);
		}
		if (currentUser.getString(C.USERNAME) != null
				&& !currentUser.getString(C.USERNAME).equals("")) {
			username.setText(currentUser.get(C.USERNAME).toString());
		} else {
			username.setTextColor(Color.RED);
		}
		if (currentUser.getString(C.MOBILEPHONRNUMBER) != null
				&& !currentUser.getString(C.MOBILEPHONRNUMBER).equals("")) {
			phone.setText(currentUser.get(C.MOBILEPHONRNUMBER).toString());
		} else {
			phone.setTextColor(Color.RED);
		}
		if (currentUser.getString(C.SEX) != null
				&& !currentUser.getString(C.SEX).equals("")) {
			sex.setText(currentUser.get(C.SEX).toString());
		} else {
			sex.setTextColor(Color.RED);
		}
		if (currentUser.getString(C.SIGN) != null
				&& !currentUser.getString(C.SIGN).equals("")) {
			sign.setText(currentUser.get(C.SIGN).toString());
		} else {

		}
		// 获取头像
		getUserImg();
	}

	/**
	 * 获取用户头像
	 */
	private void getUserImg() {

		if (currentUser.getAVFile(C.USERIMG) != null) {
			final AVFile avFile = currentUser.getAVFile(C.USERIMG);
			mService.getBitmap(false, avFile.getObjectId(),
					DisposeFile.PATH_SDCARD_IMG_USER + "/",
					avFile.getObjectId() + ".png",
					mService.new DownloadCallBack() {

						@Override
						public void progress(int progress) {

						}

						@Override
						public void failed(Exception e) {

							Show.disposeError(getApplication(), TAG, e);
						}

						@Override
						public void done(Bitmap bitmap) {

							Message msg = handler.obtainMessage();
							msg.what = 1;
							msg.obj = bitmap;
							handler.sendMessage(msg);
						}
					},200);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		currentUser = AVUser.getCurrentUser();
		if (currentUser == null) {
			startActivity(new Intent().setClass(getApplication(),
					LoginActivity.class));
			finish();
		}
	}

}

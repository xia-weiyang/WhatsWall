package com.whatswall.ui;

import java.util.Timer;
import java.util.TimerTask;

import com.whatswall.R;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.whatswall.base.C;
import com.whatswall.tools.Show;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PasswordActivity extends Activity {

	// 设置新密码
	public static final int PASSWORD_NEW = 100;
	// 找回密码
	public static final int PASSWORD_FIND = 101;
	// 更改密码
	public static final int PASSWORD_CHANGE = 102;

	private PasswordHandler handler;
	private Button send;
	private Timer timer;
	private boolean isSendCode = false;

	private int state = 0;
	private AVUser currentUser;
	private ProgressDialog pd = null;

	private final String TAG = "PasswordActivity";
	private final int PASSWORD_LENGTH = 6;

	private TextView title;
	private TextView codeTip;
	private TextView passwordTip;
	private TextView passwordAgainTip;
	private TextView oldPasswordTip;
	private EditText phone;
	private EditText code;
	private EditText password;
	private EditText passwordAgain;
	private EditText oldPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		state = getIntent().getExtras().getInt("password");
		currentUser = AVUser.getCurrentUser();
		handler = new PasswordHandler();
		timer = new Timer();
		setView();

		title = (TextView) findViewById(R.id.userinfopassword_forget_title);
		codeTip = (TextView) findViewById(R.id.userinfopassword_forget_code_tip);
		passwordTip = (TextView) findViewById(R.id.userinfopassword_forget_password_tip);
		passwordAgainTip = (TextView) findViewById(R.id.userinfopassword_forget_password_again_tip);
		oldPasswordTip = (TextView) findViewById(R.id.userinfopassword_reset_oldpaw_tip);
		send = (Button) findViewById(R.id.userinfopassword_forget_send);
		Button enter = (Button) findViewById(R.id.userinfopassword_forget_enter);
		phone = (EditText) findViewById(R.id.userinfopassword_forget_phone);
		code = (EditText) findViewById(R.id.userinfopassword_forget_code);
		password = (EditText) findViewById(R.id.userinfopassword_forget_password);
		passwordAgain = (EditText) findViewById(R.id.userinfopassword_forget_password_again);
		oldPassword = (EditText) findViewById(R.id.userinfopassword_reset_oldpaw);

		setTitle(title);
		setPhone(phone);

		if (send != null)
			send.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					sendCode(phone.getText().toString());
				}
			});
		if (code != null)
			code.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					resetTip(codeTip);
				}
			});

		password.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				resetTip(passwordTip);
			}
		});

		passwordAgain.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				resetTip(passwordAgainTip);
			}
		});
		if (oldPassword != null)
			oldPassword.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					resetTip(oldPasswordTip);
				}
			});

		enter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				switch (state) {
				case PASSWORD_NEW:
					resetPassword();
					break;
				case PASSWORD_FIND:
					resetPassword();
					break;
				case PASSWORD_CHANGE:
					changePassword();
					break;
				default:
					break;
				}
			}
		});
	}

	private void setView() {
		switch (state) {
		case PASSWORD_NEW:
			setContentView(R.layout.activity_userinfopassword_forget);
			break;
		case PASSWORD_FIND:
			setContentView(R.layout.activity_userinfopassword_forget);
			break;
		case PASSWORD_CHANGE:
			setContentView(R.layout.activity_userinfopassword_reset);
			break;
		default:
			break;
		}
	}

	private void setTitle(TextView title) {
		switch (state) {
		case PASSWORD_NEW:
			title.setText(R.string.textview_password_new);
			break;
		case PASSWORD_FIND:
			title.setText(R.string.textview_password_find);
			break;
		case PASSWORD_CHANGE:
			title.setText(R.string.textview_password_change);
			break;
		default:
			break;
		}
	}

	private void setPhone(EditText phone) {
		switch (state) {
		case PASSWORD_NEW:
			phone.setText(currentUser.getMobilePhoneNumber());
			break;
		case PASSWORD_FIND:
			phone.setEnabled(true);
		default:
			break;
		}
	}

	private void sendCode(final String phone) {
		if (phone == null || phone.equals("")) {
			Show.showToast(getApplication(), "请输入手机号!");
		} else {
			if (!isSendCode) {
				AVUser.requestPasswordResetBySmsCodeInBackground(phone,
						new RequestMobileCodeCallback() {

							@Override
							public void done(AVException e) {
								if (null == e) {

									isSendCode = true;
									Log.i(TAG, phone + "  请求验证码成功!");

									timer.schedule(new TimerTask() {

										@Override
										public void run() {

											Message message = handler
													.obtainMessage();
											message.obj = "code";
											handler.sendMessage(message);
										}
									}, 0, 1000);

								} else {
									Toast.makeText(getApplication(),
											e.toString(), Toast.LENGTH_SHORT)
											.show();
									Log.e(TAG, e.toString());
								}
							}
						});
			}
		}
	}

	@SuppressLint("HandlerLeak")
	class PasswordHandler extends Handler {

		// 多少秒之后重新发送
		int sendtimer = 60;

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			if (msg.obj.toString().equals("code")) {

				send.setClickable(false);
				send.setText("重新发送(" + (sendtimer--) + ")");
				if (sendtimer < 0) {
					send.setClickable(true);
					send.setText(R.string.action_sendcode);
					isSendCode = false;
					sendtimer = 60;
					timer.cancel();
				}
			}
		}

	}

	private void resetTip(TextView textView) {
		textView.setText("");
		textView.setVisibility(View.INVISIBLE);
	}

	private void resetPassword() {

		if (code.getText().toString() == null
				|| code.getText().toString().equals("")) {
			codeTip.setVisibility(View.VISIBLE);
			codeTip.setTextColor(Color.RED);
			codeTip.setText("不能为空!");
		} else if (password.getText().toString() == null
				|| password.getText().toString().equals("")) {
			passwordTip.setVisibility(View.VISIBLE);
			passwordTip.setTextColor(Color.RED);
			passwordTip.setText("不能为空!");
		} else if (password.getText().toString().length() < PASSWORD_LENGTH) {
			passwordTip.setVisibility(View.VISIBLE);
			passwordTip.setTextColor(Color.RED);
			passwordTip.setText("密码太短!");
		} else if (passwordAgain.getText().toString() == null
				|| passwordAgain.getText().toString().equals("")) {
			passwordAgainTip.setVisibility(View.VISIBLE);
			passwordAgainTip.setTextColor(Color.RED);
			passwordAgainTip.setText("不能为空!");
		} else if (!passwordAgain.getText().toString()
				.equals(password.getText().toString())) {
			passwordAgainTip.setVisibility(View.VISIBLE);
			passwordAgainTip.setTextColor(Color.RED);
			passwordAgainTip.setText("密码不一致!");
		} else {
			pd = ProgressDialog.show(PasswordActivity.this, null, "正在加载中..",
					true);
			AVUser.resetPasswordBySmsCodeInBackground(
					code.getText().toString(), passwordAgain.getText()
							.toString(), new UpdatePasswordCallback() {

						@Override
						public void done(AVException e) {

							if (null == e) {
								if (state == PASSWORD_NEW)
									updateValueIsSetPassword();
								else {
									pd.dismiss();
									showDialog();
								}

							} else {
								pd.dismiss();
								Show.showToast(getApplication(),
										"重置密码失败!");
								Show.disposeError(getApplication(),
										TAG, e);
							}
						}
					});
		}
	}

	private void changePassword() {

		if (oldPassword.getText().toString() == null
				|| oldPassword.getText().toString().equals("")) {
			oldPasswordTip.setVisibility(View.VISIBLE);
			oldPasswordTip.setTextColor(Color.RED);
			oldPasswordTip.setText("不能为空!");
		} else if (password.getText().toString() == null
				|| password.getText().toString().equals("")) {
			passwordTip.setVisibility(View.VISIBLE);
			;
			passwordTip.setTextColor(Color.RED);
			passwordTip.setText("不能为空!");
		} else if (password.getText().toString().length() < PASSWORD_LENGTH) {
			passwordTip.setVisibility(View.VISIBLE);
			passwordTip.setTextColor(Color.RED);
			passwordTip.setText("密码太短!");
		} else if (passwordAgain.getText().toString() == null
				|| passwordAgain.getText().toString().equals("")) {
			passwordAgainTip.setVisibility(View.VISIBLE);
			;
			passwordAgainTip.setTextColor(Color.RED);
			passwordAgainTip.setText("不能为空!");
		} else if (!passwordAgain.getText().toString()
				.equals(password.getText().toString())) {
			passwordAgainTip.setVisibility(View.VISIBLE);
			passwordAgainTip.setTextColor(Color.RED);
			passwordAgainTip.setText("密码不一致!");
		} else {
			pd = ProgressDialog.show(PasswordActivity.this, null, "正在加载中..",
					true);
			currentUser.updatePasswordInBackground(oldPassword.getText()
					.toString(), passwordAgain.getText().toString(),
					new UpdatePasswordCallback() {

						@Override
						public void done(AVException e) {

							if (e == null) {
								pd.dismiss();
								showDialog();
							} else {
								pd.dismiss();
								Show.showToast(getApplication(),
										"更改密码失败!");
								Show.disposeError(getApplication(),
										TAG, e);
							}
						}
					});
		}
	}

	private void showDialog() {
		pd.dismiss();
		new AlertDialog.Builder(PasswordActivity.this)
				.setMessage("成功!")
				.setPositiveButton(R.string.action_enter,
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								AVUser.logOut();
								currentUser = AVUser.getCurrentUser();
								finish();
							}
						}).setCancelable(false).show();

	}

	private void updateValueIsSetPassword() {
		AVUser.loginByMobilePhoneNumberInBackground(phone.getText().toString(),
				password.getText().toString(), new LogInCallback<AVUser>() {

					@Override
					public void done(AVUser arg0, AVException e) {

						if (null == e) {
							currentUser = arg0;
							currentUser.put(C.ISSETPASSWORD, true);
							currentUser.saveInBackground(new SaveCallback() {

								@Override
								public void done(AVException e) {

									if (e != null) {
										pd.dismiss();
										Show.showToast(
												getApplication(), e.toString()
														+ "ISSETPASSWORD");
										Show.disposeError(
												getApplication(), TAG, e);
									} else {
										pd.dismiss();
										showDialog();
									}
								}
							});
						} else {
							pd.dismiss();
							Show.showToast(getApplication(),
									e.toString());
							Show.disposeError(getApplication(), TAG, e);
						}
					}
				});
	}
}

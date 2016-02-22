package com.whatswall.ui;


import com.whatswall.R;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.whatswall.tools.Show;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class LoginActivity extends Activity {

	public static final String FLAG = "login";
	private LoginBroadCast broadCast;

	private final String TAG = "LoginActivity";
	
	private ProgressDialog pd = null;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		
		System.out.println("****"+ Thread.currentThread().getId());
		
		setContentView(R.layout.activity_login);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		broadCast = new LoginBroadCast();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(FLAG);
		registerReceiver(broadCast, intentFilter);

		Button login = (Button) findViewById(R.id.login_enter);
		Button register = (Button) findViewById(R.id.login_register);
		Button otherLogin = (Button) findViewById(R.id.login_other);
		Button forget = (Button) findViewById(R.id.login_forget);
		final EditText username = (EditText) findViewById(R.id.login_username);
		final EditText password = (EditText) findViewById(R.id.login_password);
		int upid = Resources.getSystem().getIdentifier("up", "id", "android");  
		ImageView img = (ImageView) findViewById(upid);  
		img.setVisibility(View.VISIBLE);
		img.setImageResource(R.drawable.back);
		img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("******");
			}
		});
		
		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pd = ProgressDialog.show(LoginActivity.this, null, "µÇÂ¼ÖÐ..",true);
				login(username.getText().toString(), password.getText()
						.toString());
			}
		});

		register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent it = new Intent();
				Bundle b = new Bundle();
				b.putInt("register", RegisterActivity.REGISTER_SMS);
				it.putExtras(b);
				it.setClass(getApplication(),
						RegisterActivity.class);
				startActivity(it);
			}
		});
		
		otherLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent it = new Intent();
				Bundle b = new Bundle();
				b.putInt("register", RegisterActivity.LOGIN_SMS);
				it.putExtras(b);
				it.setClass(getApplication(),
						RegisterActivity.class);
				startActivity(it);
			}
		});
		
		forget.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it = new Intent();
				Bundle b = new Bundle();
				b.putInt("password", PasswordActivity.PASSWORD_FIND);
				it.putExtras(b);
				it.setClass(getApplication(),
						PasswordActivity.class);
				startActivity(it);
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	class LoginBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getStringExtra("data").equals("finish")) {
				finish();
			}
		}

	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(broadCast);
		super.onDestroy();
	}

	private void login(String username, String password) {

		if (username == null || password == null || username.equals("")
				|| password.equals("")) {
			Show.showToast(getApplication(), "ÓÃ»§Ãû»òÃÜÂë²»ÄÜÎª¿Õ!");
			pd.dismiss();
		} else {

			if (isPhone(username)) {
				AVUser.loginByMobilePhoneNumberInBackground(username, password,
						new LogInCallback<AVUser>() {

							@Override
							public void done(AVUser arg0, AVException e) {

								System.out.println("###"+ Thread.currentThread().getId());
								
								pd.dismiss();
								if (null == e) {
									startActivity(new Intent().setClass(getApplication(), UserInfoActivity.class));
									finish();
								} else {
									Show.showToast(getApplication(),
											"µÇÂ¼Ê§°Ü!");
									Show.disposeError(getApplication(),
											TAG, e);
								}
							}
						});
			} else {
				AVUser.logInInBackground(username, password,
						new LogInCallback<AVUser>() {

							@Override
							public void done(AVUser arg0, AVException e) {

								if (null == e) {
									startActivity(new Intent().setClass(getApplication(), UserInfoActivity.class));
									finish();
								} else {
									Show.showToast(getApplication(),
											"µÇÂ¼Ê§°Ü!");
									Show.disposeError(getApplication(),
											TAG, e);
								}
							}
						});
			}
		}
	}

	public boolean isPhone(String phone) {
		if (phone.length() != 11) {
			return false;
		}
		return true;
	}
}

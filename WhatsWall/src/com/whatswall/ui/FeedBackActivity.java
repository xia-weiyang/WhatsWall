package com.whatswall.ui;

import com.whatswall.R;
import com.whatswall.service.WWService;
import com.whatswall.service.WWService.GetToBinder;
import com.whatswall.tools.Show;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class FeedBackActivity extends BaseActivity{

	private WWService mService;
	private ServiceConnection mSc;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		setActionBarLayout(R.layout.actionbar_layout_room);
		
		ImageButton back = (ImageButton) findViewById(R.id.back);
		ImageButton favourites = (ImageButton) findViewById(R.id.favourites);
		TextView title = (TextView) findViewById(R.id.title);
		favourites.setVisibility(View.GONE);
		title.setText(R.string.textview_feedback_title);
		Button enter  = (Button) findViewById(R.id.feedback_enter);
		final EditText editText = (EditText) findViewById(R.id.feedback_edit);
		
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
		
		enter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(editText.getText() != null && !editText.getText().toString().equals("")){
					
					mService.saveFeedBackInfo(editText.getText().toString());
					finish();
				}else{
					Show.showToast(getApplication(), "«Î ‰»Îƒ⁄»›£°");
				}
			}
		});
		
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
	protected void onStart() {

		super.onStart();
		Intent intent = new Intent(FeedBackActivity.this,
				WWService.class);
		this.getApplicationContext().bindService(intent, mSc,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {

		super.onStop();
		this.getApplicationContext().unbindService(mSc);
	}

	
}

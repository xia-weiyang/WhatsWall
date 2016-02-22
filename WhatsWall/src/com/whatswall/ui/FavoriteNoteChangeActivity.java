package com.whatswall.ui;

import com.whatswall.R;
import com.whatswall.entity.Favorite;
import com.whatswall.service.WWService;
import com.whatswall.service.WWService.GetToBinder;

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

public class FavoriteNoteChangeActivity extends BaseActivity {

	private WWService mService;
	private ServiceConnection mSc;

	private Favorite favorite = null;
	private int position = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favoritenotechange);
		setActionBarLayout(R.layout.actionbar_layout_room);

		favorite = (Favorite) getIntent().getExtras().getSerializable(
				"favorite");
		position = getIntent().getExtras().getInt("position");

		ImageButton back = (ImageButton) findViewById(R.id.back);
		ImageButton favourites = (ImageButton) findViewById(R.id.favourites);
		TextView title = (TextView) findViewById(R.id.title);
		favourites.setVisibility(View.GONE);
		Button cancle = (Button) findViewById(R.id.favoritenotechange_cancle);
		Button enter;
		if (position == -2) {
			title.setText(R.string.textview_favoritenoteset_title);
			enter = (Button) findViewById(R.id.favoritenotechange_enter_);
			enter.setVisibility(View.VISIBLE);
			cancle.setVisibility(View.VISIBLE);
		} else {
			title.setText(R.string.textview_favoritenotechange_title);
			enter = (Button) findViewById(R.id.favoritenotechange_enter);
			enter.setVisibility(View.VISIBLE);
		}
		final EditText editText = (EditText) findViewById(R.id.favoritenotechange_edit);

		editText.setText(favorite.getNote());

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
				
			}
		});

		enter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (editText.getText().toString() != null
						&& !editText.getText().toString().equals("")) {

					mService.saveFavoriteNote(favorite, editText.getText()
							.toString());
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putInt("position", position);
					bundle.putString("note", editText.getText().toString());
					intent.putExtra("data", bundle);
					intent.setAction(FavoriteActivity.FLAG);
					sendBroadcast(intent);
					finish();
				}

			}
		});

		cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				finish();
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
		Intent intent = new Intent(FavoriteNoteChangeActivity.this,
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

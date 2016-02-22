package com.whatswall.ui;

import com.whatswall.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class RoomWelcomeChangeActivity extends BaseActivity{

	private String edit = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_roomwelcomechange);
		setActionBarLayout(R.layout.actionbar_layout_room);
		
		edit = getIntent().getExtras().getString("edit");
		
		ImageButton back = (ImageButton) findViewById(R.id.back);
		ImageButton favourites = (ImageButton) findViewById(R.id.favourites);
		TextView title = (TextView) findViewById(R.id.title);
		favourites.setVisibility(View.GONE);
		title.setText(R.string.textview_roomwelcomedit_title);
		Button enter = (Button) findViewById(R.id.roomwelcomchange_enter);
		final EditText ediText = (EditText) findViewById(R.id.roomwelcomchange_edit);
		ediText.setText(edit);
		
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
		enter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				RoomWelcomeActivity.editString = ediText.getText().toString();
				finish();
			}
		});
	}

	
}

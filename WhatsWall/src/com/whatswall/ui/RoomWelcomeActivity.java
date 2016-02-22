package com.whatswall.ui;

import com.whatswall.R;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.whatswall.base.C;
import com.whatswall.entity.Room;
import com.whatswall.tools.Show;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class RoomWelcomeActivity extends Activity {

	private Button enter;
	private TextView welcome;
	private ImageButton edit;
	private EditText editText;
	private Room room = null;
	public static String editString = "";

	private final String TAG = "RoomWelcomeActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_roomwelcome);

		room = (Room) getIntent().getExtras().getSerializable("room");

		enter = (Button) findViewById(R.id.roomwelcome_enter);
		welcome = (TextView) findViewById(R.id.roomwelcome);
		edit = (ImageButton) findViewById(R.id.roomwelcome_edit);
		editText = (EditText) findViewById(R.id.roomwelcome_editText);

		setWelcome();
		editString = welcome.getText().toString();

		enter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent it = new Intent();
				Bundle b = new Bundle();
				b.putSerializable("room", room);
				it.putExtras(b);
				it.setClass(getApplication(), RoomActivity.class);
				startActivity(it);
				finish();
			}
		});
		edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// if (editText.getVisibility() == View.VISIBLE) {
				// editText.setVisibility(View.INVISIBLE);
				// if (!editText.getText().toString().equals("")) {
				// room.setWelcome(editText.getText().toString());
				// upadteWelcome(room.getWelcome());
				// welcome.setText(room.getWelcome());
				// } else {
				// setWelcome();
				// }
				//
				// } else {
				// editText.setVisibility(View.VISIBLE);
				// editText.setFocusable(true);
				// editText.findFocus();
				// welcome.setText("");
				//
				// }
				
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("edit", welcome.getText().toString());
				intent.putExtras(bundle);
				intent.setClass(RoomWelcomeActivity.this, RoomWelcomeChangeActivity.class);
				startActivity(intent);
			}
		});
	}

	private void setWelcome() {

		if (room.getWelcome() != null && !room.getWelcome().equals("")) {
			welcome.setText(room.getWelcome());
		} else {
			welcome.setText(R.string.textview_roomwelcome);

		}
	}

	private void upadteWelcome(final String string) {

		AVQuery<AVObject> avQuery = new AVQuery<>(C.ClASS_ROOM);
		avQuery.getInBackground(room.getObjectId(),
				new GetCallback<AVObject>() {

					@Override
					public void done(AVObject avObject, AVException e) {

						if (null == e) {
							avObject.put(C.ROOM_WELCOME, string);
							avObject.saveInBackground(new SaveCallback() {

								@Override
								public void done(AVException e) {
									if (null != e) {
										Show.showToast(getApplication(),
												"±£¥Ê ß∞‹!");
										Show.showToast(getApplication(),
												e.toString());
										Show.disposeError(getApplication(),
												TAG, e);
									}

								}
							});
						} else {
							Show.showToast(getApplication(), e.toString());
							Show.disposeError(getApplication(), TAG, e);
						}
					}
				});
	}

	@Override
	protected void onResume() {
		
		super.onResume();
		System.out.println(editString);
		if(!editString.equals(welcome.getText().toString())){
			welcome.setText(editString);
			upadteWelcome(editString);
		}
	}
	
	
}

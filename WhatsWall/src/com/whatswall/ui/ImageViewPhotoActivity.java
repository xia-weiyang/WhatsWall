package com.whatswall.ui;

import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;

import com.polites.android.GestureImageView;
import com.whatswall.R;
import com.whatswall.base.C;
import com.whatswall.tools.DisposeFile;
import com.whatswall.tools.Show;
import com.whatswall.tools.Time;

public class ImageViewPhotoActivity extends BaseActivity {

	private static final int IMAGE_SCALE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imageviewphoto);

		final String path = getIntent().getStringExtra("bitmap");

		GestureImageView imgWhole = (GestureImageView) findViewById(R.id.imageviewphoto_whole);
		Button delete = (Button) findViewById(R.id.imageviewphoto_delete);

		imgWhole.setImageBitmap(BitmapFactory.decodeFile(path));

		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				setResult(IMAGE_SCALE);
				finish();
				overridePendingTransition(R.anim.alpha__, R.anim.scale__);
			}
		});

		imgWhole.setClickable(true);
		imgWhole.setLongClickable(true);
		imgWhole.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
				overridePendingTransition(R.anim.alpha__, R.anim.scale__);
			}
		});

		imgWhole.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				new AlertDialog.Builder(ImageViewPhotoActivity.this)
						.setItems(
								new String[] { "保存到手机" },
								new android.content.DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										switch (which) {
										case 0:
											boolean isCopy = DisposeFile
													.copyFile(
															path,
															DisposeFile.PATH_SDCARD_SAVE_IMG
																	+ "/"
																	+ Time.getNowMDHMSTime()
																	+ C.BITMAP_FORMAT,
															true);
											if (isCopy)
												Show.showToast(
														getApplication(),
														"图片已保存到SD卡根目录/WhatsWall/Img目录下!");
											else
												Show.showToast(
														getApplication(),
														"保存图片失败!");
											break;

										default:
											break;
										}
									}
								}).create().show();
				return true;
			}
		});
	}

}

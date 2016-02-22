package com.whatswall.ui;


import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.polites.android.GestureImageView;
import com.whatswall.R;
import com.whatswall.base.C;
import com.whatswall.service.WWService;
import com.whatswall.service.WWService.GetToBinder;
import com.whatswall.tools.DisposeFile;
import com.whatswall.tools.Show;

public class ImageViewActivity extends BaseActivity {

	private String imgName = "";
	private String roomNum = "";
	private ImageView img;
	private GestureImageView imgWhole;
	private ImageViewhandler handler;
	private Bitmap imgWholeBitmap = null;

	private final String TAG = "ImageViewActivity";

	private WWService mService;
	private ServiceConnection mSc;
	private ProgressBar bar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imageview);

		imgName = getIntent().getExtras().getString("imgname");
		roomNum = getIntent().getExtras().getString("roomnum");

		img = (ImageView) findViewById(R.id.imageview);
		imgWhole = (GestureImageView) findViewById(R.id.imageview_whole);
		bar = (ProgressBar) findViewById(R.id.imageview_pro);

		Show.logInfo(getApplication(), TAG, "imgName=" + imgName + "roomNum="
				+ roomNum);

		handler = new ImageViewhandler();
		
		Message msg = handler.obtainMessage();
		msg.what = 0;
		handler.sendMessageDelayed(msg, 500);
		
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

				new AlertDialog.Builder(ImageViewActivity.this)
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
															DisposeFile.PATH_SDCARD
																	+ "Img/Room/"
																	+ roomNum
																	+ "/"
																	+ imgName
																	+ C.BITMAP_FORMAT,
															DisposeFile.PATH_SDCARD_SAVE_IMG
																	+ "/"
																	+ imgName
																	+ C.BITMAP_FORMAT,
															true);
											if(isCopy)
												Show.showToast(getApplication(), "图片已保存到SD卡根目录/WhatsWall/Img目录下!");
											else
												Show.showToast(getApplication(), "保存图片失败!");
											break;

										default:
											break;
										}
									}
								}).create().show();
				return true;
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			finish();
			overridePendingTransition(R.anim.alpha__, R.anim.scale__);
		}
		return true;
	}

	@Override
	protected void onStart() {

		super.onStart();
		Intent intent = new Intent(ImageViewActivity.this, WWService.class);
		this.getApplicationContext().bindService(intent, mSc,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {

		super.onStop();
		this.getApplicationContext().unbindService(mSc);
	}

	class ImageViewhandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			if (msg.what == 0) {
				getImage(imgName, roomNum);
			} else if (msg.what == 1) {
				Bitmap bitmap = (Bitmap) msg.obj;
				imgWhole.setImageBitmap(bitmap);
				imgWhole.setVisibility(View.VISIBLE);
				img.setVisibility(View.INVISIBLE);
				bar.setVisibility(View.INVISIBLE);
			}
		}

	}

	private void getImage(final String imgName, final String roomNum) {

		String path = DisposeFile.PATH_SDCARD + "Img/Room/" + roomNum + "/";
		String filename = imgName + C.BITMAP_FORMAT;

		mService.getBitmap(false, imgName, path, filename,
				mService.new DownloadCallBack() {

					@Override
					public void progress(int progress) {

					}

					@Override
					public void failed(Exception e) {

					}

					@Override
					public void done(Bitmap bitmap) {

						img.setImageBitmap(bitmap);
						getImgWhole(imgName, roomNum);
					}
				}, C.screenWidth);

	}

	public void getImgWhole(String imgName, String roomNum) {

		String path = DisposeFile.PATH_SDCARD + "Img/Room/" + roomNum + "/";
		String filename = imgName + C.BITMAP_FORMAT;

		mService.getBitmap(true, imgName, path, filename,
				mService.new DownloadCallBack() {

					@Override
					public void progress(int progress) {

					}

					@Override
					public void failed(Exception e) {

					}

					@Override
					public void done(Bitmap bitmap) {
						imgWholeBitmap = bitmap;
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.obj = imgWholeBitmap;
						handler.sendMessage(msg);

					}
				}, C.screenWidth);

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		// 回收Bitmap占用的内存
		if (imgWholeBitmap != null) {
			imgWholeBitmap.recycle();
		}
	}

}

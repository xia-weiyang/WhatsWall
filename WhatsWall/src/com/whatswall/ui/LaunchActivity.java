package com.whatswall.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.whatswall.R;
import com.whatswall.base.C;
import com.whatswall.service.WWService;
import com.whatswall.tools.Show;

public class LaunchActivity extends BaseActivity {

	private final String TAG = "LaunchActivity";

	private boolean isEnter = false;
	
	private ViewPager vp;
	private ArrayList<View> views;
	private ImageView cicre1;
	private ImageView cicre2;
	private ImageView cicre3;
	private ImageView cicre4;
	private SharedPreferences sp_date;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		sp_date = getSharedPreferences("date", 0);
		LaunchHandler handler = new LaunchHandler();
		
		if(sp_date.getBoolean("frist", true)){
			
			setContentView(R.layout.activity_guide);
			
			vp = (ViewPager) findViewById(R.id.viewpager);

			cicre1 = (ImageView) findViewById(R.id.img_vp_page1);
			cicre2 = (ImageView) findViewById(R.id.img_vp_page2);
			cicre3 = (ImageView) findViewById(R.id.img_vp_page3);
			cicre4 = (ImageView) findViewById(R.id.img_vp_page4);
			
			
			LayoutInflater mLi = LayoutInflater.from(LaunchActivity.this);
			View view1 = mLi.inflate(R.layout.viewpage1, null);
			View view2 = mLi.inflate(R.layout.viewpage2, null);
			View view3 = mLi.inflate(R.layout.viewpage3, null);
			View view4 = mLi.inflate(R.layout.viewpage4, null);
			views = new ArrayList<View>();
			views.add(view1);
			views.add(view2);
			views.add(view3);
			views.add(view4);
			
			vp.setAdapter(new LaunchAdapter());
			vp.setOnPageChangeListener(new OnPageChangeListener() {
				
				@Override
				public void onPageSelected(int arg0) {
					switch (arg0) {
					case 0:
						cicre1.setImageResource(R.drawable.circle_selected);
						cicre2.setImageResource(R.drawable.circle_unselected);
						break;
					case 1:
						cicre1.setImageResource(R.drawable.circle_unselected);
						cicre2.setImageResource(R.drawable.circle_selected);
						cicre3.setImageResource(R.drawable.circle_unselected);
						break;
					case 2:
						cicre2.setImageResource(R.drawable.circle_unselected);
						cicre3.setImageResource(R.drawable.circle_selected);
						cicre4.setImageResource(R.drawable.circle_unselected);
						break;
					case 3:
						cicre3.setImageResource(R.drawable.circle_unselected);
						cicre4.setImageResource(R.drawable.circle_selected);
						break;
					default:
						break;
					}
				}
				
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onPageScrollStateChanged(int arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			Button enter = (Button) view4.findViewById(R.id.guide_enter);
			enter.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					sp_date.edit().putBoolean("frist", false).commit();
					HashMap<String, String> hashMap = new HashMap<String, String>();
					hashMap.put("version", getVersionName());
					hashMap.put("isUpdate", "0");
					enter(hashMap);
				}
			});
			
		}else{
		
		setContentView(R.layout.activity_launch);

		TextView view = (TextView) findViewById(R.id.launch_version);
		view.setText("版本 "+getVersionName());
		
		
		Message msg = handler.obtainMessage();
		msg.what = 0;
		handler.sendMessageDelayed(msg, 500);
		
		}

	}

	class LaunchHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			if (msg.what == 0) {

				startService(new Intent().setClass(LaunchActivity.this,
						WWService.class));

				final HashMap<String, String> hashMap = new HashMap<String, String>();
				hashMap.put("version", getVersionName());
				hashMap.put("isUpdate", "0");
				AVQuery<AVObject> query = new AVQuery<>(C.CLASS_VERSION);
				query.whereEqualTo(C.VERSION_PLATFORM, "Android");
				Message message = this.obtainMessage();
				message.what = 1;
				this.sendMessageDelayed(message, 5000);
				query.findInBackground(new FindCallback<AVObject>() {

					@Override
					public void done(List<AVObject> versions, AVException e) {

						LaunchHandler.this.removeMessages(1);
						if (null == e) {
							if (versions.size() == 1) {
								hashMap.put("newVersion", versions.get(0)
										.getString(C.VERSION_VERSION));
								if (versions.get(0)
										.getString(C.VERSION_VERSION)
										.compareTo(getVersionName()) > 0) {

									hashMap.put("versionInfo", versions.get(0)
											.getString(C.VERSION_VERSIONINFO));
									hashMap.put("isUpdate", "1");
								}
								enter(hashMap);
							} else {
								enter(hashMap);
							}
						} else {
							Show.disposeError(getApplication(), TAG, e);
							enter(hashMap);
						}
					}
				});

			} else if (msg.what == 1) {

				Show.showToast(getApplication(), "网络太不稳定了!");
				enter(null);
			}
		}

	}

	private void enter(HashMap<String, String> hashMap) {
		if (!isEnter) {
			isEnter = true;
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("update", hashMap);
			intent.putExtras(bundle);
			intent.setClass(LaunchActivity.this, WhatsWallActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.alpha_, R.anim.alpha__);
		}
	}

	/**
	 * 获取当前应用版本号
	 * 
	 * @return
	 */
	private String getVersionName() {

		PackageManager packageManager = getPackageManager();
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {

			e.printStackTrace();
		}
		String version = packInfo.versionName;

		return version;
	}
	
	class LaunchAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return views.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(View container, int position) {

			((ViewPager) container).addView(views.get(position));
			return views.get(position);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {

			((ViewPager) arg0).removeView(views.get(arg1));
		}

	}
}

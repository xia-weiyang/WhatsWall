package com.whatswall.adapter;

import java.util.ArrayList;

import com.avos.avoscloud.PostHttpResponseHandler;
import com.whatswall.R;
import com.whatswall.base.C;
import com.whatswall.entity.Like;
import com.whatswall.entity.RoomContent;
import com.whatswall.tools.DisposeImg;
import com.whatswall.tools.Show;
import com.whatswall.tools.Time;
import com.whatswall.tools.Download.DoneCallBack;
import com.whatswall.ui.ImageViewActivity;
import com.whatswall.ui.ReportActivity;
import com.whatswall.ui.RoomActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class RoomAdapter extends BaseAdapter {

	private ArrayList<RoomContent> contents = null;
	private RoomActivity mContext = null;
	private boolean isReport = false;
	private boolean isLike = false;
	private Like like = null;

	public RoomAdapter(ArrayList<RoomContent> contents, RoomActivity mContext,
			Like like) {

		this.contents = contents;
		this.mContext = mContext;
		this.like = like;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return contents.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		holder = new ViewHolder();
		LayoutInflater inflater = LayoutInflater.from(mContext);
		switch (contents.get(position).getContentType()) {
		case C.CONTENT_TYPE_ONLYTEXT:
			convertView = inflater.inflate(R.layout.list_content_text, parent,
					false);

			holder.nickname = (TextView) convertView
					.findViewById(R.id.list_user);
			holder.content = (TextView) convertView
					.findViewById(R.id.list_content);
			holder.content.setText(contents.get(position).getContent());

			break;
		case C.CONTENT_TYPE_ONLYIMG:
			convertView = inflater.inflate(R.layout.list_content_img, parent,
					false);
			holder.nickname = (TextView) convertView
					.findViewById(R.id.list_user);
			holder.img = (ImageView) convertView.findViewById(R.id.list_img);
			// 预加载图片
			loadImgSize(contents.get(position), holder);
			if (contents.get(position).getBitmaps() != null
					&& contents.get(position).getBitmaps().size() != 0)
				holder.img.setImageBitmap(contents.get(position).getBitmaps()
						.get(0));
			break;
		case C.CONTENT_TYPE_TEXTANDIMG:
			convertView = inflater.inflate(R.layout.list_content_textandimg,
					parent, false);
			holder.nickname = (TextView) convertView
					.findViewById(R.id.list_user);
			holder.content = (TextView) convertView
					.findViewById(R.id.list_content);
			holder.img = (ImageView) convertView.findViewById(R.id.list_img);

			// 预加载图片
			loadImgSize(contents.get(position), holder);

			holder.content.setText(contents.get(position).getContent());
			if (contents.get(position).getBitmaps() != null
					&& contents.get(position).getBitmaps().size() != 0)

				holder.img.setImageBitmap(contents.get(position).getBitmaps()
						.get(0));
			break;
		case C.CONTENT_TYPE_TIME:
			convertView = inflater.inflate(R.layout.list_content_time, parent,
					false);
			holder.time = (TextView) convertView.findViewById(R.id.list_time);
			holder.time.setText(Time.getYMD(contents.get(position)
					.getCreatDate()));
			break;
		default:
			break;

		}
		if (holder.nickname != null) {
			if (!contents.get(position).isAnon())
				holder.nickname.setText(contents.get(position).getUser()
						.getNickname());
			else
				holder.nickname.setText(R.string.textview_anon);

			holder.nickname.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (!contents.get(position).isAnon()) {
						System.out.println(holder.nickname.getText().toString()
								+ position);
					}
				}
			});
		}

		if (contents.get(position).getContentType() != C.CONTENT_TYPE_TIME) {
			holder.like = (ImageButton) convertView
					.findViewById(R.id.list_like);
			holder.like_tv = (TextView) convertView
					.findViewById(R.id.list_like_tv);
			if (isLike) {
				holder.like.setVisibility(View.VISIBLE);
				holder.like_tv.setVisibility(View.INVISIBLE);
			} else {
				holder.like.setVisibility(View.INVISIBLE);

			}
			holder.like_tv.setText(contents.get(position).getLike() + "赞");
			

			holder.report = (ImageButton) convertView
					.findViewById(R.id.list_report);
			if (isReport) {
				holder.report.setVisibility(View.VISIBLE);
				holder.like_tv.setVisibility(View.INVISIBLE);
			} else {
				holder.report.setVisibility(View.INVISIBLE);

			}
			if (!isLike && !isReport){
				if(contents.get(position).getLike() == 0){
					holder.like_tv.setVisibility(View.INVISIBLE);
				}else{
					holder.like_tv.setVisibility(View.VISIBLE);
				}
			}
		}
		convertView.setTag(holder);
		if (holder.like != null)
			holder.like.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (like.getAllLike() > 0) {
						like.clickLike(contents.get(position).getContentId());
						mContext.updateLikeNum();
						contents.get(position).setLike(
								contents.get(position).getLike() + 1);
						holder.like_tv.setText(contents.get(position).getLike()
								+ "赞");
					} else {
						Show.showToast(mContext, "您没赞了!");
					}
					Animation animation = new ScaleAnimation(1, 1.5f, 1, 1.5f,
							Animation.RELATIVE_TO_SELF, 0.5f,
							Animation.RELATIVE_TO_SELF, 0.5f);
					animation.setDuration(500);
					holder.like.startAnimation(animation);
				}
			});
		if (holder.report != null)
			holder.report.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("report", contents.get(position)
							.getObjectId());
					intent.putExtras(bundle);
					intent.setClass(mContext, ReportActivity.class);
					mContext.startActivity(intent);
				}
			});
		if (holder.img != null)
			holder.img.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (contents.get(position).getImgName() != null) {
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("imgname", contents.get(position)
								.getImgName()[0]);
						bundle.putString("roomnum", contents.get(position)
								.getRoomNum());
						intent.putExtras(bundle);
						intent.setClass(mContext, ImageViewActivity.class);
						mContext.startActivity(intent);
						mContext.overridePendingTransition(R.anim.scale_,
								R.anim.alpha_);
					}
				}
			});

		return convertView;
	}

	@Override
	public void notifyDataSetChanged() {

		super.notifyDataSetChanged();
	}

	class ViewHolder {
		TextView content;
		TextView nickname;
		ImageView img;
		ImageButton report;
		ImageButton like;
		// 已赞的次数显示
		TextView like_tv;
		TextView time;
	}

	public boolean isReport() {
		return isReport;
	}

	public void setReport(boolean isReport) {
		this.isReport = isReport;
	}

	public boolean isLike() {
		return isLike;
	}

	public void setLike(boolean isLike) {
		this.isLike = isLike;
	}

	/**
	 * 将图片放大或缩小
	 * 
	 * @param scale
	 *            倍数
	 * @param bitmap
	 * @return
	 */
	public Bitmap scaleBitmap(float scale, Bitmap bitmap) {
		Matrix matrix = new Matrix();
		matrix.reset();
		matrix.postScale(2, 2);
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return bitmap;
	}

	private void loadImgSize(RoomContent content, ViewHolder holder) {

		if (content.getImgWidthHeoght() != null
				&& content.getImgWidthHeoght()[0] != -1
				&& content.getImgWidthHeoght()[1] != -1) {
			LayoutParams params = holder.img.getLayoutParams();
			int[] is = DisposeImg.convertByWidth(content.getImgWidthHeoght());
			params.width = is[0];
			params.height = is[1];
			holder.img.setLayoutParams(params);

		}
	}


	public void setLike(Like like) {
		this.like = like;
	}

}

package com.whatswall.adapter;

import java.util.ArrayList;

import com.whatswall.R;
import com.whatswall.base.C;
import com.whatswall.entity.Comment;
import com.whatswall.entity.RoomContent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContentAdapter extends BaseAdapter {

	private RoomContent content = null;
	private ArrayList<Comment> comments = null;
	private Context context;

	public ContentAdapter(RoomContent content, ArrayList<Comment> comments,
			Context context) {

		this.content = content;
		this.comments = comments;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return comments.size() + 2;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		holder = new ViewHolder();
		LayoutInflater inflater = LayoutInflater.from(context);

		switch (position) {
		case 0:
			convertView = inflater.inflate(R.layout.list_user, parent, false);
			System.out.println(convertView + "1");
			holder.nickname = (TextView) convertView
					.findViewById(R.id.list_nickname);
			holder.imgUser = (ImageView) convertView
					.findViewById(R.id.list_userimg);
			if (!content.isAnon())
				holder.nickname.setText(content.getUser().getNickname());
			else
				holder.nickname.setText(R.string.textview_anon);
			holder.imgUser.setImageBitmap(content.getUser().getImg());
			break;
		case 1:
			convertView = setContent(convertView, inflater, parent, holder);

			break;
		default:

			convertView = inflater
					.inflate(R.layout.list_comment, parent, false);
			holder.commentNickname = (TextView) convertView
					.findViewById(R.id.list_commentuser);
			holder.commtent = (TextView) convertView
					.findViewById(R.id.list_comment);
			holder.commentNickname.setText(comments.get(position - 2).getUser()
					.getNickname());
			holder.commtent.setText(comments.get(position - 2).getComment());

			break;
		}
		convertView.setTag(holder);
		return convertView;
	}

	private View setContent(View convertView, LayoutInflater inflater,
			ViewGroup parent, ViewHolder holder) {
		switch (content.getContentType()) {
		case C.CONTENT_TYPE_ONLYTEXT:
			convertView = inflater.inflate(R.layout.list_content_text, parent,
					false);

			holder.nickname = (TextView) convertView
					.findViewById(R.id.list_user);
			holder.content = (TextView) convertView
					.findViewById(R.id.list_content);
			holder.content.setText(content.getContent());

			break;
		case C.CONTENT_TYPE_ONLYIMG:
			convertView = inflater.inflate(R.layout.list_content_img, parent,
					false);
			holder.nickname = (TextView) convertView
					.findViewById(R.id.list_user);
			holder.img = (ImageView) convertView.findViewById(R.id.list_img);
			if (content.getBitmaps() != null
					&& content.getBitmaps().size() != 0)
				holder.img.setImageBitmap(content.getBitmaps().get(0));
			break;
		case C.CONTENT_TYPE_TEXTANDIMG:
			convertView = inflater.inflate(R.layout.list_content_textandimg,
					parent, false);
			holder.nickname = (TextView) convertView
					.findViewById(R.id.list_user);
			holder.content = (TextView) convertView
					.findViewById(R.id.list_content);
			holder.img = (ImageView) convertView.findViewById(R.id.list_img);
			holder.content.setText(content.getContent());
			if (content.getBitmaps() != null
					&& content.getBitmaps().size() != 0)
				holder.img.setImageBitmap(content.getBitmaps().get(0));
			break;
		default:
			break;

		}
		holder.nickname.setVisibility(View.INVISIBLE);
		return convertView;
	}

	class ViewHolder {
		TextView content;
		TextView nickname;
		ImageView img;
		ImageView imgUser;
		TextView commentNickname;
		TextView commtent;
	}
}

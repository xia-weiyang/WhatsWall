package com.whatswall.adapter;

import java.util.ArrayList;

import com.whatswall.R;

import com.whatswall.entity.Favorite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.BaseAdapter;

import android.widget.LinearLayout;
import android.widget.TextView;

public class FavoriteAdapter extends BaseAdapter {

	private ArrayList<Favorite> favorites;
	private Context mContext;
	
    private IOnItemRightClickListener mListener = null;
    private int mRightWidth = 0;

    public interface IOnItemRightClickListener {
        void onRightClick(View v, int position);
    }

	public FavoriteAdapter(ArrayList<Favorite> favorites, Context mContext, int rightWidth,  IOnItemRightClickListener mListener) {
		this.favorites = favorites;
		this.mContext = mContext;
		this.mRightWidth = rightWidth;
        this.mListener = mListener;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return favorites.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
        final int thisPosition = position;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_favorite, parent, false);
            holder = new ViewHolder();
            holder.item_left = (View)convertView.findViewById(R.id.list_favorite_left);
            holder.item_right = (View)convertView.findViewById(R.id.list_favorite_right);
            holder.number = (TextView)convertView.findViewById(R.id.list_number_tv);
            holder.note = (TextView)convertView.findViewById(R.id.list_note);
            holder.delete = (Button) convertView.findViewById(R.id.list_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        LinearLayout.LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        holder.item_left.setLayoutParams(lp1);
        LinearLayout.LayoutParams lp2 = new LayoutParams(mRightWidth, LayoutParams.MATCH_PARENT);
        holder.item_right.setLayoutParams(lp2);
        
        holder.number.setText(favorites.get(thisPosition).getNumber());
        holder.note.setText(favorites.get(thisPosition).getNote());
        
        holder.delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onRightClick(v, thisPosition);
                }
            }
        });
        
		return convertView;
	}

	 
	
	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return true;
	}



	class ViewHolder {
		TextView number;
		TextView note;
		View item_left;
        View item_right;
        Button delete;
	}
}

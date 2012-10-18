package com.massivekinetics.emp.adapters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.massivekinetics.emp.R;

public class MusicAdapter extends BaseAdapter {

	List<File> mItems;
	Context mContext;

	public MusicAdapter(Context context) {
		mItems = new ArrayList<File>(/*MusicManager.getInstance()
				.getCurrentPlaylist()*/);
		mContext = context;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.one_file_player, null);
		TextView tvSongTitle = (TextView) view.findViewById(R.id.tvSongTitle);
		tvSongTitle.setText(mItems.get(position).getName());
		return view;
	}

}

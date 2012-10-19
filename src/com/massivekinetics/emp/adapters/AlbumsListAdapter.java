package com.massivekinetics.emp.adapters;

import java.util.List;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.massivekinetics.emp.EMPApplication;
import com.massivekinetics.emp.R;
import com.massivekinetics.emp.data.entities.AlbumDO;
import com.massivekinetics.emp.data.listeners.OnPlaylistChangedListener;

public class AlbumsListAdapter extends BaseAdapter implements
		OnPlaylistChangedListener {

	List<AlbumDO> items;

	public AlbumsListAdapter(List<AlbumDO> items) {
		this.items = items;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return ((AlbumDO) getItem(position)).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(EMPApplication.context);
		View view = getView(inflater, convertView, position);
		//view.setBackgroundColor(Color.BLACK);
		return view;
	}

	private ViewGroup getView(LayoutInflater inflater, View convertView,
			int position) {
		ViewGroup view;
		if (convertView != null && convertView instanceof ViewGroup) {
			view = (ViewGroup) convertView;
		} else {
			view = (ViewGroup) inflater.inflate(R.layout.listitem_artist, null);
		}

		updateView(view, position);

		return view;
	}

	private void updateView(ViewGroup view, int position) {
		Object tag = view.getTag();
		ArtistViewHolder viewHolder;
		if (tag != null)
			viewHolder = (ArtistViewHolder) tag;
		else {
			viewHolder = new ArtistViewHolder(
					(TextView)view.findViewById(R.id.tvArtistName),
					(TextView)view.findViewById(R.id.tvAlbumsCount));
			view.setTag(viewHolder);
		}
		
		int backgroundID = (position % 2 == 0) ? R.drawable.row_light
				: R.drawable.row_dark;
		
		view.setBackgroundResource(backgroundID);
		AlbumDO album = items.get(position);
		viewHolder.tvArtist.setText(album.getTitle().toUpperCase());
		viewHolder.tvAlbumCount.setText(album.getArtistName() + " albums");
	}

	@Override
	public void onPlaylistChanged() {

	}

	private class ArtistViewHolder {
		final TextView tvArtist;
		final TextView tvAlbumCount;

		public ArtistViewHolder(TextView tvArtist, TextView tvAlbumCount) {
			this.tvArtist = tvArtist;
			this.tvAlbumCount = tvAlbumCount;
		}
	}
}

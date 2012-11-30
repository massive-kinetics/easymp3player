package com.massivekinetics.emp.adapters;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.massivekinetics.emp.EMPApplication;
import com.massivekinetics.emp.R;
import com.massivekinetics.emp.data.entities.ArtistDO;
import com.massivekinetics.emp.data.entities.BaseDO;

public class ArtistListAdapter<T extends ArtistDO> extends EMPListAdapter<T> {

	public ArtistListAdapter(List<T> items) {
		super(items, R.layout.listitem_artist);
	}
	
	@Override
	protected void updateView(ViewGroup view, int position) {
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
		ArtistDO artist = items.get(position);
		viewHolder.tvArtist.setText(artist.getName().toUpperCase());
		viewHolder.tvArtist.setSelected(true);
		viewHolder.tvAlbumCount.setText(artist.getAlbumCount() + " albums");
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

package com.massivekinetics.emp.adapters;

import java.util.List;

import android.view.ViewGroup;
import android.widget.TextView;

import com.massivekinetics.emp.R;
import com.massivekinetics.emp.data.entities.TrackDO;

public class TracksListAdapter<T extends TrackDO> extends EMPListAdapter<T> {

	public TracksListAdapter(List<T> items) {
		super(items, R.layout.listitem_track);
	}

	@Override
	protected void updateView(ViewGroup view, int position) {
		Object tag = view.getTag();
		TrackViewHolder viewHolder;
		if (tag != null)
			viewHolder = (TrackViewHolder) tag;
		else {
			viewHolder = new TrackViewHolder(
					(TextView) view.findViewById(R.id.title),
					(TextView) view.findViewById(R.id.artist),
					(TextView) view.findViewById(R.id.album),
					(TextView) view.findViewById(R.id.duration));
			view.setTag(viewHolder);
		}

		int backgroundID = (position % 2 == 0) ? R.drawable.row_light
				: R.drawable.row_dark;

		view.setBackgroundResource(backgroundID);
		TrackDO track = items.get(position);
		viewHolder.title.setText(track.getTitle().toUpperCase());
		viewHolder.album.setText(track.getAlbum());
		viewHolder.artist.setText(track.getArtist());
		long duration = track.getDuration();
		viewHolder.duration.setText(String.format("%02d:%02d", (duration%3600)/60, (duration%60)));
	}

	@Override
	public void onPlaylistChanged() {

	}

	private class TrackViewHolder {
		final TextView title;
		final TextView artist;
		final TextView album;
		final TextView duration;

		public TrackViewHolder(TextView title, TextView artist, TextView album,
				TextView duration) {
			this.title = title;
			this.artist = artist;
			this.album = album;
			this.duration = duration;
		}
	}
}

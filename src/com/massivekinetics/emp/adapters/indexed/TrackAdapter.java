package com.massivekinetics.emp.adapters.indexed;

import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils.TruncateAt;
import android.view.ViewGroup;
import android.widget.TextView;

import com.massivekinetics.emp.R;
import com.massivekinetics.emp.data.entities.TrackDO;

public class TrackAdapter extends IndexedAdapter {
	static String[] cols = new String[] { MediaStore.Audio.Media.TITLE,
			MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
			MediaStore.Audio.Media.DURATION };

	static int[] to = new int[] { R.id.title, R.id.artist, R.id.album,
			R.id.duration };

	public TrackAdapter(Cursor cursor) {
		super(R.layout.listitem_track_simple, cursor, cols, to,
				MediaStore.Audio.Media.TITLE);
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
		TrackDO track = getDataObject(position);

		viewHolder.title.setText(track.getTitle().toUpperCase());
		viewHolder.title.setEllipsize(TruncateAt.MARQUEE);
		viewHolder.title.setSingleLine(true);

		viewHolder.album.setText(track.getAlbum());
		viewHolder.artist.setText(track.getArtist());
		long duration = track.getDuration();
		viewHolder.duration.setText(String.format("%02d:%02d",
				(duration % 3600) / 60, (duration % 60)));
	}

	@Override
	protected TrackDO getDataObject(int position) {
		TrackDO trackDO = (TrackDO) cache.get(position);
		
		if (trackDO == null || isNeededUpdate) {

			Cursor cursor = (Cursor) getItem(position);
			int artistColumn = cursor
					.getColumnIndex(MediaStore.Audio.Media.ARTIST);
			int titleColumn = cursor
					.getColumnIndex(MediaStore.Audio.Media.TITLE);
			int albumColumn = cursor
					.getColumnIndex(MediaStore.Audio.Media.ALBUM);

			int albumIdColumn = cursor
					.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);

			int durationColumn = cursor
					.getColumnIndex(MediaStore.Audio.Media.DURATION);
			int idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);

			trackDO = new TrackDO(cursor.getLong(idColumn),
					cursor.getString(artistColumn),
					cursor.getString(titleColumn),
					cursor.getString(albumColumn),
					cursor.getLong(albumIdColumn),
					cursor.getLong(durationColumn));
			cache.put(position, trackDO);
		}
		
		return trackDO;

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

package com.massivekinetics.emp.adapters.indexed;

import android.database.Cursor;
import android.provider.MediaStore;
import android.view.ViewGroup;
import android.widget.TextView;

import com.massivekinetics.emp.R;
import com.massivekinetics.emp.data.entities.ArtistDO;

public class ArtistAdapter extends IndexedAdapter {

	private final static String[] cols = { MediaStore.Audio.Artists.ARTIST,
			MediaStore.Audio.Artists.NUMBER_OF_ALBUMS };

	public ArtistAdapter(Cursor cursor) {
		super(R.layout.listitem_artist, cursor, cols, new int[] {
				R.id.tvArtistName, R.id.tvAlbumsCount },
				MediaStore.Audio.Artists.ARTIST);
	}

	@Override
	protected void updateView(ViewGroup view, int position) {
		Object tag = view.getTag();
		ArtistViewHolder viewHolder;
		if (tag != null)
			viewHolder = (ArtistViewHolder) tag;
		else {
			viewHolder = new ArtistViewHolder(
					(TextView) view.findViewById(R.id.tvArtistName),
					(TextView) view.findViewById(R.id.tvAlbumsCount));
			view.setTag(viewHolder);
		}
		int backgroundPosition = position
				- sectionToOffset.get(getSectionForPosition(position)) - 1;

		int backgroundID = (backgroundPosition % 2 == 0) ? R.drawable.row_light
				: R.drawable.row_dark;

		view.setBackgroundResource(backgroundID);
		ArtistDO artist = getDataObject(position);
		viewHolder.tvArtist.setText(artist.getName().toUpperCase());
		viewHolder.tvArtist.setSelected(true);
		viewHolder.tvAlbumCount.setText(artist.getAlbumCount() + " albums");
	}

	protected ArtistDO getDataObject(int position) {
		ArtistDO artistDO = (ArtistDO) cache.get(position);
		if (artistDO == null || isNeededUpdate) {

			Cursor cursor = (Cursor) getItem(position);
			int artistIdColumn = cursor
					.getColumnIndex(MediaStore.Audio.Artists._ID);
			int artistColumm = cursor
					.getColumnIndex(MediaStore.Audio.Artists.ARTIST);
			int artistAlbumCountColumn = cursor
					.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS);
			int artistTrackCountColumn = cursor
					.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS);

			artistDO = new ArtistDO(cursor.getInt(artistIdColumn),
					cursor.getString(artistColumm),
					cursor.getInt(artistAlbumCountColumn),
					cursor.getInt(artistTrackCountColumn));

			cache.put(position, artistDO);
		}
		return artistDO;

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

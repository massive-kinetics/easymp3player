package com.massivekinetics.emp.adapters.indexed;

import android.database.Cursor;
import android.provider.MediaStore;
import android.view.ViewGroup;
import android.widget.TextView;

import com.massivekinetics.emp.R;
import com.massivekinetics.emp.data.entities.AlbumDO;

public class AlbumAdapter extends IndexedAdapter {
	static String[] cols = { MediaStore.Audio.Media.ARTIST,
			MediaStore.Audio.Albums.ALBUM,
			MediaStore.Audio.Albums.NUMBER_OF_SONGS };

	static int[] to = new int[] { R.id.artist, R.id.title, R.id.trackCount };

	public AlbumAdapter(Cursor cursor) {
		super(R.layout.listitem_albums, cursor, cols, to,
				MediaStore.Audio.AlbumColumns.ALBUM);
	}

	@Override
	protected void updateView(ViewGroup view, int position) {
		Object tag = view.getTag();
		AlbumViewHolder viewHolder;
		if (tag != null)
			viewHolder = (AlbumViewHolder) tag;
		else {
			viewHolder = new AlbumViewHolder(
					(TextView) view.findViewById(R.id.artist),
					(TextView) view.findViewById(R.id.title),
					(TextView) view.findViewById(R.id.trackCount));
			view.setTag(viewHolder);
		}

		int backgroundID = (position % 2 == 0) ? R.drawable.row_light
				: R.drawable.row_dark;

		view.setBackgroundResource(backgroundID);
		AlbumDO album = getDataObject(position);
		String title = album.getTitle().toUpperCase();

		viewHolder.tvAlbum.setText(title);
		viewHolder.tvAlbum.setSelected(true);

		viewHolder.tvArtist.setText(album.getArtistName());
		viewHolder.tvArtist.setSelected(true);
		viewHolder.tvAlbumCount.setText(album.getSongCount() + " songs");
	}

	@Override
	protected AlbumDO getDataObject(int position) {
		AlbumDO albumDO = (AlbumDO) cache.get(position);
		if (albumDO == null || isNeededUpdate) {
			Cursor cursor = (Cursor) getItem(position);
			int albumIdColumn = cursor
					.getColumnIndex(MediaStore.Audio.Albums._ID);
			int albumColumm = cursor
					.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
			int albumArtistColumn = cursor
					.getColumnIndex(MediaStore.Audio.Media.ARTIST);
			int albumSongNumberColumn = cursor
					.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS);
			albumDO = new AlbumDO(cursor.getInt(albumIdColumn),
					cursor.getString(albumColumm),
					cursor.getString(albumArtistColumn),
					cursor.getInt(albumSongNumberColumn));
			cache.put(position, albumDO);
		}
		return albumDO;
	}
	
	private class AlbumViewHolder {
		final TextView tvArtist;
		final TextView tvAlbum;
		final TextView tvAlbumCount;

		public AlbumViewHolder(TextView tvArtist, TextView tvAlbum,
				TextView tvAlbumCount) {
			this.tvArtist = tvArtist;
			this.tvAlbum = tvAlbum;
			this.tvAlbumCount = tvAlbumCount;
		}
	}


}

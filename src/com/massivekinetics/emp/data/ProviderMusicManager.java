package com.massivekinetics.emp.data;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.massivekinetics.emp.EMPApplication;
import com.massivekinetics.emp.MusicService;
import com.massivekinetics.emp.data.entities.Playlist;
import com.massivekinetics.emp.data.entities.Track;
import com.massivekinetics.emp.interfaces.DataManager;
import com.massivekinetics.emp.utils.FileUtils;

public class ProviderMusicManager implements DataManager {
	private static final String TAG = "ProviderMusicManager";

	private static ContentResolver contentResolver = EMPApplication.context
			.getContentResolver();
	private Playlist currentPlaylist;

	/**
	 * Loads music data. This method may take long, so be sure to call it
	 * asynchronously without blocking the main thread.
	 */
	public void prepare() {
		currentPlaylist = new Playlist("", 0);

		Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		// Perform a query on the content resolver. The URI we're passing
		// specifies that we
		// want to query for all audio media on external storage (e.g. SD card)
		Cursor cur = contentResolver.query(uri, null,
				MediaStore.Audio.Media.IS_MUSIC + " = 1", null, null);
		Log.i(TAG, "Query finished. "
				+ (cur == null ? "Returned NULL." : "Returned a cursor."));

		if (cur == null) {
			// Query failed...
			Log.e(TAG, "Failed to retrieve music: cursor is null :-(");
			return;
		}
		if (!cur.moveToFirst()) {
			// Nothing to query. There is no music on the device. How boring.
			Log.e(TAG, "Failed to move cursor to first row (no query results).");
			return;
		}

		Log.i(TAG, "Listing...");

		// retrieve the indices of the columns where the ID, title, etc. of the
		// song are
		int artistColumn = cur.getColumnIndex(MediaStore.Audio.Media.ARTIST);
		int titleColumn = cur.getColumnIndex(MediaStore.Audio.Media.TITLE);
		int albumColumn = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM);

		int albumIdColumn = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);

		int durationColumn = cur
				.getColumnIndex(MediaStore.Audio.Media.DURATION);
		int idColumn = cur.getColumnIndex(MediaStore.Audio.Media._ID);

		Log.i(TAG, "Title column index: " + String.valueOf(titleColumn));
		Log.i(TAG, "ID column index: " + String.valueOf(titleColumn));

		// add each song to mItems
		do {
			Log.i(TAG,
					"ID: " + cur.getString(idColumn) + " Title: "
							+ cur.getString(titleColumn));

			currentPlaylist.add(new Track(cur.getLong(idColumn), cur
					.getString(artistColumn), cur.getString(titleColumn), cur
					.getString(albumColumn), cur.getLong(albumIdColumn), cur
					.getLong(durationColumn)));

		} while (cur.moveToNext());

		Log.i(TAG, "Done querying media. MusicRetriever is ready.");
	}

	public void nextTrack() {
		Track tr = getRandomTrack();
		EMPApplication.context
				.startService(new Intent(MusicService.ACTION_SKIP));
	}

	public Track getRandomTrack() {
		return currentPlaylist.get((int) (Math.random() * currentPlaylist
				.size()));
	}

	public ContentResolver getContentResolver() {
		return contentResolver;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public Playlist getPlayList(int playlistID) {
		return currentPlaylist;
	}

}

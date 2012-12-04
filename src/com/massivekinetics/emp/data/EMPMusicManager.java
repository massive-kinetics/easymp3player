package com.massivekinetics.emp.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;

import com.massivekinetics.emp.EMPApplication;
import com.massivekinetics.emp.R;
import com.massivekinetics.emp.data.db.DatabaseHelper;
import com.massivekinetics.emp.data.db.PlaylistTable;
import com.massivekinetics.emp.data.db.TrackToPlaylistTable;
import com.massivekinetics.emp.data.entities.AlbumDO;
import com.massivekinetics.emp.data.entities.ArtistDO;
import com.massivekinetics.emp.data.entities.BaseDO;
import com.massivekinetics.emp.data.entities.PlaylistDO;
import com.massivekinetics.emp.data.entities.TrackDO;
import com.massivekinetics.emp.data.listeners.OnPlaylistChangedListener;
import com.massivekinetics.emp.data.listeners.OnPlaylistsInfoChangedListener;
import com.massivekinetics.emp.interfaces.MusicManager;
import com.massivekinetics.emp.logger.Logger;
import com.massivekinetics.emp.utils.Error;

public class EMPMusicManager implements MusicManager {

	private final static String TAG = "EMPMusicManager";

	private static PlaylistDO allTracksPlaylist;
	private boolean isInitialized = false;

	// TODO: play with cache
	private Map<Long, PlaylistDO> playlistCache;
	private SparseArray<OnPlaylistChangedListener> playlistChangedListeners;
	private List<OnPlaylistsInfoChangedListener> playlistsInfoListeners;

	// Caches
	private SparseArray<BaseDO> trackCache;
	private SparseArray<BaseDO> artistCache;
	private SparseArray<BaseDO> albumCache;

	private ContentResolver contentResolver;

	private SQLiteDatabase database;
	private DatabaseHelper databaseHelper;

	private static final Uri MEDIA_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	private static final Uri ALBUMS_URI = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
	private static final Uri ARTISTS_URI = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;

	public EMPMusicManager() {
		databaseHelper = new DatabaseHelper(EMPApplication.context);
	}

	public boolean isInitialized() {
		return isInitialized;
	}

	@Override
	public void init() {
		synchronized (EMPMusicManager.class) {
			if (!isInitialized) {
				playlistCache = new HashMap<Long, PlaylistDO>();
				playlistChangedListeners = new SparseArray<OnPlaylistChangedListener>();
				playlistsInfoListeners = new ArrayList<OnPlaylistsInfoChangedListener>();

				trackCache = new SparseArray<BaseDO>();
				artistCache = new SparseArray<BaseDO>();
				albumCache = new SparseArray<BaseDO>();

				contentResolver = EMPApplication.context.getContentResolver();
				fetchAllTracksFromSdCard();
				fetchPlaylistCache();
				isInitialized = true;
			}
		}
	}

	private void fetchPlaylistCache() {
		openDatabase();
		try {
			Cursor cursor = database.query(PlaylistTable.TABLE_NAME,
					PlaylistTable.COLUMNS, null, null, null, null,
					PlaylistTable.CREATED + " asc");
			if (cursor != null && cursor.moveToFirst()) {
				do {
					PlaylistDO playlist = getPlaylistFromCursor(cursor);
					fetchPlaylistWithTracks(playlist);
					playlistCache.put(playlist.getId(), playlist);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Logger.e(TAG, e.getMessage(), e);
		} finally {
			closeDatabase();
		}
	}

	private void fetchPlaylistWithTracks(final PlaylistDO playlist) {
		openDatabase();
		Cursor cursor = database.query(TrackToPlaylistTable.TABLE_NAME,
				TrackToPlaylistTable.COLUMNS, TrackToPlaylistTable.PLAYLIST_ID
						+ "=?", new String[] { "" + playlist.getId() }, null,
				null, null);
		try {
			if (cursor != null && cursor.moveToFirst()) {
				int[] ids = new int[cursor.getCount()];
				int counter = 0;

				int idColumn = cursor
						.getColumnIndex(TrackToPlaylistTable.TRACK_ID);
				do {
					ids[counter++] = cursor.getInt(idColumn);
				} while (cursor.moveToNext());

				List<TrackDO> trackList = getTrackListFromIds(ids);
				playlist.setTracks(trackList);
			}
		} finally {
			closeDatabase();
		}
	}

	private List<TrackDO> getTrackListFromIds(int[] ids) {
		List<TrackDO> trackList = new ArrayList<TrackDO>();

		int argcount = ids.length; // number of IN arguments
		String[] args = new String[ids.length];
		for (int i = 0; i < argcount; i++) {
			args[i] = "" + ids[i];
		}

		StringBuilder inList = new StringBuilder(argcount * 2);

		for (int i = 0; i < argcount; i++) {
			if (i > 0)
				inList.append(",");
			inList.append("?");
		}

		Cursor cur = contentResolver.query(MEDIA_URI, null,
				MediaStore.Audio.Media.IS_MUSIC + " = 1" + " and _id IN ("
						+ inList.toString() + ")", args, null);

		if (cur != null && cur.moveToFirst()) {
			// retrieve the indices of the columns where the ID, title, etc. of
			// the
			// song are
			int artistColumn = cur
					.getColumnIndex(MediaStore.Audio.Media.ARTIST);
			int titleColumn = cur.getColumnIndex(MediaStore.Audio.Media.TITLE);
			int albumColumn = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM);

			int albumIdColumn = cur
					.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);

			int durationColumn = cur
					.getColumnIndex(MediaStore.Audio.Media.DURATION);
			int idColumn = cur.getColumnIndex(MediaStore.Audio.Media._ID);

			do {
				trackList.add(new TrackDO(cur.getInt(idColumn), cur
						.getString(artistColumn), cur.getString(titleColumn),
						cur.getString(albumColumn), cur.getLong(albumIdColumn),
						cur.getLong(durationColumn)));
			} while (cur.moveToNext());

		}

		return trackList;
	}

	@Override
	public PlaylistDO getPlaylist(long playlistId) {
		if (!isInitialized)
			throw new RuntimeException(TAG
					+ " getPlaylist() : EMPMusicManager isn't initialized!");

		if (playlistId == ALL_TRACKS)
			return allTracksPlaylist;

		PlaylistDO result = playlistCache.get(playlistId);
		if (result != null)
			return result;

		result = getPlaylistFromDB(playlistId);
		playlistCache.put(playlistId, result);
		return result;
	}

	private PlaylistDO getPlaylistFromDB(long playlistId) {
		PlaylistDO result;
		openDatabase();
		Cursor cursor = database.query(PlaylistTable.TABLE_NAME,
				PlaylistTable.COLUMNS, PlaylistTable.ID + "=?",
				new String[] { "" + playlistId }, null, null, null);

		try {
			if (cursor == null || !cursor.moveToFirst()) {
				result = PlaylistDO.Null;
				return result;
			} else {
				result = getPlaylistFromCursor(cursor);

			}

			fetchPlaylistWithTracks(result);

		} finally {
			closeDatabase();
		}
		return result;
	}

	private PlaylistDO getPlaylistFromCursor(Cursor cur) {

		int idColumn = cur.getColumnIndex(PlaylistTable.ID);
		int titleColumn = cur.getColumnIndex(PlaylistTable.TITLE);
		int createdColumn = cur.getColumnIndex(PlaylistTable.CREATED);
		int modifiedColumn = cur.getColumnIndex(PlaylistTable.MODIFIED);

		return new PlaylistDO(cur.getLong(idColumn),
				cur.getString(titleColumn), Long.parseLong(cur
						.getString(createdColumn)), Long.parseLong(cur
						.getString(modifiedColumn)));

	}

	@Override
	public List<PlaylistDO> getPlaylists() {
		List<PlaylistDO> res = new ArrayList<PlaylistDO>();
		for (long key : playlistCache.keySet())
			res.add(playlistCache.get(key));
		return res;
	}

	@Override
	public long deletePlaylist(long playlistId) {
		openDatabase();
		// default value. If delete operation is successful then this method
		// returns ID of deleted row
		long id = Error.PLAYLIST_NOT_DELETED;
		try {
			id = database.delete(PlaylistTable.TABLE_NAME, PlaylistTable.ID
					+ "=?", new String[] { "" + playlistId });
		} finally {
			closeDatabase();
		}

		if (id > 0 && id != Error.PLAYLIST_NOT_DELETED)
			playlistCache.remove(playlistId);

		return id;
	}

	@Override
	public void deleteTrackFromPlaylist(long playlistId, long[] trackIds) {
		openDatabase();

		StringBuilder removeIds = new StringBuilder();
		boolean first = true;

		for (long trackId : trackIds) {
			if (!first) {
				removeIds.append(",");
			}
			removeIds.append("'" + trackId + "'");
			first = false;
		}
		String rawQuery = "delete from " + TrackToPlaylistTable.TABLE_NAME
				+ " where " + TrackToPlaylistTable.PLAYLIST_ID + "="
				+ playlistId + " and " + TrackToPlaylistTable.TRACK_ID
				+ " in (" + removeIds.toString() + ")";

		long operationId = Error.PLAYLIST_NOT_UPDATED;
		try {
			Cursor cursor = database.rawQuery(rawQuery, null);

			/*
			 * operationId = database .delete(TrackToPlaylistTable.TABLE_NAME,
			 * TrackToPlaylistTable.PLAYLIST_ID + "=? and " +
			 * TrackToPlaylistTable.TRACK_ID + " in (?)", new String[] { "" +
			 * playlistId, "" + removeIds.toString() });
			 */
		} finally {
			closeDatabase();
		}

		playlistCache.remove(playlistId);
		playlistCache.put(playlistId, getPlaylistFromDB(playlistId));

	}

	@Override
	public void updatePlaylist(PlaylistDO playlist) {
		ContentValues playlistValues = fromPlaylist(playlist);
		openDatabase();
		try {
			database.update(PlaylistTable.TABLE_NAME, playlistValues,
					PlaylistTable.ID + "=?",
					new String[] { "" + playlist.getId() });
		} finally {
			closeDatabase();
		}
	}

	@Override
	public long createPlaylist(String title, List<TrackDO> tracks) {
		ContentValues playlistValues = newPlaylist(title);
		long newPlaylistId;

		openDatabase();
		try {
			database.beginTransaction();
			newPlaylistId = database.insert(PlaylistTable.TABLE_NAME,
					PlaylistTable.MODIFIED, playlistValues);
			ContentValues[] trackToPlaylist = fromTracklist(newPlaylistId,
					tracks);
			for (ContentValues track : trackToPlaylist) {
				database.insert(TrackToPlaylistTable.TABLE_NAME,
						TrackToPlaylistTable.PLAYLIST_ID, track);
			}
			database.setTransactionSuccessful();
			PlaylistDO createdPlaylist = new PlaylistDO(newPlaylistId, title,
					tracks);
			playlistCache.put(newPlaylistId, createdPlaylist);
		} catch (Exception e) {
			newPlaylistId = Error.PLAYLIST_NOT_CREATED;
			Logger.e(TAG, e.getMessage(), e);
		} finally {
			database.endTransaction();
			closeDatabase();
		}

		return newPlaylistId;
	}

	private void openDatabase() {
		database = databaseHelper.getWritableDatabase();
	}

	private void closeDatabase() {
		databaseHelper.close();
	}

	private void fetchAllTracksFromSdCard() {

		// Perform a query on the content resolver. The URI we're passing
		// specifies that we
		// want to query for all audio media on external storage (e.g. SD card)
		Cursor cur = contentResolver.query(MEDIA_URI, null,
				MediaStore.Audio.Media.IS_MUSIC + " = 1", null, null);
		Log.i(TAG, "Query finished. "
				+ (cur == null ? "Returned NULL." : "Returned a cursor."));

		if (cur == null) {
			// Query failed...
			Logger.e(TAG, "Failed to retrieve music: cursor is null :-(");
			allTracksPlaylist = PlaylistDO.Null;
			return;
		}
		if (!cur.moveToFirst()) {
			// Nothing to query. There is no music on the device. How boring.
			Logger.e(TAG,
					"Failed to move cursor to first row (no query results).");
			allTracksPlaylist = PlaylistDO.Null;
			return;
		}

		Log.i(TAG, "Listing...");

		allTracksPlaylist = new PlaylistDO(ALL_TRACKS,
				EMPApplication.context.getString(R.string.all_tracks));

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

			allTracksPlaylist.add(new TrackDO(cur.getLong(idColumn), cur
					.getString(artistColumn), cur.getString(titleColumn), cur
					.getString(albumColumn), cur.getLong(albumIdColumn), cur
					.getLong(durationColumn)));

		} while (cur.moveToNext());

		Log.i(TAG, "Done querying media. EMPMusicManager is ready.");
	}

	@Override
	public Cursor getTracks() {
		return contentResolver.query(MEDIA_URI, null,
				MediaStore.Audio.Media.IS_MUSIC + " = 1", null,
				MediaStore.Audio.Media.TITLE_KEY);
	}
	
	public Cursor getTrack(long trackId) {
		return contentResolver.query(MEDIA_URI, null, 
				"_id = ? and is_music = 1", new String[]{""+trackId},
				null);
	}

	@Override
	public Cursor getArtists() {
		String[] cols = { MediaStore.Audio.Artists._ID,
				MediaStore.Audio.Artists.ARTIST,
				MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
				MediaStore.Audio.Artists.NUMBER_OF_TRACKS };

		return contentResolver.query(ARTISTS_URI, cols, null, null,
				MediaStore.Audio.Artists.ARTIST_KEY);
	}

	@Override
	public Cursor getAlbums() {
		String[] cols = { MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM,
				MediaStore.Audio.Albums.ALBUM_ART,
				MediaStore.Audio.Media.ARTIST, 
				MediaStore.Audio.Albums.NUMBER_OF_SONGS };

		return contentResolver.query(ALBUMS_URI, cols, null, null,
				MediaStore.Audio.Albums.ALBUM_KEY);
	}

	public List<ArtistDO> getArtistsInfo() {
		List<ArtistDO> artistList = new ArrayList<ArtistDO>();

		String[] cols = { MediaStore.Audio.Artists._ID,
				MediaStore.Audio.Artists.ARTIST,
				MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
				MediaStore.Audio.Artists.NUMBER_OF_TRACKS };

		Cursor artistToAlbumCursor = contentResolver.query(ARTISTS_URI, cols,
				null, null, MediaStore.Audio.Artists.ARTIST);
		if (artistToAlbumCursor != null && artistToAlbumCursor.moveToFirst()) {
			int artistIdColumn = artistToAlbumCursor
					.getColumnIndex(MediaStore.Audio.Artists._ID);
			int artistColumm = artistToAlbumCursor
					.getColumnIndex(MediaStore.Audio.Artists.ARTIST);
			int artistAlbumCountColumn = artistToAlbumCursor
					.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS);
			int artistTrackCountColumn = artistToAlbumCursor
					.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS);
			do {
				ArtistDO artistDO = new ArtistDO(
						artistToAlbumCursor.getInt(artistIdColumn),
						artistToAlbumCursor.getString(artistColumm),
						artistToAlbumCursor.getInt(artistAlbumCountColumn),
						artistToAlbumCursor.getInt(artistTrackCountColumn));

				artistList.add(artistDO);
			} while (artistToAlbumCursor.moveToNext());
		}
		return artistList;
	}

	public List<AlbumDO> getAllAlbumsInfo() {
		List<AlbumDO> albumsList = new ArrayList<AlbumDO>();

		String[] cols = { MediaStore.Audio.Albums.ALBUM,
				MediaStore.Audio.Albums.ALBUM_ART,
				MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Albums._ID,
				MediaStore.Audio.Albums.NUMBER_OF_SONGS };

		Cursor albumsCursor = contentResolver.query(ALBUMS_URI, cols, null,
				null, MediaStore.Audio.Media.ARTIST);
		// SELECT IFNULL(title, "------") AS title FROM books;
		Log.d(TAG, "" + albumsCursor.getCount());

		if (albumsCursor != null && albumsCursor.moveToFirst()) {
			Log.d(TAG, "Managing cursor");

			int albumIdColumn = albumsCursor
					.getColumnIndex(MediaStore.Audio.Albums._ID);
			int albumColumm = albumsCursor
					.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
			int albumArtistColumn = albumsCursor
					.getColumnIndex(MediaStore.Audio.Media.ARTIST);
			int albumSongNumberColumn = albumsCursor
					.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS);
			do {
				AlbumDO album = new AlbumDO(albumsCursor.getInt(albumIdColumn),
						albumsCursor.getString(albumColumm),
						albumsCursor.getString(albumArtistColumn),
						albumsCursor.getInt(albumSongNumberColumn));
				albumsList.add(album);
			} while (albumsCursor.moveToNext());
		}

		return albumsList;
	}

	public List<AlbumDO> getAlbumsFromArtist(int artistId) {
		List<AlbumDO> albumsList = new ArrayList<AlbumDO>();
		String[] cols = { MediaStore.Audio.Albums.ALBUM,
				MediaStore.Audio.Albums.ALBUM_ART,
				MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Albums._ID,
				MediaStore.Audio.Albums.NUMBER_OF_SONGS };

		Cursor albumsCursor = contentResolver.query(ALBUMS_URI, cols,
				MediaStore.Audio.Artists._ID + "=?", new String[] { ""
						+ artistId }, MediaStore.Audio.Media.ARTIST);

		if (albumsCursor != null && albumsCursor.moveToFirst()) {
			Log.d(TAG, "Managing cursor");

			int albumIdColumn = albumsCursor
					.getColumnIndex(MediaStore.Audio.Albums._ID);
			int albumColumm = albumsCursor
					.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
			int albumArtistColumn = albumsCursor
					.getColumnIndex(MediaStore.Audio.Media.ARTIST);
			int albumSongNumberColumn = albumsCursor
					.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS);
			do {
				AlbumDO album = new AlbumDO(albumsCursor.getInt(albumIdColumn),
						albumsCursor.getString(albumColumm),
						albumsCursor.getString(albumArtistColumn),
						albumsCursor.getInt(albumSongNumberColumn));
				albumsList.add(album);
			} while (albumsCursor.moveToNext());
		}

		return albumsList;
	}

	public TrackDO getTrackById(long trackId) {
		TrackDO track = allTracksPlaylist.getById(trackId);
		return track;
	}

	private ContentValues newPlaylist(String title) {
		long time = System.currentTimeMillis();
		ContentValues playlistValues = new ContentValues();
		playlistValues.put(PlaylistTable.TITLE, title);
		playlistValues.put(PlaylistTable.CREATED, "" + time);
		playlistValues.put(PlaylistTable.MODIFIED, "" + time);
		return playlistValues;
	}

	private ContentValues fromPlaylist(PlaylistDO playlist) {
		ContentValues playlistValues = new ContentValues();
		playlistValues.put(PlaylistTable.TITLE, playlist.getTitle());
		playlistValues.put(PlaylistTable.CREATED, playlist.getCreatedTime());
		playlistValues.put(PlaylistTable.MODIFIED, playlist.getModifiedTime());
		return playlistValues;
	}

	private ContentValues[] fromTracklist(long playlistId,
			List<TrackDO> trackList) {
		int size = trackList.size();
		ContentValues[] trackToPlaylistValues = new ContentValues[size];

		for (int i = 0; i < size; i++) {
			ContentValues values = new ContentValues();
			values.put(TrackToPlaylistTable.PLAYLIST_ID, "" + playlistId);
			values.put(TrackToPlaylistTable.TRACK_ID, ""
					+ trackList.get(i).getId());

			trackToPlaylistValues[i] = values;
		}
		return trackToPlaylistValues;
	}

	@Override
	public void addOnPlaylistsInfoChangedListener(
			OnPlaylistsInfoChangedListener listener) {
		playlistsInfoListeners.add(listener);
	}

	@Override
	public void addOnPlaylistChangedListener(
			OnPlaylistChangedListener listener, int playlistId) {
		playlistChangedListeners.put(playlistId, listener);
	}

}

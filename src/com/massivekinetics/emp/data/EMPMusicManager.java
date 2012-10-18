package com.massivekinetics.emp.data;

import java.util.ArrayList;
import java.util.List;

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
import com.massivekinetics.emp.data.entities.Playlist;
import com.massivekinetics.emp.data.entities.Track;
import com.massivekinetics.emp.data.listeners.OnPlaylistChangedListener;
import com.massivekinetics.emp.data.listeners.OnPlaylistsInfoChangedListener;
import com.massivekinetics.emp.interfaces.AbstractMusicManager;
import com.massivekinetics.emp.logger.Logger;

public class EMPMusicManager extends AbstractMusicManager {

	private final static String TAG = "EMPMusicManager";

	private static Playlist allTracksPlaylist;
	private static boolean isInitialized = false;

	// TODO: play with cache
	private SparseArray<Playlist> playlistCache;
	private SparseArray<OnPlaylistChangedListener> playlistChangedListeners;
	private List<OnPlaylistsInfoChangedListener> playlistsInfoListeners;

	private ContentResolver contentResolver;

	private SQLiteDatabase database;
	private DatabaseHelper databaseHelper;

	private static final Uri MEDIA_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	private static final Uri ALBUMS_URI = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
	private static final Uri ARTISTS_URI = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;

	public EMPMusicManager() {
		databaseHelper = new DatabaseHelper(EMPApplication.context);
	}

	@Override
	public void init() {
		playlistCache = new SparseArray<Playlist>();
		playlistChangedListeners = new SparseArray<OnPlaylistChangedListener>();
		playlistsInfoListeners = new ArrayList<OnPlaylistsInfoChangedListener>();
		contentResolver = EMPApplication.context.getContentResolver();
		fetchAllTracksFromSdCard();
		fetchPlaylistCache();
		isInitialized = true;
	}

	private void fetchPlaylistCache() {
		openDatabase();
		try {
			Cursor cursor = database.query(PlaylistTable.TABLE_NAME,
					PlaylistTable.COLUMNS, null, null, null, null, null);
			if (cursor != null && cursor.moveToFirst()) {
				do {
					Playlist playlist = getPlaylistFromCursor(cursor);
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

	private void fetchPlaylistWithTracks(final Playlist playlist) {
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

				List<Track> trackList = getTrackListFromIds(ids);
				playlist.setTracks(trackList);
			}
		} finally {
			closeDatabase();
		}
	}

	private List<Track> getTrackListFromIds(int[] ids) {
		List<Track> trackList = new ArrayList<Track>();

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
				trackList.add(new Track(cur.getInt(idColumn), cur
						.getString(artistColumn), cur.getString(titleColumn),
						cur.getString(albumColumn), cur.getLong(albumIdColumn),
						cur.getLong(durationColumn)));
			} while (cur.moveToNext());

		}

		return trackList;
	}

	@Override
	public Playlist getPlaylist(int playlistId) {
		if (!isInitialized)
			throw new RuntimeException(TAG
					+ " getPlaylist() : EMPMusicManager isn't initialized!");

		if (playlistId == ALL_TRACKS)
			return allTracksPlaylist;

		Playlist result = playlistCache.get(playlistId);
		if (result != null)
			return result;

		openDatabase();
		Cursor cursor = database.query(PlaylistTable.TABLE_NAME,
				PlaylistTable.COLUMNS, PlaylistTable.ID + "=?",
				new String[] { "" + playlistId }, null, null, null);

		try {
			if (cursor == null || !cursor.moveToFirst())
				result = Playlist.Null;
			else {
				result = getPlaylistFromCursor(cursor);
				playlistCache.put(playlistId, result);
			}

			fetchPlaylistWithTracks(result);

		} finally {
			closeDatabase();
		}
		return result;
	}

	private Playlist getPlaylistFromCursor(Cursor cur) {

		int idColumn = cur.getColumnIndex(PlaylistTable.ID);
		int titleColumn = cur.getColumnIndex(PlaylistTable.TITLE);
		int createdColumn = cur.getColumnIndex(PlaylistTable.CREATED);
		int modifiedColumn = cur.getColumnIndex(PlaylistTable.MODIFIED);

		return new Playlist(cur.getString(titleColumn), cur.getInt(idColumn),
				Long.parseLong(cur.getString(createdColumn)),
				Long.parseLong(cur.getString(modifiedColumn)));

	}

	@Override
	public List<Playlist> getPlaylists() {
		List<Playlist> res = new ArrayList<Playlist>();
		for (int i = 0; i < playlistCache.size(); i++)
			res.add(playlistCache.valueAt(i));
		return res;
	}

	@Override
	public long deletePlaylist(int playlistId) {
		openDatabase();
		// default value. If delete operation is successful then this method
		// returns ID of deleted row
		long id = OPERATION_ERROR;
		try {
			id = database.delete(PlaylistTable.TABLE_NAME, PlaylistTable.ID
					+ "=?", new String[] { "" + playlistId });
		} finally {
			closeDatabase();
		}

		return id;
	}

	@Override
	public void deleteTrackFromPlaylist(int trackId, int playlistId) {
		openDatabase();

		long id = OPERATION_ERROR;
		try {
			id = database.delete(TrackToPlaylistTable.TABLE_NAME,
					TrackToPlaylistTable.PLAYLIST_ID + "=? and "
							+ TrackToPlaylistTable.PLAYLIST_ID + "=?",
					new String[] { "" + playlistId, "" + trackId });
		} finally {
			closeDatabase();
		}
	}

	@Override
	public void updatePlaylist(Playlist playlist) {
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
	public long createPlaylist(String title, List<Track> tracks) {
		ContentValues playlistValues = newPlaylist(title);
		long newPlaylistId = -1;

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
			Playlist createdPlaylist = new Playlist(title, (int) newPlaylistId,
					tracks);
			playlistCache.put((int) newPlaylistId, createdPlaylist);
		} catch (Exception e) {
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
			allTracksPlaylist = Playlist.Null;
			return;
		}
		if (!cur.moveToFirst()) {
			// Nothing to query. There is no music on the device. How boring.
			Logger.e(TAG,
					"Failed to move cursor to first row (no query results).");
			allTracksPlaylist = Playlist.Null;
			return;
		}

		Log.i(TAG, "Listing...");

		allTracksPlaylist = new Playlist(
				EMPApplication.context.getString(R.string.all_tracks),
				EMPMusicManager.ALL_TRACKS);

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

			allTracksPlaylist.add(new Track(cur.getLong(idColumn), cur
					.getString(artistColumn), cur.getString(titleColumn), cur
					.getString(albumColumn), cur.getLong(albumIdColumn), cur
					.getLong(durationColumn)));

		} while (cur.moveToNext());

		Log.i(TAG, "Done querying media. EMPMusicManager is ready.");
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

	public Track getTrackById(long trackId) {
		Track track = allTracksPlaylist.getById(trackId);
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

	private ContentValues fromPlaylist(Playlist playlist) {
		ContentValues playlistValues = new ContentValues();
		playlistValues.put(PlaylistTable.TITLE, playlist.getTitle());
		playlistValues.put(PlaylistTable.CREATED, playlist.getCreatedTime());
		playlistValues.put(PlaylistTable.MODIFIED, playlist.getModifiedTime());
		return playlistValues;
	}

	private ContentValues[] fromTracklist(long playlistId, List<Track> trackList) {
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

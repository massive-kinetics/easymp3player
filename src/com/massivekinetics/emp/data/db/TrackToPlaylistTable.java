package com.massivekinetics.emp.data.db;

public class TrackToPlaylistTable {

	public static final String TABLE_NAME = "tracktoplaylist";

	public static final String ID = "_id";
	public static final String PLAYLIST_ID = "playlist_id";
	public static final String TRACK_ID = "track_id";

	public static final String[] COLUMNS = { ID, PLAYLIST_ID, TRACK_ID };
	
	public static final int DATABASE_VERSION = 1;

	public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME
			+ " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + PLAYLIST_ID
			+ " INTEGER, " + TRACK_ID + " INTEGER);";

	public static final String TABLE_DROP = "DROP TABLE IF EXISTS "
			+ TABLE_NAME;

}

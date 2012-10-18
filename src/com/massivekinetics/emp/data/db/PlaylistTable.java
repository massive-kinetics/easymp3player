package com.massivekinetics.emp.data.db;

public class PlaylistTable {

	public static final String TABLE_NAME = "playlists";
	public static final String ID = "_id";
	public static final String TITLE = "title";
	public static final String CREATED = "created";
	public static final String MODIFIED = "modified";

	public static final String[] COLUMNS = { ID, TITLE, CREATED,
			MODIFIED };

	public static final int DATABASE_VERSION = 1;

	public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME
			+ " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TITLE
			+ " TEXT, " + CREATED + " TEXT, "
			+ MODIFIED + " TEXT);";

	public static final String TABLE_DROP = "DROP TABLE IF EXISTS "
			+ TABLE_NAME;

}

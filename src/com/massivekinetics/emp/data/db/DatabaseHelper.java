package com.massivekinetics.emp.data.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "emp_db";
	public static final int DATABASE_VERSION = 1;
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(PlaylistTable.TABLE_CREATE);
		db.execSQL(TrackToPlaylistTable.TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(PlaylistTable.TABLE_DROP);
		db.execSQL(TrackToPlaylistTable.TABLE_DROP);
		onCreate(db);
	}

}

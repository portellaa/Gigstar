package org.ua.gigstar.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "Gigstar.s3db";
	private static final int DATABASE_VERSION = 1;
	
	public static final String TABLE_ARTIST = "artist";
	public static final String COLUMN_ID = "_id";
	public static final String ARTIST_COLUMN_MBID = "mbid";
	public static final String ARTIST_COLUMN_NAME = "name";
	public static final String ARTIST_COLUMN_PIC = "pic";
	public static final String ARTIST_COLUMN_BIO = "bio";
	
	public static final String TABLE_EVENT = "event";
	public static final String EVENT_COLUMN_ID = "eventid";
	public static final String EVENT_COLUMN_TITLE = "title";
	public static final String EVENT_COLUMN_LINK = "link";
	public static final String EVENT_COLUMN_STARTDATE = "startdate";
	public static final String EVENT_COLUMN_STARTTIME = "starttime";
	public static final String EVENT_COLUMN_ENDDATE = "enddate";
	public static final String EVENT_COLUMN_ENDTIME = "endtime";
	public static final String EVENT_COLUMN_LOCATION = "location"; 
	
	private final String ARTIST_TABLE_CREATE = "create table " + TABLE_ARTIST + "( "
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ ARTIST_COLUMN_MBID + " text not null,"
			+ ARTIST_COLUMN_NAME + " text not null,"
			+ ARTIST_COLUMN_PIC + " text not null,"
			+ ARTIST_COLUMN_BIO + " text not null);";
	private final String EVENT_TABLE_CREATE = "create table " + TABLE_EVENT + " ("
			+ COLUMN_ID + " integer primary key autoincrement,"
			+ EVENT_COLUMN_ID + " text not null,"
			+ EVENT_COLUMN_TITLE + " text not null,"
			+ EVENT_COLUMN_LINK + " text not null,"
			+ EVENT_COLUMN_STARTDATE + " text not null,"
			+ EVENT_COLUMN_STARTTIME + " text,"
			+ EVENT_COLUMN_ENDDATE + " text,"
			+ EVENT_COLUMN_ENDTIME + " text,"
			+ EVENT_COLUMN_LOCATION + " text not null);";

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(ARTIST_TABLE_CREATE);
		database.execSQL(EVENT_TABLE_CREATE);
		Log.d(MySQLiteHelper.class.getName(), "Executed on create");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTIST);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
		onCreate(db);
	}

}

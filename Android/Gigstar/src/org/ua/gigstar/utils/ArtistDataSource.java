package org.ua.gigstar.utils;

import java.util.ArrayList;
import java.util.List;

import org.ua.gigstar.entities.Artist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ArtistDataSource {
	
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.ARTIST_COLUMN_MBID, MySQLiteHelper.ARTIST_COLUMN_NAME, MySQLiteHelper.ARTIST_COLUMN_PIC, MySQLiteHelper.ARTIST_COLUMN_BIO };
	
	public ArtistDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	public Artist createArtist(String mbid, String name, String pic_url, String bio) {
		
		ContentValues values = new ContentValues();
		
		values.put(MySQLiteHelper.ARTIST_COLUMN_MBID, mbid);
		values.put(MySQLiteHelper.ARTIST_COLUMN_NAME, name);
		values.put(MySQLiteHelper.ARTIST_COLUMN_PIC, pic_url);
		values.put(MySQLiteHelper.ARTIST_COLUMN_BIO, bio);
		
		long insertId = database.insert(MySQLiteHelper.TABLE_ARTIST, null, values);
		Log.d(ArtistDataSource.class.getName(),"Insert ID: " + insertId);
		
		Cursor cursor = database.query(MySQLiteHelper.TABLE_ARTIST, allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		
		Artist newArtist = cursorToArtist(cursor);
		cursor.close();
		
		return newArtist;
	}
	
	public Artist createArtist(Artist artist) {
		return createArtist(artist.getMbid(), artist.getName(), artist.getPicture_url(), artist.getBiography());
	}
	
	public void deleteArtist(Artist artist) {
		
		long id = artist.getId();
		
		Log.i(ArtistDataSource.class.getName(), "Artist deleted with id: " + id);
		
		database.delete(MySQLiteHelper.TABLE_ARTIST, MySQLiteHelper.COLUMN_ID + " = " + id, null);
	}

	public List<Artist> getAllArtists() {
		List<Artist> artitsList = new ArrayList<Artist>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_ARTIST, allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Artist artist = cursorToArtist(cursor);
			artitsList.add(artist);
			cursor.moveToNext();
		}

		cursor.close();
		return artitsList;
	}
	
	public Artist getArtistByMBID(String mbid) {
		
		Artist newArtist = null;
		
		Cursor cursor = database.query(MySQLiteHelper.TABLE_ARTIST, allColumns, MySQLiteHelper.ARTIST_COLUMN_MBID + " = '" + mbid + "'", null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			newArtist = cursorToArtist(cursor);
			cursor.close();
		}
		
		return newArtist;
	}
	
	public List<Artist> filterArtists(String name) {
		List<Artist> artitsList = new ArrayList<Artist>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_ARTIST, allColumns, MySQLiteHelper.ARTIST_COLUMN_NAME + " LIKE '%" + name + "%'", null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Artist artist = cursorToArtist(cursor);
			artitsList.add(artist);
			cursor.moveToNext();
		}

		cursor.close();
		return artitsList;
	}

	private Artist cursorToArtist(Cursor cursor) {
		Artist artist = new Artist();
		artist.setId(cursor.getLong(0));
		artist.setMbid(cursor.getString(1));
		artist.setName(cursor.getString(2));
		artist.setPicture_url(cursor.getString(3));
		artist.setBiography(cursor.getString(4));
		return artist;
	}

}

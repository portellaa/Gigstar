package org.ua.gigstar.utils;

import java.util.ArrayList;
import java.util.List;

import org.ua.gigstar.entities.Event;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class EventDataSource {
	
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.EVENT_COLUMN_ID, MySQLiteHelper.EVENT_COLUMN_TITLE, MySQLiteHelper.EVENT_COLUMN_LINK, 
			MySQLiteHelper.EVENT_COLUMN_STARTDATE, MySQLiteHelper.EVENT_COLUMN_STARTTIME,
			MySQLiteHelper.EVENT_COLUMN_ENDDATE, MySQLiteHelper.EVENT_COLUMN_ENDTIME, MySQLiteHelper.EVENT_COLUMN_LOCATION};
	
	public EventDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	public Event createEvent(String eventid, String title, String link, String startdate, String enddate, String starttime, String endtime, String location) {
		
		ContentValues values = new ContentValues();
		
		values.put(MySQLiteHelper.EVENT_COLUMN_ID, eventid);
		values.put(MySQLiteHelper.EVENT_COLUMN_TITLE, title);
		values.put(MySQLiteHelper.EVENT_COLUMN_LINK, link);
		values.put(MySQLiteHelper.EVENT_COLUMN_STARTDATE, startdate);
		values.put(MySQLiteHelper.EVENT_COLUMN_STARTTIME, starttime);
		values.put(MySQLiteHelper.EVENT_COLUMN_ENDDATE, enddate);
		values.put(MySQLiteHelper.EVENT_COLUMN_ENDTIME, endtime);
		values.put(MySQLiteHelper.EVENT_COLUMN_LOCATION, location);
		
		long insertId = database.insert(MySQLiteHelper.TABLE_EVENT, null, values);
		Log.d(EventDataSource.class.getName(),"Insert ID: " + insertId);
		
		Cursor cursor = database.query(MySQLiteHelper.TABLE_EVENT, allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		
		Event newEvent = cursorToEvent(cursor);
		cursor.close();
		
		return newEvent;
	}
	
	public Event createEvent(Event event) {
		return createEvent(event.getEventID(), event.getName(), event.getUri(), event.getStartDate(), event.getEndDate(), event.getStartTime(), event.getEndTime(), event.getLocationName());
	}
	
	public void deleteEvent(Event event) {
		
		long id = event.getId();
		
		Log.i(EventDataSource.class.getName(), "Event deleted with id: " + id);
		
		database.delete(MySQLiteHelper.TABLE_EVENT, MySQLiteHelper.COLUMN_ID + " = " + id, null);
	}

	public List<Event> getAllEvents() {
		List<Event> artitsList = new ArrayList<Event>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_EVENT, allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Event event = cursorToEvent(cursor);
			artitsList.add(event);
			cursor.moveToNext();
		}

		cursor.close();
		return artitsList;
	}
	
	public Event getEventByID(String eventid) {
		
		Event newEvent = null;
		
		Cursor cursor = database.query(MySQLiteHelper.TABLE_EVENT, allColumns, MySQLiteHelper.EVENT_COLUMN_ID + " = " + "'" + eventid + "'", null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			newEvent = cursorToEvent(cursor);
			cursor.close();
		}
		
		return newEvent;
		
	}

	private Event cursorToEvent(Cursor cursor) {
		Event event = new Event();
		event.setId(cursor.getLong(0));
		event.setEventID(cursor.getString(1));
		event.setName(cursor.getString(2));
		event.setUri(cursor.getString(3));
		event.setStartDate(cursor.getString(4));
		event.setStartTime(cursor.getString(5));
		event.setEndDate(cursor.getString(6));
		event.setEndTime(cursor.getString(7));
		event.setLocationName(cursor.getString(8));
		return event;
	}

}

package org.ua.gigstar.wsclients;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ua.gigstar.entities.Artist;
import org.ua.gigstar.entities.Event;
import org.ua.gigstar.utils.GigGeoPoint;

import android.util.Log;

public class GigEventsLocator {
	
	private final String apiURL = "api.songkick.com";
	private final String apiKey = "apikey=p6G0ajVMJ65yAMJP";
	
	private HttpClient httpClient;
	private HttpGet httpGet;
	private HttpResponse response;
	
	private StringBuilder stringResult;
	

	public GigEventsLocator() {
		
		this.httpClient = new DefaultHttpClient();		
		this.stringResult = new StringBuilder();
	}
	
	public List<Event> getFromLocationName(String latitude, String longitude) throws ClientProtocolException, IOException {
		
		List<Event> list = null;
		
		try {
			this.httpGet = new HttpGet(URIUtils.createURI("http", apiURL, 80, "api/3.0/events.json", apiKey + "&location=geo:"+latitude+","+longitude, null));
			Log.d(GigEventsLocator.class.getName(), this.httpGet.getURI().toURL().toString());
		} catch (URISyntaxException e) {
			throw new IOException();
		}
		
		this.response = this.httpClient.execute(httpGet);
		HttpEntity entity = response.getEntity();
		
		InputStream resultStream = entity.getContent();
		
		int b;
		while ((b = resultStream.read()) != -1) {
			this.stringResult.append((char) b);
		}
		
		Log.d(GigEventsLocator.class.getName(), this.stringResult.toString());
		
		JSONArray jsonEvents, performers;
		JSONObject tmpJSONObj, venue, start, end, performer;
		try {
			jsonEvents = new JSONObject(this.stringResult.toString()).getJSONObject("resultsPage").getJSONObject("results").getJSONArray("event");
		} catch (JSONException e) {
			Log.e("Gigstar", "Error processing full result data");
			return null;
		}
		
		List<Artist> performersList;
		
		list = new ArrayList<Event>(jsonEvents.length());
		Event tmpEvent;
		Artist tmpArtist;
		
		for (int i = 0; i < jsonEvents.length(); i++) {
			try {
				tmpJSONObj = jsonEvents.getJSONObject(i);
			} catch (JSONException e) {
				return null;
			}
			tmpEvent = new Event();
			
			try {
				
				tmpEvent.setEventID(tmpJSONObj.getString("id"));
				tmpEvent.setName(tmpJSONObj.getString("displayName"));
				tmpEvent.setUri(tmpJSONObj.getString("uri"));
				tmpEvent.setType(tmpJSONObj.getString("type"));
				
//				Log.i("Gigstar", "Processed normal information");
				
				try {
					venue = tmpJSONObj.getJSONObject("venue");
					if ((venue.getString("lat") == null) || (venue.getString("lat").equalsIgnoreCase("null"))) {
						venue = tmpJSONObj.getJSONObject("location");
						tmpEvent.setLocationName(venue.getString("city"));
					}
					else tmpEvent.setLocationName(venue.getJSONObject("metroArea").getString("displayName"));
					
					tmpEvent.setLocation(new GigGeoPoint((int)(venue.getDouble("lat") * 1E6), (int)(venue.getDouble("lng") * 1E6)));
					
//					Log.i("Gigstar", "point: lat: " + venue.getDouble("lat") + "  | long: " + venue.getDouble("lng"));
				} catch (JSONException e) {
					Log.e("Gigstar", "Error parsing location " + e.getMessage());
					venue = tmpJSONObj.getJSONObject("location");
					tmpEvent.setLocation(new GigGeoPoint((int)(venue.getDouble("lat") * 1E6), (int)(venue.getDouble("lng") * 1E6)));
					tmpEvent.setLocationName(venue.getString("city"));
				}
				
//				Log.i("Gigstar", "Processed location information");
				
				performers = tmpJSONObj.getJSONArray("performance");
				performersList = new ArrayList<Artist>(performers.length());
				for (int j = 0; j < performers.length(); j++) {
					tmpArtist = new Artist();
					performer = performers.getJSONObject(j);
					tmpArtist.setName(performer.getString("displayName"));
					try {
						tmpArtist.setMbid(performer.getJSONObject("artist").getJSONArray("identifier").getJSONObject(0).getString("mbid"));
					} catch (JSONException e) {
						Log.e("Gigstar", "Error getting artist mbid");
					}
					performersList.add(tmpArtist);
				}
				tmpEvent.setPerformers(performersList);
				
//				Log.i("Gigstar", "Processed performers information");
				
				start = tmpJSONObj.getJSONObject("start");
				tmpEvent.setStartDate(start.getString("date"));
				tmpEvent.setStartTime(start.getString("time"));
				
				try {
					end = tmpJSONObj.getJSONObject("end");
					tmpEvent.setEndDate(end.getString("date"));
					tmpEvent.setEndTime(end.getString("time"));
				} catch (JSONException e) {
					Log.e("Gigstar", "There is no end tag...");
				}
				
//				Log.i("Gigstar", "Event processed: " + tmpEvent.toString());
				
				list.add(tmpEvent);
			} catch (JSONException e) {
				try {
					Log.e("Gigstar", "I = " + i + jsonEvents.getJSONObject(i).toString());
				} catch (JSONException e1) {
					Log.e("Gigstar", e1.getMessage());
					e1.printStackTrace();
				}
			}
		}
		return list;
	}
	
}

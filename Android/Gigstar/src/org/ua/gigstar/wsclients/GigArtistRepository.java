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

import android.util.Log;

public class GigArtistRepository {
	private static final String TAG = "ArtistRepository";
	
	private final String apiURL = "developer.echonest.com";
	private final String apiGetURL = "ws.audioscrobbler.com";
	
	private HttpClient httpClient;
	private HttpGet httpGet;
	private HttpResponse response;
	
	private StringBuilder stringResult;

	public GigArtistRepository() {
		this.httpClient = new DefaultHttpClient();		
		this.stringResult = new StringBuilder();
	}
	
	public List<Artist> findArtistWithQuery(String query, int max_results) throws ClientProtocolException, IOException {
		
		List<Artist> list = null;
		
		try {
			this.httpGet = new HttpGet(URIUtils.createURI("http", apiURL, 80, "api/v4/artist/search", "api_key=N6E4NIOVYMTHNDM8J&format=json&sort=familiarity-desc&bucket=id:musicbrainz&bucket=terms&name=" + query.replaceAll(" ", "%20") + "&results=" + max_results, null));
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
		
		JSONArray jsonGeoNames;
		try {
			jsonGeoNames = new JSONObject(this.stringResult.toString()).getJSONObject("response").getJSONArray("artists");
		} catch (JSONException e) {
			return null;
		}
		JSONObject tmpJSONObj;
		
		JSONArray terms;
		
		list = new ArrayList<Artist>(jsonGeoNames.length());
		Artist tmpArtist;
		
		List<String> termsList;
		
		for (int i = 0; i < jsonGeoNames.length(); i++) {
			try {
				tmpJSONObj = jsonGeoNames.getJSONObject(i);
			} catch (JSONException e) {
				return null;
			}
			
			tmpArtist = new Artist();
			
			try {
				
				String[] tmp = tmpJSONObj.getJSONArray("foreign_ids").getJSONObject(0).getString("foreign_id").split(":");
				tmpArtist.setMbid(tmp[tmp.length-1]);
				
				terms = tmpJSONObj.getJSONArray("terms");
				if (terms.length() > 0) {
					termsList = new ArrayList<String>(terms.length());
					for (int j = 0; j < terms.length(); j++) {
						termsList.add(terms.getJSONObject(i).getString("name"));
					}
					tmpArtist.setTags(termsList);
				}
				
				tmpArtist.setName(tmpJSONObj.getString("name"));
				
				list.add(tmpArtist);
			} catch (JSONException e) {
				try {
					Log.e(TAG, "I = " + i + jsonGeoNames.getJSONObject(i).toString());
				} catch (JSONException e1) {
					Log.e(TAG, e1.getMessage());
					e1.printStackTrace();
				}
			}
		}
		return list;
	}
	
	public Artist getArtistInformation(String mbid) throws IOException {
		
		Artist result = null;
		
		try {
			this.httpGet = new HttpGet(URIUtils.createURI("http", apiGetURL, 80, "2.0", "method=artist.getinfo&mbid=" + mbid + "&api_key=92acbb9ef2c30c614417cc9b9ce2ee0c&format=json", null));
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
		
		JSONObject tmpJSONObj, tmpImage;
		try {

			tmpJSONObj = new JSONObject(this.stringResult.toString());
			tmpJSONObj = tmpJSONObj.getJSONObject("artist");
		} catch (JSONException e) {
			Log.e("GigstarRep", e.getMessage());
			return null;
		}
		result = new Artist();
		
		JSONArray tmpImages;
		try {
			tmpImages = tmpJSONObj.getJSONArray("image");
			tmpImage = tmpImages.getJSONObject(tmpImages.length()-1);
			
			result.setPicture_url(tmpImage.getString("#text"));
			Log.d("GigstarArtistRep", "Picture getted: " + result.getPicture_url());
			
			result.setName(tmpJSONObj.getString("name"));

			result.setBiography(tmpJSONObj.getJSONObject("bio").getString("content"));
			
		} catch (JSONException e) {
			Log.e("GigstarRep", e.getMessage());
			return null;
		}
		
		return result;
	}
	
}

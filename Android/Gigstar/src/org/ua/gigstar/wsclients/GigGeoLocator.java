package org.ua.gigstar.wsclients;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

import android.location.Address;
import android.util.Log;

public class GigGeoLocator {
	
	private final String apiURL = "api.geonames.org";
	
	private HttpClient httpClient;
	private HttpGet httpGet;
	private HttpResponse response;
	
	private StringBuilder stringResult;
	

	public GigGeoLocator() {
		
		this.httpClient = new DefaultHttpClient();		
		this.stringResult = new StringBuilder();
	}
	
	public List<Address> getFromLocationName(String address, int results) throws ClientProtocolException, IOException {
		
		List<Address> list = null;
		
		try {
			this.httpGet = new HttpGet(URIUtils.createURI("http", apiURL, 80, "searchJSON", "username=meligaletiko&q="+address.replaceAll(" ", "%20")+"&maxRows="+results, null));
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
			jsonGeoNames = new JSONObject(this.stringResult.toString()).getJSONArray("geonames");
		} catch (JSONException e) {
			return null;
		}
		JSONObject tmpJSONObj;
		
		list = new ArrayList<Address>(jsonGeoNames.length());
		Address tmpAddr;
		
		for (int i = 0; i < jsonGeoNames.length(); i++) {
			try {
				tmpJSONObj = jsonGeoNames.getJSONObject(i);
			} catch (JSONException e) {
				return null;
			}
			tmpAddr = new Address(Locale.getDefault());
			
			try {
				tmpAddr.setAdminArea(tmpJSONObj.getString("adminCode1"));
				tmpAddr.setCountryName(tmpJSONObj.getString("countryName"));
				tmpAddr.setCountryCode(tmpJSONObj.getString("countryCode"));
				tmpAddr.setLatitude(tmpJSONObj.getDouble("lat"));
				tmpAddr.setLongitude(tmpJSONObj.getDouble("lng"));
				tmpAddr.setFeatureName(tmpJSONObj.getString("name"));
				
				list.add(tmpAddr);
			} catch (JSONException e) {
				try {
					Log.e("Gigstar", "I = " + i + jsonGeoNames.getJSONObject(i).toString());
				} catch (JSONException e1) {
					Log.e("Gigstar", e1.getMessage());
					e1.printStackTrace();
				}
			}
		}
		return list;
	}
	
}

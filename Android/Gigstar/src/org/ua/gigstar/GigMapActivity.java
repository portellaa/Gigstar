package org.ua.gigstar;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.ua.gigstar.entities.Event;
import org.ua.gigstar.utils.GigMapView;
import org.ua.gigstar.utils.GigMyOverlay;
import org.ua.gigstar.utils.GigOverlayItem;
import org.ua.gigstar.wsclients.GigEventsLocator;
import org.ua.gigstar.wsclients.GigGeoLocator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class GigMapActivity extends MapActivity {
	
	private final int MAP_ZOOM_LEVEL = 15;
	private final int MAP_VIEW = 1;
	private final int SEARCH_VIEW = 0;
	
	private GigMapView mapView;
	private ListView searchResultsListView;
	
	private MapController mapController;
	private LocationManager locationManager;
	private Object locationsSearch;
	private int currentView = MAP_VIEW;
	
	private GeoPoint myLocation = null;
	private GigMyOverlay myPosition = null;
	private GigOverlayItem eventsOverlays = null;
	private GigEventsLocator eventsHandler;
	private List<Overlay> mapOverlays;
	
	private EditText searchTextBox;
	private Button searchButton;

	private Button rightNavButton;
	
	private Drawable myPinPoint;
	private Drawable eventsPinPoint;
	
	private List<Address> locations = null;
	private List<Event> eventsList = null;

	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mapmain);
        
        myPinPoint = getResources().getDrawable(R.drawable.iosmaplocationpin);
    	eventsPinPoint = getResources().getDrawable(R.drawable.iosmappin);
        
        mapView = (GigMapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setSatellite(false);
        mapController = mapView.getController();
        
        mapView.setOnChangeListener(new MapViewChangeListener());
        
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        String provider = locationManager.getBestProvider(new Criteria(), true);
        
        locationManager.isProviderEnabled(provider);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, new GeoUpdateHandler());
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, new GeoUpdateHandler());
		
		mapOverlays = mapView.getOverlays();
		eventsOverlays = new GigOverlayItem(eventsPinPoint, this, this.mapView, this);
		eventsOverlays.setBalloonBottomOffset(10);
		
		eventsHandler = new GigEventsLocator();
		
		searchResultsListView = new ListView(this);
		searchResultsListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Log.i("Gigstar", "Pressing on item: " + arg2);
				Address tmpAddr = locations.get(arg2);
				centerMap(new GeoPoint((int)(tmpAddr.getLatitude() * 1E6), (int)(tmpAddr.getLongitude() * 1E6)));
				swapView(MAP_VIEW);
				GigstarActivity.getTitleTextView().requestFocus();
			}
		});
		
		/* Get the edittext and button on search bar */
		searchTextBox = (EditText) findViewById(R.id.searchTxtBox);
		searchButton = (Button) findViewById(R.id.searchButton);
		searchButton.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/HelveticaNeueBold.ttf"));
		
		searchResultsListView.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (locations != null) {
						searchResultsListView.setAdapter(new iOSAdapter(GigMapActivity.this, R.layout.ios_list, R.id.ioslisttext, locations));
						swapView(SEARCH_VIEW);
					}
				}
			}
		});
		
		searchTextBox.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				searchResultsListView.requestFocus();
			}
		});
		
		searchTextBox.addTextChangedListener(new TextWatcher() {
			
			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				Log.i("Gigstar", "Values: start: " + start + " | before: " + before + " | count: " + count);
//				
//				if ((start == 0) && (before == 0)) {
//					Log.d("Gigstar text Changed","position changed: " + SEARCH_VIEW);
//					swapView(SEARCH_VIEW);
//				    
//					if (Geocoder.isPresent())
//						locationsSearch = new Geocoder(GigMapActivity.this);
//					else locationsSearch = new GigGeoLocator();
//				}
//				else if ((start == 0) && (before == 1)) {
//					Log.d("Gigstar text Changed","view changed: " + MAP_VIEW);
//					swapView(MAP_VIEW);
//				}
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				Log.i("Gigstar", "Values 2: start: " + start + " | after: " + after + " | count: " + count);
				
			}
			
			public void afterTextChanged(Editable s) {
				
				if (searchTextBox.getText().length() > 0) {
					swapView(SEARCH_VIEW);
				}
				else swapView(MAP_VIEW);
				
			}
		});
		
		searchButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Log.i("Gigstar", "Search button pressed...");
				
				if (locationsSearch instanceof Geocoder) {
					Geocoder tmpLocation = (Geocoder) locationsSearch;
					Log.d("Gigstar search button", "Geocoder");
					try {
						locations = tmpLocation.getFromLocationName(searchTextBox.getText().toString(), 50);
					} catch (IOException e) {
						Log.e("Gigstar", "Error getting locations..." + e.getMessage());
						locationsSearch = new GigGeoLocator();
						locations = tryGigGeoList(searchTextBox.getText().toString());
					}
				}
				else {
					
					Log.d("Gigstar search button", "GigGeocoder");
					
					locations = tryGigGeoList(searchTextBox.getText().toString());
				}
				if (locations != null) {
					searchResultsListView.setAdapter(new iOSAdapter(GigMapActivity.this, R.layout.ios_list, R.id.ioslisttext, locations));
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		setUpNavButtons();
		super.onResume();
	}
	
	private void swapView(int sel) {
		
		if (this.currentView != sel) {
			
			Log.i("Gigstar", "Current View different...");
		
			if (sel == 0) {
				searchButton.setEnabled(true);
				ViewGroup parent = (ViewGroup) mapView.getParent();
				int index = parent.indexOfChild(mapView);
			    parent.removeView(mapView);
			    mapView.invalidate();
			    parent.addView(searchResultsListView, index);
			}
			else {
				searchButton.setEnabled(false);
				ViewGroup parent = (ViewGroup) searchResultsListView.getParent();
				int index = parent.indexOfChild(searchResultsListView);
			    parent.removeView(searchResultsListView);
			    searchResultsListView.invalidate();
			    parent.addView(mapView, index);
			}
			this.currentView = sel;
		}
	}
	
	private void centerMap(GeoPoint point) {
		goToPosition(point);
	}
	
	private List<Address> tryGigGeoList(String address) {
		
		Log.d("Gigstar gig geo list", "GeoList");
		
		List<Address> resultList = null;
		
//		if (locationsSearch instanceof GigGeoLocator) {
//			GigGeoLocator tmpLocation = (GigGeoLocator) locationsSearch;
		
		GigGeoLocator tmpLocation = new GigGeoLocator();
			
			Log.d("Gigstar gig geo list", address);
			
			try {
				resultList = tmpLocation.getFromLocationName(address, 10);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		}
		return resultList;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return true;
	}
	
	public class GeoUpdateHandler implements LocationListener {

		public void onLocationChanged(Location arg0) {
			
			Log.i("GigStar", "Location changed to: Latitude: " + arg0.getLatitude() + "  |  Longitude: " +arg0.getLongitude());
			
			if (myLocation == null) {
				
				GeoPoint newPos = new GeoPoint((int)(arg0.getLatitude() * 1E6), (int)(arg0.getLongitude() * 1E6));
				updateMyPosition(newPos);
				
				rightNavButton.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						moveToMyPosition();
						
					}
				});
				rightNavButton.setEnabled(true);
			}
			else {
				GeoPoint newPos = new GeoPoint((int)(arg0.getLatitude() * 1E6), (int)(arg0.getLongitude() * 1E6));
				updateMyPosition(newPos);
			}
			
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private void moveToMyPosition() {
		
		Log.i("Gigstar","moveToMyPosition");
		
		goToPosition(this.myLocation);
	}
	
	private void goToPosition(final GeoPoint location) {
		
		mapController.animateTo(location);
		mapController.setZoom(MAP_ZOOM_LEVEL);
		
		getEventsForPosition(location);
			
		mapView.postInvalidate();
	}
	
	private void getEventsForPosition(final GeoPoint position) {
		
		Log.i("Gigstar", "Getting events for position: " + position.toString());
		
		new FetchAsyncTask() {
			
			@Override
			protected void onPostExecute(java.util.List<Event> result) {
				
				mapView.getOverlays().remove(eventsOverlays);
				mapView.getOverlays().clear();
				eventsOverlays.clear();
				
				for (Event e : result) {
	    			Log.i("Gigstar", "Adding event: " + e.toString());
	    			eventsOverlays.addOverlay(new OverlayItem(e.getLocation().getGeoPoint(), e.getName(), e.getStartDate()), e);
	    			mapView.getOverlays().add(eventsOverlays);
	    			mapView.invalidate();
	    		}
				
			};
		}.execute(position);
		
////		new Thread() {
////			@Override
////			public void run() {
////				runOnUiThread(new Runnable() {
////    			    public void run() {
//    			    	try {
//    			    		mapView.getOverlays().remove(eventsOverlays);
////    			    		mapView.getOverlays().clear();
//    			    		eventsOverlays.clear();
//    			    		Log.i("Gigstar", "Calling web service.");
//							eventsList = eventsHandler.getFromLocationName(new Double(position.getLatitudeE6()/1E6).toString(), new Double(position.getLongitudeE6()/1E6).toString());
//							
//							Log.i("Gigstar", "Web service getted result");
//							if (eventsList != null) {
//
//	    			    		for (Event e : eventsList) {
//	    			    			Log.i("Gigstar", "Adding event: " + e.toString());
//	    			    			eventsOverlays.addOverlay(new OverlayItem(e.getLocation().getGeoPoint(), e.getName(), e.getStartDate()), e);
//	    			    		}
//
//	    			    		mapView.getOverlays().add(eventsOverlays);
////	    			    		mapView.invalidate();
//	    			    	}
//							
//						} catch (ClientProtocolException e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						} catch (IOException e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						}
////    			    }
////    			});
////			}
////		}.start();
		
	}
	
	private class FetchAsyncTask extends AsyncTask<GeoPoint, Integer, List<Event>> {

		@Override
		protected List<Event> doInBackground(GeoPoint... params) {
			
			List<Event> eventsList = null;
			
			try {
				
				eventsList = eventsHandler.getFromLocationName(new Double(params[0].getLatitudeE6()/1E6).toString(), new Double(params[0].getLongitudeE6()/1E6).toString());
				
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return eventsList;
		}
		
	}
	
	private void updateMyPosition(GeoPoint newPos) {
		
		Log.i("Gigstar","updateMyPosition");
		
    	if (myPosition != null) {
    		this.mapView.getOverlays().remove(myPosition);
    	}
    	
    	Log.i("Gigstar","updateMyPosition - Updating my location");
    	
    	this.myLocation = newPos;
    	
    	this.myPosition = new GigMyOverlay(myPinPoint);
    	this.myPosition.addOverlay(new OverlayItem(this.myLocation, "", ""));
    	Log.i("Gigstar","updateMyPosition - Overlay updated");
    	this.mapView.getOverlays().add(this.myPosition);
    	Log.i("Gigstar","updateMyPosition - Overlay added");
    	this.mapView.postInvalidate();
    	
	}
	
	protected class iOSAdapter extends ArrayAdapter<Address> {
		
		
		private final Activity con;
		private final List<Address> list;
		
		private int resource;
		private int textViewResourceId;
		
		public iOSAdapter(Activity context, int resource, int textViewResourceId, List<Address> objects) {
			super(context, resource, textViewResourceId, objects);
			
			this.con = context;
			this.list = objects;
			this.resource = resource;
			this.textViewResourceId = textViewResourceId;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			LayoutInflater inflater = this.con.getLayoutInflater();
			View rowView = inflater.inflate(resource, null, true);
			
			if (list.size() == 1) {
				rowView.setBackgroundDrawable(getResources().getDrawable(R.drawable.ios_list_single_row));
			}
			else {
				if (position == 0) {
					rowView.setBackgroundDrawable(getResources().getDrawable(R.drawable.ios_list_top_row));
				}
				else if (position == (list.size()-1)) {
					rowView.setBackgroundDrawable(getResources().getDrawable(R.drawable.ios_list_bottom_row));
				}
				else {
					rowView.setBackgroundDrawable(getResources().getDrawable(R.drawable.ios_list_med_row));
				}
			}
			
			rowView.findViewById(R.id.ioslistpict).setVisibility(View.GONE);
			
			TextView addrTxt = (TextView) rowView.findViewById(textViewResourceId);
			Address tmp = this.list.get(position);
			addrTxt.setText(tmp.getFeatureName() + ", " + tmp.getCountryName() + ", " + tmp.getCountryCode());
			
			return rowView;
		}
	}
	
	private class MapViewChangeListener implements GigMapView.OnChangeListener {

		public void onChange(MapView view, GeoPoint newCenter, GeoPoint oldCenter, int newZoom, int oldZoom) {
			if ((!newCenter.equals(oldCenter)) && (newZoom != oldZoom)) {
				Log.i("Gigstar", "Values changed... Center: " + newCenter + " | zoom: " + newZoom);
			}
			else if (!newCenter.equals(oldCenter)) {
				Log.i("Gigstar", "Values changed... Center: " + newCenter);
			}
			else if (newZoom != oldZoom) {
				Log.i("Gigstar", "Values changed... Zoom: " + newZoom);
			}
		}	
	}
	
	private void setUpNavButtons() {
		setupLeftButton();
		setupRightButton();
	}
	
	private void setupLeftButton() {
		GigstarActivity.getLeftButton().setVisibility(View.INVISIBLE);
		GigstarActivity.getLeftButton().setOnClickListener(null);
	}
	
	private void setupRightButton() {
		/* Gets instance of the right navigation button */
		if (rightNavButton == null) {
			rightNavButton = GigstarActivity.getRightButton();
			rightNavButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			
			BitmapDrawable location = (BitmapDrawable) this.getResources().getDrawable(R.drawable.location);
			rightNavButton.setCompoundDrawablesWithIntrinsicBounds(null, location, null, null);
			
			int padSize = (rightNavButton.getHeight() - location.getBitmap().getHeight())/2;
			
			Log.i("Gigstar", "Size of padding: " + padSize);
			rightNavButton.setPadding(0, (-1 * padSize) - 2, 0, 0);
			rightNavButton.setVisibility(View.VISIBLE);
		}
		else {
			rightNavButton.setVisibility(View.VISIBLE);
		}
		rightNavButton.setEnabled(false);
		
		if (myLocation != null) {
			rightNavButton.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					moveToMyPosition();
					
				}
			});
			rightNavButton.setEnabled(true);
		}
	}
	
	@Override
	public void onBackPressed() {
		
		if (this.currentView == SEARCH_VIEW) {
			swapView(MAP_VIEW);
		}
		else {
			super.onBackPressed();
		}
	}
	
}
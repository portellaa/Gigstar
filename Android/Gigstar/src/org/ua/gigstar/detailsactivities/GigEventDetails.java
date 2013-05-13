package org.ua.gigstar.detailsactivities;

import java.util.List;

import org.ua.gigstar.R;
import org.ua.gigstar.entities.Artist;
import org.ua.gigstar.entities.Event;
import org.ua.gigstar.utils.EventDataSource;
import org.ua.gigstar.utils.GigCustomActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class GigEventDetails extends GigCustomActivity {
	
	private TextView title, link, start, endLabel, end, location;
	private ListView artistList;
	
	private EventDataSource eventsPersist;
	private Event currentEvent = null;
	
	private List<Artist> listOfArtists;
	
	private boolean scheduled = false;
	
	Facebook facebook = new Facebook("ga0RGNYHvNM5d0SLGQfpQWAPGJ8=");
	private SharedPreferences gigPrefs;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.eventdetails, getResources().getString(R.string.eventsDetailsTitle));
        
        if ((currentEvent = (Event) this.getIntent().getSerializableExtra("event")) != null) {
        	
        	gigPrefs = getPreferences(MODE_PRIVATE);
    		
        	mapObjects();
        	fillData(currentEvent);
        	
        	eventsPersist = new EventDataSource(this);
        	
        	getRightButton().setText("+");
        	
        	eventsPersist.open();
        	if (eventsPersist.getEventByID(currentEvent.getEventID()) != null) {
        		this.scheduled = true;
        		getRightButton().setText("-");
        	}
        	eventsPersist.close();
        	
        	getRightButton().setVisibility(Button.VISIBLE);
    		getRightButton().setEnabled(true);
        	
        	getRightButton().setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					swapFollow();
				}
			});
        	
        	getLeftButton().setVisibility(TextView.VISIBLE);
        	getLeftButton().setText(this.getIntent().getStringExtra("backActiv"));
        	getLeftButton().setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					onBackPressed();
				}
			});
        	getLeftButton().setEnabled(true);
        }
        else this.finish();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		facebook.extendAccessTokenIfNeeded(this, null);
	}
	
	private void mapObjects() {
		title = (TextView) findViewById(R.id.eventstitle);
		link = (TextView) findViewById(R.id.eventslink);
		
//		startLabel = (TextView) findViewById(R.id.eventsstartlabel);
		start = (TextView) findViewById(R.id.eventsstartdate);
		
		endLabel = (TextView) findViewById(R.id.eventsendlabel);
		end = (TextView) findViewById(R.id.eventsenddate);
		
		location = (TextView) findViewById(R.id.eventlocation);
		
		artistList = (ListView) findViewById(R.id.eventartistslist);
	}
	
	private void fillData(Event e) {
		
		title.setText(e.getName());
		link.setText(Html.fromHtml("<a href=\"" + link + "\">" + getResources().getString(R.string.hiperLink) +"</a>"));
		link.setMovementMethod(LinkMovementMethod.getInstance());
		
		if (!((e.getStartDate() == null) || e.getStartDate().equalsIgnoreCase("null") || e.getStartDate().equalsIgnoreCase("")))
			start.setText(e.getStartDate());
		if (((e.getStartTime() == null) || e.getStartTime().equalsIgnoreCase("null") || e.getStartTime().equalsIgnoreCase("")))
			start.setText(start.getText() + " " + e.getStartTime());
		
		if (((e.getEndDate() == null) || e.getEndDate().equalsIgnoreCase("null") || e.getEndDate().equalsIgnoreCase("")) &&
				((e.getEndTime() == null) || e.getEndTime().equalsIgnoreCase("null") || e.getEndTime().equalsIgnoreCase(""))) {
			endLabel.setVisibility(TextView.GONE);
			end.setVisibility(TextView.GONE);
		}
		else {
			if (!((e.getEndDate() == null) || e.getEndDate().equalsIgnoreCase("null") || e.getEndDate().equalsIgnoreCase(""))) {
				end.setText(e.getEndDate());
			}
			if (((e.getEndTime() == null) || e.getEndTime().equalsIgnoreCase("null") || e.getEndTime().equalsIgnoreCase(""))) {
				end.setText(end.getText() + " " + e.getEndTime());
			}
		}
		
		location.setText(e.getLocationName());
		
		listOfArtists = e.getPerformers();
		
		if (listOfArtists != null) {
		
			Log.i("Gigstar", "Number of performers: " + listOfArtists.size());
			
			artistList.setAdapter(new iOSAdapter(this, R.layout.ios_list, listOfArtists));
			artistList.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					if (listOfArtists.get(arg2).getMbid() != null) {
						Intent tmp = new Intent(GigEventDetails.this, GigArtistDetails.class);
						tmp.putExtra("backActiv", getResources().getString(R.string.eventLabel));
						tmp.putExtra("artist", listOfArtists.get(arg2));
						startActivity(tmp);
					}
					else {
						Toast.makeText(GigEventDetails.this, getResources().getString(R.string.noMBIDError), 200).show();
					}
				}
			});
		}
		
	}
	
	private void swapFollow() {
		
		eventsPersist.open();
		if (scheduled) {
			eventsPersist.deleteEvent(this.currentEvent);
			getRightButton().setText("+");
			this.scheduled = false;
		}
		else {
			loginToFacebook();
			this.currentEvent = eventsPersist.createEvent(currentEvent);
			getRightButton().setText("-");
			this.scheduled = true;
			postToFacebook("");
		}
		eventsPersist.close();
	}
	
	protected class iOSAdapter extends ArrayAdapter<Artist> {
		
		
		private final Activity con;
		private final List<Artist> list;
		
		private int resource;
		
		public iOSAdapter(Activity context, int resource, List<Artist> objects) {
			super(context, resource, objects);
			
			this.con = context;
			this.list = objects;
			this.resource = resource;
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
			
			Artist tmp = this.list.get(position);
			
			ImageView iosPict = (ImageView) rowView.findViewById(R.id.ioslistpict);
			if (tmp.getPicture_url() != null) {
				iosPict.setVisibility(ImageView.GONE);
			}
			
			TextView listText = (TextView) rowView.findViewById(R.id.ioslisttext);
			
			if (tmp.getPicture_url() != null) {
				// Load Image
			}
			
			listText.setText(tmp.getName());
			
			return rowView;
		}
	}
	
	private void loginToFacebook() {
		String access_token = gigPrefs.getString("access_token", null);
        long expires = gigPrefs.getLong("access_expires", 0);
        
        if(access_token != null) {
            facebook.setAccessToken(access_token);
        }
        if(expires != 0) {
            facebook.setAccessExpires(expires);
        }

        if (!facebook.isSessionValid()) {
	        facebook.authorize(this, new String[] {"publish_checkins", "publish_stream", }, new DialogListener() {
	            public void onComplete(Bundle values) {
	            	SharedPreferences.Editor editor = gigPrefs.edit();
	                editor.putString("access_token", facebook.getAccessToken());
	                editor.putLong("access_expires", facebook.getAccessExpires());
	                editor.commit();
	            }
	
	            public void onFacebookError(FacebookError error) {
	            	Log.i(GigFacebookLogin.class.getName() + " Error", error.toString());
	            }
	
	            public void onError(DialogError e) {
	            	Log.i(GigFacebookLogin.class.getName() + " Error", e.toString());
	            }
	
	            public void onCancel() {
	            	Log.i(GigFacebookLogin.class.getName() + " Cancel", "canceled...");
	            }
	        });
        }
	}
	
	private void postToFacebook(String comment) {
		
		Bundle extras = new Bundle();
		
		extras.putString("link", currentEvent.getUri());
		extras.putString("name", currentEvent.getName());
	    facebook.dialog(this, "feed", extras, new Facebook.DialogListener() {
			
			public void onFacebookError(FacebookError e) {
				// TODO Auto-generated method stub
				
			}
			
			public void onError(DialogError e) {
				// TODO Auto-generated method stub
				
			}
			
			public void onComplete(Bundle values) {
				// TODO Auto-generated method stub
				
			}
			
			public void onCancel() {
				// TODO Auto-generated method stub
				
			}
		});
	}
}

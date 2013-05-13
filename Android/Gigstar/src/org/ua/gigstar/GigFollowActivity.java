package org.ua.gigstar;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.ua.gigstar.detailsactivities.GigArtistDetails;
import org.ua.gigstar.entities.Artist;
import org.ua.gigstar.utils.ArtistDataSource;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class GigFollowActivity extends Activity {
	
	private ArtistDataSource artistPersist;
	private List<Artist> artistsList;
	
	private ListView listView;
	private EditText searchEditText;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.followmain);
        
        artistPersist = new ArtistDataSource(this);
        artistPersist.open();
        
        listView = (ListView) findViewById(R.id.followAllListView);
		searchEditText = (EditText) findViewById(R.id.followSearchBox);
		
		searchEditText.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				if (s.toString().length() > 0) {
					artistsList = artistPersist.filterArtists(s.toString());
					listView.setAdapter(new iOSAdapter(GigFollowActivity.this, R.layout.ios_list, artistsList));
				}
				else {
					artistsList = artistPersist.getAllArtists();
					listView.setAdapter(new iOSAdapter(GigFollowActivity.this, R.layout.ios_list, artistsList));
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
				
			}

			public void onTextChanged(CharSequence s, int start, int before,int count) {
				
			}
		});
        
	}
	
	@Override
	protected void onResume() {
		
		setupButtons();
		artistPersist.open();
		if (searchEditText.getText().toString().length() > 0) {
			artistsList = artistPersist.filterArtists(searchEditText.getText().toString().toString());
		}
		else {
			artistsList = artistPersist.getAllArtists();
		}
		configureListView();
		super.onResume();
	}
	
	private void setupButtons() {
		GigstarActivity.getRightButton().setEnabled(false);
		GigstarActivity.getRightButton().setVisibility(Button.INVISIBLE);
		GigstarActivity.getLeftButton().setEnabled(false);
		GigstarActivity.getLeftButton().setVisibility(Button.INVISIBLE);
	}
	
	private void configureListView() {
		
		if (artistsList != null) {
			listView.setAdapter(new iOSAdapter(this, R.layout.ios_list, artistsList));
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Intent tmp = new Intent(GigFollowActivity.this, GigArtistDetails.class);
					tmp.putExtra("backActiv", getResources().getString(R.string.followTabName));
					tmp.putExtra("artist", artistsList.get(arg2));
					artistPersist.close();
					startActivity(tmp);
				}
			});
		}
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
			
			final ImageView iosPict = (ImageView) rowView.findViewById(R.id.ioslistpict);
			if (tmp.getPicture_url() != null) {
				iosPict.setVisibility(ImageView.VISIBLE);
			
				new FetchImageTask() { 
			        @Override
					protected void onPostExecute(Bitmap result) {
			            if (result != null) {
			            	iosPict.setImageBitmap(result);
			            }
			        }
			    }.execute(tmp.getPicture_url());
			}
			
			TextView listText = (TextView) rowView.findViewById(R.id.ioslisttext);
			listText.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/HelveticaNeueBold.ttf"));
			
			listText.setText(tmp.getName());
			
			return rowView;
		}
		
		private class FetchImageTask extends AsyncTask<String, Integer, Bitmap> {
		    @Override
		    protected Bitmap doInBackground(String... arg0) {
		    	Bitmap b = null;
		    	try {
					 b = BitmapFactory.decodeStream((InputStream) new URL(arg0[0]).getContent());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
		        return b;
		    }	
		}
	}
	
	@Override
	protected void onDestroy() {

		if (this.artistPersist != null) {
			this.artistPersist.close();
		}
		
		super.onDestroy();
	}
}

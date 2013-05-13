package org.ua.gigstar;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.ua.gigstar.detailsactivities.GigArtistDetails;
import org.ua.gigstar.entities.Artist;
import org.ua.gigstar.wsclients.GigArtistRepository;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

public class GigDiscoverActivity extends Activity {

	private ListView listView;
	private EditText searchEditText;
	private Button searchButton;
	
	private List<Artist> artistsList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.discovermain);

		listView = (ListView) findViewById(R.id.discoverResultsListView);
		searchEditText = (EditText) findViewById(R.id.searchEditText);
		searchButton = (Button) findViewById(R.id.searchButton);
		
		searchButton.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/HelveticaNeueBold.ttf"));

		searchEditText.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,int count) {
				
				if (searchEditText.getText().length() > 0) {
					searchButton.setEnabled(true);
				}
				else {
					searchButton.setEnabled(false);
				}
			}
		});
		
		searchButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				getResults(searchEditText.getText().toString());
			}
		});
	}
	
	@Override
	protected void onResume() {
		setupButtons();
		super.onResume();
	}
	
	private void setupButtons() {
		GigstarActivity.getRightButton().setEnabled(false);
		GigstarActivity.getRightButton().setVisibility(Button.INVISIBLE);
		GigstarActivity.getLeftButton().setEnabled(false);
		GigstarActivity.getLeftButton().setVisibility(Button.INVISIBLE);
	}
	
	private void getResults(final String name) {
		
		GigArtistRepository artistsFetcher = new GigArtistRepository();
		
		try {
			
			artistsList = artistsFetcher.findArtistWithQuery(name, 20);
			
			listView.setAdapter(new iOSAdapter(this, R.layout.ios_list, artistsList));
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Log.i("Gigstar", "Pressing on item: " + arg2);
					Intent tmp = new Intent(GigDiscoverActivity.this, GigArtistDetails.class);
					tmp.putExtra("backActiv", getResources().getString(R.string.discoverTabName));
					tmp.putExtra("artist", artistsList.get(arg2));
					startActivity(tmp);
				}
			});
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			
			ImageView iosPict = (ImageView) rowView.findViewById(R.id.ioslistpict);
			if (tmp.getPicture_url() != null) {
				iosPict.setVisibility(ImageView.GONE);
			}
			
			TextView listText = (TextView) rowView.findViewById(R.id.ioslisttext);
			listText.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/HelveticaNeueBold.ttf"));
			
			if (tmp.getPicture_url() != null) {
				// Load Image
			}
			
			listText.setText(tmp.getName());
			
			return rowView;
		}
	}
}

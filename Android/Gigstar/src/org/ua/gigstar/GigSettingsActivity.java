package org.ua.gigstar;

import org.ua.gigstar.detailsactivities.GigFacebookLogin;
import org.ua.gigstar.utils.GigCustomActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class GigSettingsActivity extends GigCustomActivity {
	
	private String[] socialNetworks = {"Facebook","Twitter"};
	
	private ListView socialNetListView;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.settingsmain, getResources().getString(R.string.settingsTabName));
        
        if (this.getIntent().getStringExtra("backActiv") != null) {
        	getLeftButton().setText(this.getIntent().getStringExtra("backActiv"));
        }
        else getLeftButton().setText(getResources().getString(R.string.moreTabName));
        getLeftButton().setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		});
        getLeftButton().setEnabled(true);
        getLeftButton().setVisibility(Button.VISIBLE);
        
        socialNetListView = (ListView) findViewById(R.id.socialNetworkListView);
        socialNetListView.setAdapter(new iOSAdapter(this, R.layout.ios_list, socialNetworks));
        socialNetListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				
				switch (arg2) {
					case 0: {
						Intent tmp = new Intent(GigSettingsActivity .this, GigFacebookLogin.class);
						tmp.putExtra("backActiv", getResources().getString(R.string.settingsTabName));
						startActivity(tmp);
						break;
					}
				}
				
			}
		});
        
	}
	
	protected class iOSAdapter extends ArrayAdapter<String> {
		
		
		private final Activity con;
		private final String[] list;
		
		private int resource;
		
		public iOSAdapter(Activity context, int resource, String[] objects) {
			super(context, resource, objects);
			
			this.con = context;
			this.list = objects;
			this.resource = resource;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			LayoutInflater inflater = this.con.getLayoutInflater();
			View rowView = inflater.inflate(resource, null, true);
			
			if (list.length == 1) {
				rowView.setBackgroundDrawable(getResources().getDrawable(R.drawable.ios_list_single_row));
			}
			else {
				if (position == 0) {
					rowView.setBackgroundDrawable(getResources().getDrawable(R.drawable.ios_list_top_row));
				}
				else if (position == (list.length-1)) {
					rowView.setBackgroundDrawable(getResources().getDrawable(R.drawable.ios_list_bottom_row));
				}
				else {
					rowView.setBackgroundDrawable(getResources().getDrawable(R.drawable.ios_list_med_row));
				}
			}
			
			TextView listText = (TextView) rowView.findViewById(R.id.ioslisttext);
			listText.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/HelveticaNeueBold.ttf"));
			
			listText.setText(list[position]);
			
			return rowView;
		}
	}
	
	

}

package org.ua.gigstar;

import java.util.List;

import org.ua.gigstar.detailsactivities.GigEventDetails;
import org.ua.gigstar.entities.Event;
import org.ua.gigstar.utils.EventDataSource;

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

public class GigScheduleActivity extends Activity {
	
	private EventDataSource eventsPersist;
	
	private ListView listView;
	
	private List<Event> eventsList;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedulemain);
        
        eventsPersist = new EventDataSource(this);
        
        listView = (ListView) findViewById(R.id.scheduleListView);
		
		GigstarActivity.getRightButton().setEnabled(false);
		GigstarActivity.getRightButton().setVisibility(Button.INVISIBLE);
		GigstarActivity.getLeftButton().setEnabled(false);
		GigstarActivity.getLeftButton().setVisibility(Button.INVISIBLE);
	}
	
	@Override
	protected void onResume() {
		eventsPersist.open();
		eventsList = eventsPersist.getAllEvents();
		configureListView();
		eventsPersist.close();
		setupButtons();
		super.onResume();
	}
	
	private void setupButtons() {
		GigstarActivity.getRightButton().setEnabled(false);
		GigstarActivity.getRightButton().setVisibility(Button.INVISIBLE);
		GigstarActivity.getLeftButton().setEnabled(false);
		GigstarActivity.getLeftButton().setVisibility(Button.INVISIBLE);
	}
	
	private void configureListView() {
		
		if (eventsList != null) {
			listView.setAdapter(new iOSAdapter(this, R.layout.ios_list, eventsList));
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Intent tmp = new Intent(GigScheduleActivity .this, GigEventDetails.class);
					tmp.putExtra("backActiv", getResources().getString(R.string.scheduleTabName));
					tmp.putExtra("event", eventsList.get(arg2));
					startActivity(tmp);
				}
			});
		}
	}
	
	protected class iOSAdapter extends ArrayAdapter<Event> {
		
		
		private final Activity con;
		private final List<Event> list;
		
		private int resource;
		
		public iOSAdapter(Activity context, int resource, List<Event> objects) {
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
			
			Event tmp = this.list.get(position);
			
			TextView listText = (TextView) rowView.findViewById(R.id.ioslisttext);
			listText.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/HelveticaNeueBold.ttf"));
			
			listText.setText(tmp.getName());
			
			return rowView;
		}
	}
	
	@Override
	protected void onDestroy() {

		if (this.eventsPersist != null) {
			this.eventsPersist.close();
		}
		
		super.onDestroy();
	}

}

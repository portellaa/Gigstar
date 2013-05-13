package org.ua.gigstar;

import java.util.ArrayList;
import java.util.List;

import org.ua.gigstar.utils.TabClass;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class GigMoreTabsActivity extends ListActivity {
	
	private List<TabClass> moreTabs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.moretabs);
		
		GigstarActivity.getRightButton().setEnabled(false);
		GigstarActivity.getRightButton().setVisibility(Button.INVISIBLE);
		GigstarActivity.getLeftButton().setEnabled(false);
		GigstarActivity.getLeftButton().setVisibility(Button.INVISIBLE);
		
		setTitle(getResources().getString(R.string.moreTabName));
		
		moreTabs = new ArrayList<TabClass>();
		
		moreTabs.add(new TabClass(R.drawable.about_unselected, "About", GigAboutActivity.class));
		moreTabs.add(new TabClass(R.drawable.settings_unselected, "Settings", GigSettingsActivity.class));
		
		setListAdapter(new MoreTabsAdaper(this, moreTabs));
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
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		startActivity(new Intent(GigMoreTabsActivity.this, this.moreTabs.get(position).getActivToStart()));
	}
	
	private class MoreTabsAdaper extends ArrayAdapter<TabClass> {
		
		private Activity con;
		private List<TabClass> items;
		
		public MoreTabsAdaper(Activity context, List<TabClass> moreTabs) {
			super (context, R.layout.moretabsitem, moreTabs);
			
			this.con = context;
			this.items = moreTabs;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			LayoutInflater inflater = this.con.getLayoutInflater();
			View rowView = inflater.inflate(R.layout.moretabsitem, null, true);
			
			ImageView image = (ImageView) rowView.findViewById(R.id.tabImage);
			TextView text = (TextView) rowView.findViewById(R.id.tabText);
			
			image.setImageResource(items.get(position).getImageID());
			text.setText(items.get(position).getName());
			
			return rowView;
			
		}
		
		
	}

}

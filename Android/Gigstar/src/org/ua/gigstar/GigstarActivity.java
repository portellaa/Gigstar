package org.ua.gigstar;

import java.util.List;

import org.ua.gigstar.utils.TabClass;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class GigstarActivity extends TabActivity implements OnTabChangeListener {

	private static TextView title;
	private static Button leftButton;
	private static Button rightButton;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.main);
 
        if (customTitleSupported) {
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);
            title = (TextView) findViewById(R.id.title);
            title.setText("Gigstar");
            title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeueBold.ttf"));
            
            leftButton = (Button) findViewById(R.id.backButton);
            leftButton.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/HelveticaNeueBold.ttf"));
            rightButton = (Button) findViewById(R.id.rightButton);
            rightButton.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/HelveticaNeueBold.ttf"));
        }
        
        TabHost tabHost = getTabHost();
        
        tabHost.setOnTabChangedListener(this);
        
        createTab(tabHost, R.string.mapTabName, R.drawable.tab_map, GigMapActivity.class);
        createTab(tabHost, R.string.discoverTabName, R.drawable.tab_discover, GigDiscoverActivity.class);
        createTab(tabHost, R.string.followTabName, R.drawable.tab_follow, GigFollowActivity.class);
        createTab(tabHost, R.string.scheduleTabName, R.drawable.tab_schedule, GigScheduleActivity.class);
        createTab(tabHost, R.string.moreTabName, R.drawable.tab_more, GigMoreTabsActivity.class);
        
        tabHost.setCurrentTab(0);
    }
    
    private void createTab(TabHost tabHost, int nameID, int tabID, Class<?> activ){
    	
    	String name = getResources().getString(nameID);
        TabHost.TabSpec tabSpec = tabHost.newTabSpec(name);
        
        View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);
		TextView title = (TextView) tabIndicator.findViewById(R.id.title);
		title.setText(name);
		title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeueBold.ttf"));
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
		icon.setImageResource(tabID);

    	tabSpec.setIndicator(tabIndicator);
    	tabSpec.setContent(new Intent(this, activ));
    	
    	tabHost.addTab(tabSpec);
    }
    
	public void onTabChanged(String tabId) {
		
		Log.i("Gigstar", "TabChanged: " + tabId);
		setTitle(tabId);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	switch(item.getItemId()) {
    	
    		case R.id.about_item: {
    			Intent tmp = new Intent(this, GigAboutActivity.class);
    			tmp.putExtra("backActiv", getTabHost().getCurrentTabTag());
    			startActivity(tmp);
    			break;
    		}
    		case R.id.settings_item: {
    			Intent tmp = new Intent(this, GigSettingsActivity.class);
    			tmp.putExtra("backActiv", getTabHost().getCurrentTabTag());
    			startActivity(tmp);
    			break;
    		}
    		default : {
    			return super.onOptionsItemSelected(item);
    		}
    	}
    	
        return true;
    }
	
	@Override
	public void setTitle(CharSequence newTitle) {
		title.setText(newTitle);
		title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeueBold.ttf"));
	}
	
	public static void updateTitle(String newTitle) {
		title.setText(newTitle);
	}
	
	public static Button getLeftButton() {
		return leftButton;
	}
	
	public static Button getRightButton() {
		return rightButton;
	}
	
	public static TextView getTitleTextView() {
		return title;
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
package org.ua.gigstar.utils;

import org.ua.gigstar.R;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public abstract class GigCustomActivity extends Activity {
	
	private static TextView mainTitle;
	private static Button leftButton;
	private static Button rightButton;
	
	public void onCreate(Bundle savedInstanceState, int viewID, String title) {
        super.onCreate(savedInstanceState);
        
        Boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(viewID);
        
        if (customTitleSupported) {
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);
            mainTitle = (TextView) findViewById(R.id.title);
            mainTitle.setText("Gigstar");
            mainTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeueBold.ttf"));
            
            leftButton = (Button) findViewById(R.id.backButton);
            leftButton.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/HelveticaNeueBold.ttf"));
            rightButton = (Button) findViewById(R.id.rightButton);
            rightButton.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/HelveticaNeueBold.ttf"));
        }
        setTitle(title);
	}
	
	@Override
	public void setTitle(CharSequence newTitle) {
		mainTitle.setText(newTitle);
		mainTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeueBold.ttf"));
	}
	
	public static void updateTitle(String newTitle) {
		mainTitle.setText(newTitle);
	}
	
	public static Button getLeftButton() {
		return leftButton;
	}
	
	public static Button getRightButton() {
		return rightButton;
	}
	
	public static TextView getTitleTextView() {
		return mainTitle;
	}

}

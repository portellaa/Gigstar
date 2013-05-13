package org.ua.gigstar.detailsactivities;

import org.ua.gigstar.R;
import org.ua.gigstar.utils.GigCustomActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class GigFacebookLogin extends GigCustomActivity {
	
	Facebook facebook = new Facebook("ga0RGNYHvNM5d0SLGQfpQWAPGJ8=");
	private SharedPreferences gigPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.facebookmain, "Facebook");

        getLeftButton().setText(getResources().getString(R.string.settingsTabName));
        getLeftButton().setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		});
        getLeftButton().setEnabled(true);
        getLeftButton().setVisibility(Button.VISIBLE);
        
        gigPrefs = getPreferences(MODE_PRIVATE);
        
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
	            	Log.i(GigFacebookLogin.class.getName() + " Complete", values.toString());
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
    
    @Override
    protected void onResume() {
    	super.onResume();
    	facebook.extendAccessTokenIfNeeded(this, null);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }

}

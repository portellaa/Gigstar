package org.ua.gigstar.detailsactivities;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.ua.gigstar.R;
import org.ua.gigstar.entities.Artist;
import org.ua.gigstar.utils.ArtistDataSource;
import org.ua.gigstar.utils.GigCustomActivity;
import org.ua.gigstar.wsclients.GigArtistRepository;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GigArtistDetails extends GigCustomActivity {
	
	private TextView artistName, artistBio;
	private ImageView artistImage;
	
	private ArtistDataSource artistPersist;
	private GigArtistRepository artistFetch;
	
	private Artist currentArtist = null;
	private boolean following = false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.artistdetails, getResources().getString(R.string.artistDetailsTitle));
        
        if ((currentArtist = (Artist) this.getIntent().getSerializableExtra("artist")) != null) {
        	artistPersist = new ArtistDataSource(this);
//        	artistPersist.open();
        	artistFetch = new GigArtistRepository();
        	
        	getRightButton().setText(getResources().getString(R.string.follow));
        	
        	if ((currentArtist = requetsArtist(currentArtist.getMbid())) != null) {
        		getRightButton().setVisibility(Button.VISIBLE);
        		getRightButton().setEnabled(true);
            	
        		getRightButton().setOnClickListener(new View.OnClickListener() {
    				
    				public void onClick(View v) {
    					swapFollow();
    				}
    			});
	        	mapObjects();
	        	fillData(currentArtist);
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
        else this.finish();
	}
	
	private void mapObjects() {
		artistName = (TextView) findViewById(R.id.artistName);
		artistBio = (TextView) findViewById(R.id.artistBio);
		
		artistImage = (ImageView) findViewById(R.id.artistPic);
	}
	
	private Artist requetsArtist(String mbid) {
		
		Artist artist = null;
		
		artistPersist.open();
		if ((artist = artistPersist.getArtistByMBID(mbid)) == null) {
			
			try {
				artist = artistFetch.getArtistInformation(mbid);
				if (artist != null) {
					artist.setMbid(mbid);
					this.following = false;
					Log.i("Gigstar","artist information fetched... " + artist.toString());
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			this.following = true;
			getRightButton().setText(getResources().getString(R.string.unfollow));
		}
		artistPersist.close();
		
		return artist;
	}
	
	private void fillData(Artist e) {
		
		if (e != null) {
			Log.i("Gigstar", e.toString());

			new FetchImageTask() { 
		        @Override
				protected void onPostExecute(Bitmap result) {
		            if (result != null) {
		            	artistImage.setImageBitmap(result);
		            }
		        }
		    }.execute(e.getPicture_url());
			
			artistName.setText(currentArtist.getName());
			artistBio.setText(currentArtist.getBiography());
		}
		else finish();			
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
	
	private void swapFollow() {
		
		artistPersist.open();
		if (following) {
			artistPersist.deleteArtist(this.currentArtist);
			getRightButton().setText(getResources().getString(R.string.follow));
			this.following = false;
		}
		else {
			this.currentArtist = artistPersist.createArtist(currentArtist);
			getRightButton().setText(getResources().getString(R.string.unfollow));
			this.following = true;
		}
		artistPersist.close();
	}
	
	@Override
	protected void onDestroy() {

		if (this.artistPersist != null) {
			this.artistPersist.close();
		}
		
		super.onDestroy();
	}
	
}

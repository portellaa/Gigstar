package org.ua.gigstar.utils;

import java.util.ArrayList;
import java.util.List;

import org.ua.gigstar.R;
import org.ua.gigstar.detailsactivities.GigEventDetails;
import org.ua.gigstar.entities.Event;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class GigOverlayItem extends ItemizedOverlay<OverlayItem> {
	
	private static final long BALLOON_INFLATION_TIME = 300;
	private static Handler handler = new Handler();

	private ArrayList<OverlayItem> mapEventsOverlays = new ArrayList<OverlayItem>();
	private ArrayList<Event> events = new ArrayList<Event>();
	
	private Activity mainActivity;
	private GigMapView mapView;
	private MapController mapController;
	private View clickRegion;
	private GigOverlayItemView ballonView;
	
	private int bottomOffset;
	private int selectedIndex;
	private OverlayItem selectedOverlay;
	
	private static boolean isInflating = false;
	
	public GigOverlayItem(Drawable defaultMarker, Context context, GigMapView mapView, Activity oldActiv) {
		super(boundCenterBottom(defaultMarker));

		this.mapView = mapView;
		this.mapController = this.mapView.getController();
		this.mainActivity = oldActiv;
	}
	
	public void addOverlay(OverlayItem overlay, Event e) {
	    mapEventsOverlays.add(overlay);
	    populate();
	    events.add(e);
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		return mapEventsOverlays.get(i);
	}

	@Override
	public int size() {
		return mapEventsOverlays.size();
	}
	
	public void clear() {
		mapEventsOverlays.clear();
		populate();
		mapEventsOverlays = new ArrayList<OverlayItem>();
	}
	
	public void setBalloonBottomOffset(int pixels) {
		bottomOffset = pixels;
	}
	public int getBalloonBottomOffset() {
		return bottomOffset;
	}
	
	protected boolean onBalloonTap(int index, OverlayItem item) {
		return false;
	}
	
	protected void onBalloonOpen(int index) {}

	@Override
	public final boolean onTap(int index) {
		
		handler.removeCallbacks(finishBalloonInflation);
		isInflating = true;
		handler.postDelayed(finishBalloonInflation, BALLOON_INFLATION_TIME);
		
		selectedIndex = index;
		selectedOverlay = createItem(index);
		setLastFocusedIndex(index);
		
		onBalloonOpen(index);
		createAndDisplayBalloonOverlay();
		
		animateTo(index, selectedOverlay.getPoint());
		
		return true;
	}

	protected void animateTo(int index, GeoPoint center) {
		mapController.animateTo(center);
	}
	
	public void hideBalloon() {
		if (ballonView != null) {
			ballonView.setVisibility(View.GONE);
		}
		selectedOverlay = null;
	}
	
	private void hideOtherBalloons(List<Overlay> overlays) {
		
		for (Overlay overlay : overlays) {
			if (overlay instanceof GigOverlayItem && overlay != this) {
				((GigOverlayItem) overlay).hideBalloon();
			}
		}
		
	}
	
	public void hideAllBalloons() {
		if (!isInflating) {
			List<Overlay> mapOverlays = mapView.getOverlays();
			if (mapOverlays.size() > 1) {
				hideOtherBalloons(mapOverlays);
			}
			hideBalloon();
		}
	}
	
	private OnTouchListener createBalloonTouchListener() {
		return new OnTouchListener() {
			
			float startX;
			float startY;
			
			public boolean onTouch(View v, MotionEvent event) {
				
				View l =  ((View) v.getParent()).findViewById(R.id.balloon_main_layout);
				Drawable d = l.getBackground();
				
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					int[] states = {android.R.attr.state_pressed};
					if (d.setState(states)) {
						d.invalidateSelf();
					}
					startX = event.getX();
					startY = event.getY();
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					int newStates[] = {};
					if (d.setState(newStates)) {
						d.invalidateSelf();
					}
					if (Math.abs(startX - event.getX()) < 40 && 
							Math.abs(startY - event.getY()) < 40 ) {
						// call overridden method
						onBalloonTap(selectedIndex, selectedOverlay);
					}
					return true;
				} else {
					return false;
				}
				
			}
		};
	}
	
	@Override
	public OverlayItem getFocus() {
		return selectedOverlay;
	}

	@Override
	public void setFocus(OverlayItem item) {
		super.setFocus(item);	
		selectedIndex = getLastFocusedIndex();
		selectedOverlay = item;
		if (selectedOverlay == null) {
			hideBalloon();
		} else {
			createAndDisplayBalloonOverlay();
		}	
	}
	
	private boolean createAndDisplayBalloonOverlay(){
		boolean isRecycled;
		
		if (ballonView == null) {
			ballonView = new GigOverlayItemView(mapView.getContext(), bottomOffset);
			clickRegion = ballonView.findViewById(R.id.balloon_inner_layout);
			clickRegion.setOnTouchListener(createBalloonTouchListener());

			View v = ballonView.findViewById(R.id.balloon_disclosure);
			if (v != null) {
				v.setVisibility(View.VISIBLE);
				v.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						hideBalloon();
						Intent i = new Intent(mapView.getContext(), GigEventDetails.class);
						i.putExtra("event", events.get(selectedIndex));
						i.putExtra("backActiv", mainActivity.getResources().getString(R.string.mapsActName));
						mainActivity.startActivity(i);
					}
				});
			}
			isRecycled = false;
		} else {
			isRecycled = true;
		}
	
		ballonView.setVisibility(View.GONE);
		
		List<Overlay> mapOverlays = mapView.getOverlays();
		if (mapOverlays.size() > 1) {
			hideOtherBalloons(mapOverlays);
		}
		
		if (selectedOverlay != null)
			ballonView.setData(selectedOverlay);
		
		GeoPoint point = selectedOverlay.getPoint();
		MapView.LayoutParams params = new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, point,
				MapView.LayoutParams.BOTTOM_CENTER);
		params.mode = MapView.LayoutParams.MODE_MAP;
		
		ballonView.setVisibility(View.VISIBLE);
		
		if (isRecycled) {
			ballonView.setLayoutParams(params);
		} else {
			mapView.addView(ballonView, params);
		}
		
		return isRecycled;
	}

	public static boolean isInflating() {
		return isInflating;
	}
	
	private static Runnable finishBalloonInflation = new Runnable() {
		public void run() {
			isInflating = false;
		}
	};

}

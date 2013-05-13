package org.ua.gigstar.utils;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class GigMapView extends MapView {
	
	private final long mEventsTimeout = 50L;
	
	public interface OnChangeListener {
		public void onChange(MapView view, GeoPoint newCenter, GeoPoint oldCenter, int newZoom, int oldZoom);
	}

	private boolean touched = false;
	private GeoPoint lastCenterPosition;
	private int lastZoomLevel;
	private Timer delayTimer;
	private GigMapView.OnChangeListener changeListener = null;

	public GigMapView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		lastCenterPosition = this.getMapCenter();
		lastZoomLevel = this.getZoomLevel();
		delayTimer = new Timer();
	}
	
	public void setOnChangeListener(GigMapView.OnChangeListener newChangeListenter)
	{
		changeListener = newChangeListenter;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{		
		touched = (ev.getAction() != MotionEvent.ACTION_UP);
		return super.onTouchEvent(ev);
	}

	@Override
	public void computeScroll()
	{
		super.computeScroll();

		if (isSpanChanged() || isZoomChanged())
		{
			resetMapChangeTimer();
		}
	}
	
	private void resetMapChangeTimer()
	{
		delayTimer.cancel();
		delayTimer = new Timer();
		delayTimer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				if (changeListener != null)
					changeListener.onChange(GigMapView.this, getMapCenter(), lastCenterPosition, getZoomLevel(), lastZoomLevel);
				lastCenterPosition = getMapCenter();
				lastZoomLevel = getZoomLevel();
			}
		}, mEventsTimeout);
	}
	
	private boolean isSpanChanged()
	{
		return !touched && !getMapCenter().equals(lastCenterPosition);
	}

	private boolean isZoomChanged()
	{
		return (getZoomLevel() != lastZoomLevel);
	}

}

package org.ua.gigstar.utils;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class GigMyOverlay extends ItemizedOverlay<OverlayItem> {

	private OverlayItem myPosition = null;
	
	public GigMyOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}
	
	public void addOverlay(OverlayItem overlay) {
	    myPosition = overlay;
	    populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return myPosition;
	}

	@Override
	public int size() {
		return 1;
	}
}

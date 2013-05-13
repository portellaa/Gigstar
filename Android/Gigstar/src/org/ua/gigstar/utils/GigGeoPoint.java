package org.ua.gigstar.utils;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.maps.GeoPoint;

public class GigGeoPoint implements Parcelable, Serializable {

	private static final long serialVersionUID = 1L;

	private int latitudeE6, longitudeE6;

	public GigGeoPoint(GeoPoint point) {
		this.latitudeE6 = point.getLatitudeE6();
		this.longitudeE6 = point.getLongitudeE6();
	}
	
	public GigGeoPoint(int latitudeE6, int longitudeE6) {
		this.latitudeE6 = latitudeE6;
		this.longitudeE6 = longitudeE6;
	}

	public GeoPoint getGeoPoint() {
		return new GeoPoint(latitudeE6, longitudeE6);
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(latitudeE6);
		out.writeInt(longitudeE6);
	}

	public static final Parcelable.Creator<GigGeoPoint> CREATOR = new Parcelable.Creator<GigGeoPoint>() {
		public GigGeoPoint createFromParcel(Parcel in) {
			return new GigGeoPoint(in);
         }
		
		public GigGeoPoint[] newArray(int size) {
			return new GigGeoPoint[size];
		}
	};

 	private GigGeoPoint(Parcel in) {
 		latitudeE6 = in.readInt();
 		longitudeE6 = in.readInt();
 	}
}

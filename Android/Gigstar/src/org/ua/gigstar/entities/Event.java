package org.ua.gigstar.entities;

import java.io.Serializable;
import java.util.List;

import org.ua.gigstar.utils.GigGeoPoint;

public class Event implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long id;
	private String eventID = null;
	private String name = null;
	private String type = null;	//can be converted to an enumerator
	private String uri = null;
	
	// can be converted to datetime
	private String startDate = null;
	private String endDate = null;
	private String startTime = null;
	private String endTime = null;
	
	private GigGeoPoint location = null;
	private String locationName = null;
	
	private List<Artist> performers = null;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEventID() {
		return eventID;
	}

	public void setEventID(String eventID) {
		this.eventID = eventID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public GigGeoPoint getLocation() {
		return location;
	}

	public void setLocation(GigGeoPoint location) {
		this.location = location;
	}

	public List<Artist> getPerformers() {
		return performers;
	}

	public void setPerformers(List<Artist> performers) {
		this.performers = performers;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	@Override
	public String toString() {
		return "Event [eventID=" + eventID + ", name=" + name + ", type="
				+ type + ", uri=" + uri + ", startDate=" + startDate
				+ ", endDate=" + endDate + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", location=" + location
				+ ", performers=" + performers + "]";
	}
	
	
}

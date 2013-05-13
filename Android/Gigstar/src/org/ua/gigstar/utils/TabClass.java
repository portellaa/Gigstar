package org.ua.gigstar.utils;

public class TabClass {

	private int imageID;
	private String name;
	
	private Class<?> activToStart;

	public TabClass(int imageID, String name, Class<?> activToStart) {
		super();
		this.imageID = imageID;
		this.name = name;
		this.activToStart = activToStart;
	}

	public int getImageID() {
		return imageID;
	}

	public void setImageID(int imageID) {
		this.imageID = imageID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?> getActivToStart() {
		return activToStart;
	}

	public void setActivToStart(Class<?> activToStart) {
		this.activToStart = activToStart;
	}

//	public T getActivToStart() {
//		return activToStart;
//	}
//
//	public void setActivToStart(T activToStart) {
//		this.activToStart = activToStart;
//	}
//	
	
	
}

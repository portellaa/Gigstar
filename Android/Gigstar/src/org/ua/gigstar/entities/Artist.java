package org.ua.gigstar.entities;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Artist implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private long id;
	
	private String mbid, name, biography, picture_url, url;
	private String[] similar;
	private List<String> tags;
	/**
	 * @return the mbid
	 */
	public String getMbid() {
		return mbid;
	}
	/**
	 * @param mbid the mbid to set
	 */
	public void setMbid(String mbid) {
		this.mbid = mbid;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the biography
	 */
	public String getBiography() {
		return biography;
	}
	/**
	 * @param biography the biography to set
	 */
	public void setBiography(String biography) {
		this.biography = biography;
	}
	/**
	 * @return the picture_url
	 */
	public String getPicture_url() {
		return picture_url;
	}
	/**
	 * @param picture_url the picture_url to set
	 */
	public void setPicture_url(String picture_url) {
		this.picture_url = picture_url;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return the tags
	 */
	public List<String> getTags() {
		return tags;
	}
	/**
	 * @param tags the tags to set
	 */
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	/**
	 * @return the similar
	 */
	public String[] getSimilar() {
		return similar;
	}
	/**
	 * @param similar the similar to set
	 */
	public void setSimilar(String[] similar) {
		this.similar = similar;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "Artist [id=" + id + ", mbid=" + mbid + ", name=" + name
				+ ", biography=" + biography + ", picture_url=" + picture_url
				+ ", url=" + url + ", similar=" + Arrays.toString(similar)
				+ ", tags=" + tags + "]";
	}

}

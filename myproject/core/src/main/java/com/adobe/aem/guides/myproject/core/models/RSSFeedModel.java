package com.adobe.aem.guides.myproject.core.models;

import java.time.LocalDateTime;

/**
 * @author bchauhanu
 * POJO class for a Single RSS Feed
 */
public class RSSFeedModel {
	
	private String title;
	private String description;
	private String link;
	private String publishDate;
	private LocalDateTime feedDate;

	//Getters & Setters for Feed Properties
    public String getTitle() {
    	return title;
    }
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}
	public LocalDateTime getFeedDate() {
		return feedDate;
	}
	public void setFeedDate(LocalDateTime feedDate) {
		this.feedDate = feedDate;
	}
}

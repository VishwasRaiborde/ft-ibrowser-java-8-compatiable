package com.cloudsherpas.request;

public class NewsRequest {
	public String key;
	public String title;
	public String date;
	public String description;
	
	public String sortField;
	public String order;
	public String step;

	public NewsRequest() {
	}

	public NewsRequest(String key, String title, String date, String description,String sortField,String order,String step) {
		this.key = key;
		this.title = title;
		this.date = date;
		this.description = description;
		this.sortField = sortField;
		this.order = order ;
		this.step = step;
	}
}

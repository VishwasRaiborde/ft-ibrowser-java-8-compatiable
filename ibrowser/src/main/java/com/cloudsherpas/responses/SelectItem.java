package com.cloudsherpas.responses;

public class SelectItem {
	private String key;
	private String name;
	
	public SelectItem () {
	}

	public SelectItem(String key, String name) {
		this.key = key;
		this.name = name;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return this.key!=null && this.key.equals(((SelectItem)obj).getKey());
	}

	
	
}

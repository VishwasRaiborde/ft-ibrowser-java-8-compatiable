package com.cloudsherpas.model;

public class IPRangeItem {
	private String lowerRange;
	private String higherRange;
	
	public IPRangeItem(String lr, String hr) {
		this.lowerRange = lr;
		this.higherRange = hr;
	}
	
	public String getLowerRange() {
		return lowerRange;
	}
	public void setLowerRange(String lowerRange) {
		this.lowerRange = lowerRange;
	}
	public String getHigherRange() {
		return higherRange;
	}
	public void setHigherRange(String higherRange) {
		this.higherRange = higherRange;
	}
	
	

}

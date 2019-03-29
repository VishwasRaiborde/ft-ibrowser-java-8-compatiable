package com.cloudsherpas.model;

import java.util.ArrayList;
import java.util.List;

import com.cloudsherpas.domain.Instance;
import com.cloudsherpas.enums.FrequencyEnum;

public class ReportHistory {
	private FrequencyEnum frequency;
	private List<Instance> histories = new ArrayList<Instance>();
	private Instance today;
	private Instance lastWeek;
	private Instance lastTradingPeriod;
	private Instance lastHalf;
	private Instance lastYear;
	
	private List<Instance> tradingPeriods = new ArrayList<Instance>();
	private List<Instance> halfs = new ArrayList<Instance>();
	private List<Instance> years = new ArrayList<Instance>();
	
	
	public FrequencyEnum getFrequency() {
		return frequency;
	}
	public void setFrequency(FrequencyEnum frequency) {
		this.frequency = frequency;
	}
	public List<Instance> getHistories() {
		return histories;
	}
	public void setHistories(List<Instance> histories) {
		this.histories = histories != null ? histories : new ArrayList<Instance>();
	}
	
	public Instance getToday() {
		return today;
	}
	public void setToday(Instance today) {
		this.today = today;
	}
	public Instance getLastWeek() {
		return lastWeek;
	}
	public void setLastWeek(Instance lastWeek) {
		this.lastWeek = lastWeek;
	}
	public Instance getLastTradingPeriod() {
		return lastTradingPeriod;
	}
	public void setLastTradingPeriod(Instance lastTradingPeriod) {
		this.lastTradingPeriod = lastTradingPeriod;
	}
	public Instance getLastHalf() {
		return lastHalf;
	}
	public void setLastHalf(Instance lastHalf) {
		this.lastHalf = lastHalf;
	}
	public Instance getLastYear() {
		return lastYear;
	}
	public void setLastYear(Instance lastYear) {
		this.lastYear = lastYear;
	}
	public List<Instance> getTradingPeriods() {
		return tradingPeriods;
	}
	public void setTradingPeriods(List<Instance> tradingPeriods) {
		this.tradingPeriods = tradingPeriods != null ? tradingPeriods : new ArrayList<Instance>();
	}
	public List<Instance> getHalfs() {
		return halfs;
	}
	public void setHalfs(List<Instance> halfs) {
		this.halfs = halfs != null ? halfs : new ArrayList<Instance>();
	}
	public List<Instance> getYears() {
		return years;
	}
	public void setYears(List<Instance> years) {
		this.years = years != null ? years : new ArrayList<Instance>();
	}
	
	
}

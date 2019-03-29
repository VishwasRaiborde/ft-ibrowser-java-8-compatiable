package com.cloudsherpas.enums;

public enum DeletionPeriodEnum {
	DAY_1("DAY_1", "1 day"),
	WEEK_1("WEEK_1", "1 week"),
	WEEK_2("WEEK_2", "2 weeks"),
	WEEK_3("WEEK_3", "3 weeks"),
	MONTH_1("MONTH_1", "1 month"),
	MONTH_2("MONTH_2", "2 months"),
	MONTH_3("MONTH_3", "3 months");

	private String code;
	private String name;
	
	private DeletionPeriodEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	
	public static DeletionPeriodEnum buildByCode(String code) {
		
		if("DAY_1".equals(code)){
			return DAY_1;
		}else if("WEEK_1".equals(code)){
			return WEEK_1;
		}else if("WEEK_2".equals(code)){
			return WEEK_2;
		}else if("WEEK_3".equals(code)){
			return WEEK_3;
		}else if ("MONTH_1".equals(code)) {
			return MONTH_1;
		} else if ("MONTH_2".equals(code)) {
			return MONTH_2;
		} else if ("MONTH_3".equals(code)) {
			return MONTH_3;
		} else {
			throw new IllegalArgumentException("Deletion period not found");
		}
	}
}

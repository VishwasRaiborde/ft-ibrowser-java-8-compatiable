package com.cloudsherpas.enums;

public enum AuditEnum {
	WEEKLY("WEEKLY", "This weeek"),
	YEARLY("YEARLY", "This year"),
	ALL_TIME("ALL_TIME", "All time");

	private String code;
	private String name;
	
	private AuditEnum(String code , String name){
		this.code = code;
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	
	public static AuditEnum buildByCode(String code) {
		if ("WEEKLY".equals(code)) {
			return WEEKLY;
		} else if ("YEARLY".equals(code)) {
			return YEARLY;
		} else if ("ALL_TIME".equals(code)) {
			return ALL_TIME;
		} else {
			throw new IllegalArgumentException("Deletion period not found");
		}
	}
	
}

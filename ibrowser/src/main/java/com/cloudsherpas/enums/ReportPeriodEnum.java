package com.cloudsherpas.enums;

public enum ReportPeriodEnum {
	DAILY("DAILY", "Daily"),
	WEEKLY("WEEKLY", "Weekly"),
	END_OF_TRADING_PERIOD("END_OF_TRADING_PERIOD", "End of Trading Period"),
	END_OF_HALF("END_OF_HALF", "End of Half"),
	END_OF_YEAR("END_OF_YEAR", "End of Year");
	
	private String code;
	private String name;
	
	private ReportPeriodEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
	
}

package com.cloudsherpas.enums;

public enum EntityTransferStatus {
	READY_TO_TRANSFER("READY_TO_TRANSFER"),
	TRANSFER_COMPLETED("TRANSFER_COMPLETED");
	
	private String code;
	
	private EntityTransferStatus(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
}
